package com.fuzhi.fuzhisever.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuzhi.fuzhisever.Model.SkinAnalysis;
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

    public void saveSkinAnalysis(File jsonFile) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonData = objectMapper.readValue(jsonFile, Map.class);

        SkinAnalysis skinAnalysis = new SkinAnalysis();
        skinAnalysis.setRequestId((String) jsonData.get("request_id"));
        skinAnalysis.setResult((Map<String, Object>) jsonData.get("result"));



        skinAnalysisRepository.save(skinAnalysis);
    }

    public SkinAnalysis saveSkinAnalysisData(Object jsonObject, String imageKey) throws Exception {

        Map<String, Object> jsonData = objectMapper.convertValue(jsonObject, Map.class);

        SkinAnalysis skinAnalysis = new SkinAnalysis();
        skinAnalysis.setImageKey(imageKey);
        skinAnalysis.setRequestId((String) jsonData.get("request_id"));
        skinAnalysis.setResult((Map<String, Object>) jsonData.get("result"));

        return skinAnalysisRepository.save(skinAnalysis);
    }
}
