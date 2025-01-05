package com.fuzhi.fuzhisever.Controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.fuzhi.fuzhisever.DTO.ApiResponse;
import com.fuzhi.fuzhisever.Exception.BusinessException;
import com.fuzhi.fuzhisever.Exception.ErrorCode;
import com.fuzhi.fuzhisever.Model.History;
import com.fuzhi.fuzhisever.Model.SkinAnalysis;
import com.fuzhi.fuzhisever.Repository.SkinAnalysisRepository;
import com.fuzhi.fuzhisever.Service.CommunicationService;
import com.fuzhi.fuzhisever.Service.HistoryService;
import com.fuzhi.fuzhisever.Service.SkinAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/analysis")
@Log4j2
@RequiredArgsConstructor
public class AnalysisController {
    private final CommunicationService communicationService;
    private final SkinAnalysisService skinAnalysisService;
    private final SkinAnalysisRepository skinAnalysisRepository;
    private final HistoryService historyService;

    @PostMapping("/facialReport")
    @SaCheckLogin
    @CacheEvict(value = {"history"}, keyGenerator = "customKeyGenerator")
    public ResponseEntity<ApiResponse<SkinAnalysis>> getFacialReport(@RequestParam("file") MultipartFile file) throws Exception {
        log.info("Received request to get facial report with file: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            log.error("File is empty: {}", file.getOriginalFilename());
            throw new BusinessException(ErrorCode.FILE_EMPTY);
        }

        String userId = StpUtil.getLoginId().toString();
        UUID uuid = UUID.randomUUID();
        String key = "facialAnalysis/" + userId + "/" + uuid + file.getOriginalFilename();

        log.info("Uploading file to S3 with key: {}", key);
        communicationService.uploadFileToS3(file.getInputStream(), key);

        log.info("Getting facial report for file: {}", file.getOriginalFilename());
        Object result = communicationService.getFacialReport(file);

        log.info("Saving skin analysis data for user: {}", userId);
        SkinAnalysis analysisResult = skinAnalysisService.saveSkinAnalysisData(result, key, userId);

        log.info("Returning successful response with skin analysis result");
        return ResponseEntity.ok(ApiResponse.success(analysisResult));
    }

    @GetMapping("/getSkinAnalysisHistory")
    @SaCheckLogin
    public ResponseEntity<ApiResponse<List<History>>> getSkinAnalysisHistory() {
        String userId = StpUtil.getLoginId().toString();
        log.info("Getting skin analysis history for user: {}", userId);

        List<History> historyList = historyService.findAllByUserIdOrderByTimeStamp(userId);
        log.info("Returning successful response with history list");

        return ResponseEntity.ok(ApiResponse.success(historyList));
    }

    @GetMapping("/getSkinAnalysisReport")
    @SaCheckLogin
    public ResponseEntity<ApiResponse<SkinAnalysis>> getSkinAnalysis(@RequestParam String id) {
        log.info("Getting skin analysis report for ID: {}", id);

        SkinAnalysis skinAnalysis = skinAnalysisRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Skin analysis not found for ID: {}", id);
                    return new BusinessException(ErrorCode.SKIN_ANALYSIS_NOT_FOUND);
                });

        log.info("Returning successful response with skin analysis report");
        return ResponseEntity.ok(ApiResponse.success(skinAnalysis));
    }
}
