package com.fuzhi.fuzhisever.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuzhi.fuzhisever.Model.SkinAnalysis;
import com.fuzhi.fuzhisever.Repository.SkinAnalysisRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class SkinAnalysisService {
    private SkinAnalysisRepository skinAnalysisRepository;
    private ObjectMapper objectMapper;
    private final CommunicationService communicationService;

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

        // 获取 face_maps 对象
        Map<String, String> face_maps = (Map<String, String>) skinAnalysis.getResult().get("face_maps");
        Map<String, Object> result = skinAnalysis.getResult();
        result.remove("face_maps");
        skinAnalysis.setResult(result);
        if (face_maps != null) {

            String brownArea = face_maps.get("brown_area");
            String redArea = face_maps.get("red_area");
            String roiOutlineMap = face_maps.get("roi_outline_map");
            String roughArea = face_maps.get("rough_area");
            String waterArea = face_maps.get("water_area");
            String textureEnhancedBlackheads = face_maps.get("texture_enhanced_blackheads");
            String textureEnhancedBw = face_maps.get("texture_enhanced_bw");
            String textureEnhancedLines = face_maps.get("texture_enhanced_lines");
            String textureEnhancedOilyArea = face_maps.get("texture_enhanced_oily_area");
            String textureEnhancedPores = face_maps.get("texture_enhanced_pores");
            System.out.println("face_maps: " + face_maps);
            Map<String, String> newMap = new HashMap<>();
            newMap.put("brown_area", brownArea);
            newMap.put("red_area", redArea);
            newMap.put("roi_outline_map", roiOutlineMap);
            newMap.put("rough_area", roughArea);
            newMap.put("water_area", waterArea);
            newMap.put("texture_enhanced_blackheads", textureEnhancedBlackheads);
            newMap.put("texture_enhanced_bw", textureEnhancedBw);
            newMap.put("texture_enhanced_lines", textureEnhancedLines);
            newMap.put("texture_enhanced_oily_area", textureEnhancedOilyArea);
            newMap.put("texture_enhanced_pores", textureEnhancedPores);
            Map<String, String> face_pic = new HashMap<>();
            for (Map.Entry<String, String> entry : newMap.entrySet()) {
                UUID uuid = UUID.randomUUID();
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
                String timestamp = now.format(formatter);
                System.out.println(entry.getValue());
                String key ="facialAnalysis/" + userId + "/" +timestamp + "/"+ uuid+entry.getKey();
                communicationService.uploadBase64Image(entry.getValue(), key);
                face_pic.put(entry.getKey(), key);
            }

            skinAnalysis.setFaceMaps(face_pic);
        } else {
            System.out.println("face_maps not found in the result");
        }

        // Save the SkinAnalysis object
        try {
            return skinAnalysisRepository.save(skinAnalysis);
        } catch (Exception e) {
            throw new Exception("Failed to save SkinAnalysis data", e);
        }
    }


}
