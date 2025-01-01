package com.fuzhi.fuzhisever.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuzhi.fuzhisever.Model.History;
import com.fuzhi.fuzhisever.Model.SkinAnalysis;
import com.fuzhi.fuzhisever.Repository.HistoryRepository;
import com.fuzhi.fuzhisever.Repository.SkinAnalysisRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

@Service
@AllArgsConstructor
public class SkinAnalysisService {
    private SkinAnalysisRepository skinAnalysisRepository;
    private ObjectMapper objectMapper;
    private HistoryRepository historyRepository;

    public void saveSkinAnalysis(File jsonFile) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonData = objectMapper.readValue(jsonFile, Map.class);

        SkinAnalysis skinAnalysis = new SkinAnalysis();
        skinAnalysis.setRequestId((String) jsonData.get("request_id"));
        skinAnalysis.setResult((Map<String, Object>) jsonData.get("result"));



        skinAnalysisRepository.save(skinAnalysis);
    }

    public SkinAnalysis saveSkinAnalysisData(Object jsonObject, String imageKey, String userId) throws Exception {

        // Convert JSON object to Map
        Map<String, Object> jsonData;
        try {
            jsonData = objectMapper.convertValue(jsonObject, Map.class);
        } catch (IllegalArgumentException e) {
            throw new Exception("Failed to convert JSON object to Map", e);
        }

        // Create a new SkinAnalysis instance
        SkinAnalysis skinAnalysis = new SkinAnalysis();
        skinAnalysis.setImageKey(imageKey);
        skinAnalysis.setUserId(userId);

        // Set requestId if it exists
        if (jsonData.containsKey("request_id")) {
            skinAnalysis.setRequestId((String) jsonData.get("request_id"));
        } else {
            throw new Exception("Missing 'request_id' in JSON data");
        }

        // Set result if it exists and is of the correct type
        if (jsonData.containsKey("result") && jsonData.get("result") instanceof Map) {
            skinAnalysis.setResult((Map<String, Object>) jsonData.get("result"));
        } else {
            throw new Exception("Missing or invalid 'result' in JSON data");
        }

        // Save the SkinAnalysis object
        try {

            SkinAnalysis savedSkinAnalysis = skinAnalysisRepository.save(skinAnalysis);
            History history = objectMapper.convertValue(savedSkinAnalysis, History.class);
            history.setScore(savedSkinAnalysis.getResult().get("score_info"));
            historyRepository.save(history);
            return savedSkinAnalysis;
        } catch (Exception e) {
            throw new Exception("Failed to save SkinAnalysis data", e);
        }
    }


}
