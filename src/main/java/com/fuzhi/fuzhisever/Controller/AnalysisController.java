package com.fuzhi.fuzhisever.Controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuzhi.fuzhisever.DTO.HistoryDTO;
import com.fuzhi.fuzhisever.DTO.InsightsDto;
import com.fuzhi.fuzhisever.DTO.TimestampAndScoreDTO;
import com.fuzhi.fuzhisever.Model.SkinAnalysis;
import com.fuzhi.fuzhisever.Model.User;
import com.fuzhi.fuzhisever.Repository.SkinAnalysisRepository;
import com.fuzhi.fuzhisever.Repository.UserRepository;
import com.fuzhi.fuzhisever.Service.CommunicationService;
import com.fuzhi.fuzhisever.Service.SkinAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
@Slf4j
public class AnalysisController {
    private final CommunicationService communicationService;
    private final UserRepository userRepository;
    private final SkinAnalysisService skinAnalysisService;
    private final ObjectMapper objectMapper;
    private final SkinAnalysisRepository skinAnalysisRepository;

    @PostMapping("/facialReport")
    @SaCheckLogin
    @CacheEvict(value = "insights")
    public ResponseEntity<Object> getFacialReport(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(SaResult.error("上传的文件不能为空"));
        }

        try {
            String userId = StpUtil.getLoginId().toString();

            UUID uuid = UUID.randomUUID();

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
            String timestamp = now.format(formatter);
            String key ="facialAnalysis/" + userId + "/"+timestamp +"/" + uuid+file.getOriginalFilename();

            communicationService.uploadFileToS3(file.getInputStream(), key);
            Object result =communicationService.getFacialReport(file);
            Object analysisResult =skinAnalysisService.saveSkinAnalysisData(result, key,userId);


            return ResponseEntity.ok(analysisResult);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(SaResult.error("文件上传失败: " + e.getMessage()));
        }
    }

    @GetMapping("/getSkinAnalysisHistory")
    @SaCheckLogin
    public ResponseEntity<SaResult> getSkinAnalysisHistory() {
        String userId = StpUtil.getLoginId().toString();

        List<SkinAnalysis> skinAnalysisList = skinAnalysisRepository.findSkinAnalysisByUserId(userId);
        List<HistoryDTO> skinAnalysisDTOList = skinAnalysisList.stream()
                .map(skinAnalysis -> objectMapper.convertValue(skinAnalysis, HistoryDTO.class))
                .toList();
        return ResponseEntity.ok(SaResult.data(skinAnalysisDTOList));
    }

    @GetMapping("/getSkinAnalysisReport")
    @SaCheckLogin
    public ResponseEntity<SaResult> getSkinAnalysis(@RequestParam String Id) {
        Optional<SkinAnalysis> optionalSkinAnalysis = skinAnalysisRepository.findById(Id);
        if (optionalSkinAnalysis.isEmpty()) {
            return new ResponseEntity<>(SaResult.error("皮肤分析结果不存在"), HttpStatus.NOT_FOUND);
        }
        SkinAnalysis skinAnalysis = optionalSkinAnalysis.get();
        return ResponseEntity.ok(SaResult.data(skinAnalysis));

    }
    @GetMapping("/getSkinAnalysisInsights")
    @SaCheckLogin
    @Cacheable(value = "insights")
    public ResponseEntity<InsightsDto> getSkinAnalysisInsights() {
        String userId = StpUtil.getLoginId().toString();
        List<TimestampAndScoreDTO> totalScoreAndTimestamp = skinAnalysisRepository.findTimestampAndTotalScoreByUserId( userId);
        List<TimestampAndScoreDTO> acneScoreAndTimestamp = skinAnalysisRepository.findTimestampAndAcneScoreByUserId( userId);
        List<TimestampAndScoreDTO> blackheadScoreAndTimestamp = skinAnalysisRepository.findTimestampAndBlackheadScoreByUserId( userId);
        List<TimestampAndScoreDTO> roughScoreAndTimestamp = skinAnalysisRepository.findTimestampAndRoughScoreByUserId( userId);
        List<TimestampAndScoreDTO> sensitivityScoreAndTimestamp = skinAnalysisRepository.findTimestampAndSensitivityScoreByUserId( userId);
        InsightsDto insightsDto = new InsightsDto(totalScoreAndTimestamp, acneScoreAndTimestamp, blackheadScoreAndTimestamp, roughScoreAndTimestamp, sensitivityScoreAndTimestamp);
        return ResponseEntity.ok(insightsDto);
    }

}
