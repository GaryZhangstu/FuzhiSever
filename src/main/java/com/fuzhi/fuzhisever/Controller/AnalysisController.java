package com.fuzhi.fuzhisever.Controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuzhi.fuzhisever.DTO.HistoryDTO;
import com.fuzhi.fuzhisever.Model.SkinAnalysis;
import com.fuzhi.fuzhisever.Model.User;
import com.fuzhi.fuzhisever.Repository.UserRepository;
import com.fuzhi.fuzhisever.Service.CommunicationService;
import com.fuzhi.fuzhisever.Service.SkinAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
public class AnalysisController {
    private final CommunicationService communicationService;
    private final UserRepository userRepository;
    private final SkinAnalysisService skinAnalysisService;
    private final ObjectMapper objectMapper;

    @GetMapping("/facialReport")
    @SaCheckLogin
    public ResponseEntity<SaResult> getFacialReport(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(SaResult.error("上传的文件不能为空"));
        }

        try {
            String userId = StpUtil.getLoginId().toString();
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isEmpty()) {
                return new ResponseEntity<>(SaResult.error("用户不存在"), HttpStatus.NOT_FOUND);
            }


            User user = optionalUser.get();
            UUID uuid = UUID.randomUUID();

            String key ="facialAnalysis/" + userId + "/" + uuid+file.getOriginalFilename();

            communicationService.uploadFileToS3(file.getResource().getFile(), key);
            SkinAnalysis skinAnalysis =skinAnalysisService.saveSkinAnalysisData(communicationService.getFacialReport(file.getResource().getFile()), key);
            user.getSkinAnalysisList().add(skinAnalysis);

            userRepository.save(user);

            return ResponseEntity.ok(SaResult.ok("头像上传成功"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(SaResult.error("文件上传失败: " + e.getMessage()));
        }
    }

    @GetMapping("/getSkinAnalysisHistory")
    @SaCheckLogin
    public ResponseEntity<SaResult> getSkinAnalysisHistory() {
        String userId = StpUtil.getLoginId().toString();
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(SaResult.error("用户不存在"), HttpStatus.NOT_FOUND);
        }
        User user = optionalUser.get();
        List<SkinAnalysis> skinAnalysisList = user.getSkinAnalysisList();
        List<HistoryDTO> skinAnalysisDTOList = skinAnalysisList.stream()
                .map(skinAnalysis -> objectMapper.convertValue(skinAnalysis, HistoryDTO.class))
                .toList();
        return ResponseEntity.ok(SaResult.data(skinAnalysisDTOList));
    }

}
