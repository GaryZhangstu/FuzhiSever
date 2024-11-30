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

    public void saveSkinAnalysisData(File jsonFile) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonData = objectMapper.readValue(jsonFile, Map.class);

        SkinAnalysis skinAnalysis = new SkinAnalysis();
        skinAnalysis.setRequestId((String) jsonData.get("request_id"));
        skinAnalysis.setResult((Map<String, Object>) jsonData.get("result"));



        skinAnalysisRepository.save(skinAnalysis);
    }
}
