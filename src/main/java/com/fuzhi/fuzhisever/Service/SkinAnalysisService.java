package com.fuzhi.fuzhisever.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuzhi.fuzhisever.Model.History;
import com.fuzhi.fuzhisever.Model.SkinAnalysis;
import com.fuzhi.fuzhisever.Repository.HistoryRepository;
import com.fuzhi.fuzhisever.Repository.SkinAnalysisRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Log4j2
public class SkinAnalysisService {

    private static final String REQUEST_ID_KEY = "request_id";
    private static final String RESULT_KEY = "result";
    private static final String SCORE_INFO_KEY = "score_info";

    private final SkinAnalysisRepository skinAnalysisRepository;
    private final ObjectMapper objectMapper;
    private final HistoryRepository historyRepository;

    @Transactional
    public SkinAnalysis saveSkinAnalysisData(Object jsonObject, String imageKey, String userId) throws Exception {
        if (objectMapper == null) {
            throw new IllegalStateException("ObjectMapper is not properly injected");
        }

        // Convert JSON object to Map
        Map<String, Object> jsonData;
        try {
            jsonData = objectMapper.convertValue(jsonObject, Map.class);
        } catch (IllegalArgumentException e) {
            log.error("Failed to convert JSON object to Map", e);
            throw new IllegalArgumentException("Failed to convert JSON object to Map", e);
        }

        // Create a new SkinAnalysis instance
        SkinAnalysis skinAnalysis = new SkinAnalysis();
        skinAnalysis.setImageKey(imageKey);
        skinAnalysis.setUserId(userId);

        // Set requestId if it exists
        if (jsonData.containsKey(REQUEST_ID_KEY)) {
            skinAnalysis.setRequestId((String) jsonData.get(REQUEST_ID_KEY));
        } else {
            log.error("Missing 'request_id' in JSON data");
            throw new IllegalArgumentException("Missing 'request_id' in JSON data");
        }

        // Set result if it exists and is of the correct type
        if (jsonData.containsKey(RESULT_KEY) && jsonData.get(RESULT_KEY) instanceof Map) {
            skinAnalysis.setResult((Map<String, Object>) jsonData.get(RESULT_KEY));
        } else {
            log.error("Missing or invalid 'result' in JSON data");
            throw new IllegalArgumentException("Missing or invalid 'result' in JSON data");
        }

        // Save the SkinAnalysis object
        try {
            SkinAnalysis savedSkinAnalysis = skinAnalysisRepository.save(skinAnalysis);
            History history = new History();
            history.setId(savedSkinAnalysis.getId());
            history.setUserId(savedSkinAnalysis.getUserId());
            history.setTimeStamp(savedSkinAnalysis.getTimeStamp());
            history.setImageKey(savedSkinAnalysis.getImageKey());

            Object scoreInfo = savedSkinAnalysis.getResult().get(SCORE_INFO_KEY);
            if (scoreInfo != null) {
                history.setScore(scoreInfo.toString());
            } else {
                log.warn("Score info is null for SkinAnalysis ID: {}", savedSkinAnalysis.getId());
            }

            historyRepository.save(history);
            return savedSkinAnalysis;
        } catch (Exception e) {
            log.error("Failed to save SkinAnalysis data", e);
            throw new RuntimeException("Failed to save SkinAnalysis data", e);
        }
    }
}

