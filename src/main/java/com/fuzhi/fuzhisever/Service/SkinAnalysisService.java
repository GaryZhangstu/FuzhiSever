package com.fuzhi.fuzhisever.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuzhi.fuzhisever.Model.History;
import com.fuzhi.fuzhisever.Model.SkinAnalysis;
import com.fuzhi.fuzhisever.Repository.HistoryRepository;
import com.fuzhi.fuzhisever.Repository.SkinAnalysisRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class SkinAnalysisService {
    private final SkinAnalysisRepository skinAnalysisRepository;
    private final ObjectMapper objectMapper;
    private final HistoryRepository historyRepository;

    public SkinAnalysis saveSkinAnalysisData(Object jsonObject, String imageKey, String userId) throws Exception {
        Map<String, Object> jsonData = convertJsonToMap(jsonObject);
        SkinAnalysis skinAnalysis = createSkinAnalysis(jsonData, imageKey, userId);
        return saveSkinAnalysis(skinAnalysis);
    }

    private Map<String, Object> convertJsonToMap(Object jsonObject) throws Exception {
        try {
            return objectMapper.convertValue(jsonObject, Map.class);
        } catch (IllegalArgumentException e) {
            throw new Exception("Failed to convert JSON object to Map", e);
        }
    }

    private SkinAnalysis createSkinAnalysis(Map<String, Object> jsonData, String imageKey, String userId) throws Exception {
        SkinAnalysis skinAnalysis = new SkinAnalysis();
        skinAnalysis.setImageKey(imageKey);
        skinAnalysis.setUserId(userId);

        if (jsonData.containsKey("request_id")) {
            skinAnalysis.setRequestId((String) jsonData.get("request_id"));
        } else {
            throw new Exception("Missing 'request_id' in JSON data");
        }

        if (jsonData.containsKey("result") && jsonData.get("result") instanceof Map) {
            skinAnalysis.setResult((Map<String, Object>) jsonData.get("result"));
        } else {
            throw new Exception("Missing or invalid 'result' in JSON data");
        }

        return skinAnalysis;
    }

    private SkinAnalysis saveSkinAnalysis(SkinAnalysis skinAnalysis) throws Exception {
        try {
            SkinAnalysis savedSkinAnalysis = skinAnalysisRepository.save(skinAnalysis);
            saveHistory(savedSkinAnalysis);
            return savedSkinAnalysis;
        } catch (Exception e) {
            throw new Exception("Failed to save SkinAnalysis data", e);
        }
    }

    private void saveHistory(SkinAnalysis savedSkinAnalysis) {
        History history = new History();
        history.setId(savedSkinAnalysis.getId());
        history.setUserId(savedSkinAnalysis.getUserId());
        history.setTimeStamp(savedSkinAnalysis.getTimeStamp());
        history.setImageKey(savedSkinAnalysis.getImageKey());
        history.setScore(savedSkinAnalysis.getResult().get("score_info"));
        historyRepository.save(history);
    }
}
