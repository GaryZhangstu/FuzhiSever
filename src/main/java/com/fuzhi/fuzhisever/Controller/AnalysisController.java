package com.fuzhi.fuzhisever.Controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuzhi.fuzhisever.DTO.HistoryDTO;
import com.fuzhi.fuzhisever.DTO.InsightsDto;
import com.fuzhi.fuzhisever.DTO.TimestampAndScoreDTO;
import com.fuzhi.fuzhisever.Model.History;
import com.fuzhi.fuzhisever.Model.SkinAnalysis;
import com.fuzhi.fuzhisever.Model.User;
import com.fuzhi.fuzhisever.Repository.HistoryRepository;
import com.fuzhi.fuzhisever.Repository.SkinAnalysisRepository;
import com.fuzhi.fuzhisever.Repository.UserRepository;
import com.fuzhi.fuzhisever.Service.CommunicationService;
import com.fuzhi.fuzhisever.Service.SkinAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
public class AnalysisController {
    private final CommunicationService communicationService;
    private final UserRepository userRepository;
    private final SkinAnalysisService skinAnalysisService;
    private final ObjectMapper objectMapper;
    private final SkinAnalysisRepository skinAnalysisRepository;
    private final HistoryRepository historyRepository;

    @PostMapping("/facialReport")
    @SaCheckLogin
    @CacheEvict(value = {"insights", "history"}, allEntries = true)
    public ResponseEntity<Object> getFacialReport(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(SaResult.error("上传的文件不能为空"));
        }

        try {
            String userId = StpUtil.getLoginId().toString();

            UUID uuid = UUID.randomUUID();

            String key ="facialAnalysis/" + userId + "/" + uuid+file.getOriginalFilename();

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
    @Cacheable(value = "history")
    public ResponseEntity<SaResult> getSkinAnalysisHistory() {
        String userId = StpUtil.getLoginId().toString();
        List<History> historyList = historyRepository.findAllByUserIdOrderByTimeStamp(userId);

        return ResponseEntity.ok(SaResult.data(historyList));
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
    @Deprecated
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
