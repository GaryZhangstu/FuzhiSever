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
import org.springframework.cache.annotation.CacheEvict;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

import java.util.UUID;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
public class AnalysisController {
    private final CommunicationService communicationService;

    private final SkinAnalysisService skinAnalysisService;

    private final SkinAnalysisRepository skinAnalysisRepository;
    private final HistoryService historyService;

    @PostMapping("/facialReport")
    @SaCheckLogin
    @CacheEvict(value = { "history"}, keyGenerator = "customKeyGenerator")
    public ResponseEntity<ApiResponse<SkinAnalysis>> getFacialReport(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_EMPTY);
        }

        String userId = StpUtil.getLoginId().toString();
        UUID uuid = UUID.randomUUID();
        String key = "facialAnalysis/" + userId + "/" + uuid + file.getOriginalFilename();

        communicationService.uploadFileToS3(file.getInputStream(), key);
        Object result = communicationService.getFacialReport(file);
        SkinAnalysis analysisResult = skinAnalysisService.saveSkinAnalysisData(result, key, userId);

        return ResponseEntity.ok(ApiResponse.success(analysisResult));
    }


    @GetMapping("/getSkinAnalysisHistory")
    @SaCheckLogin
    public ResponseEntity<ApiResponse<List<History>>> getSkinAnalysisHistory() {
        String userId = StpUtil.getLoginId().toString();
        List<History> historyList = historyService.findAllByUserIdOrderByTimeStamp(userId);
        return ResponseEntity.ok(ApiResponse.success(historyList));
    }

    @GetMapping("/getSkinAnalysisReport")
    @SaCheckLogin
    public ResponseEntity<ApiResponse<SkinAnalysis>> getSkinAnalysis(@RequestParam String Id) {
        SkinAnalysis skinAnalysis = skinAnalysisRepository.findById(Id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SKIN_ANALYSIS_NOT_FOUND));
        return ResponseEntity.ok(ApiResponse.success(skinAnalysis));
    }


}
