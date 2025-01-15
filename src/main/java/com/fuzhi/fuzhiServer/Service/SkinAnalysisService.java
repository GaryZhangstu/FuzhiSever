package com.fuzhi.fuzhiServer.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuzhi.fuzhiServer.Model.History;
import com.fuzhi.fuzhiServer.Model.SkinAnalysis;
import com.fuzhi.fuzhiServer.Repository.HistoryRepository;
import com.fuzhi.fuzhiServer.Repository.SkinAnalysisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 保存皮肤分析数据到数据库，并创建相应的历史记录。
     *
     * @param jsonObject 包含皮肤分析结果的 JSON 对象
     * @param imageKey   图像的唯一标识符
     * @param userId     用户的唯一标识符
     * @return 保存的 SkinAnalysis 实体对象
     * @throws IllegalArgumentException 如果 JSON 数据格式不正确或缺少必要字段
     * @throws RuntimeException 如果保存数据时发生异常
     */
    @Transactional
    public SkinAnalysis saveSkinAnalysisData(Object jsonObject, String imageKey, String userId) {


        // 将传入的 JSON 对象转换为 Map<String, Object>
        // 如果转换失败，记录错误日志并抛出异常
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

        // 设置请求 ID，如果 JSON 数据中不存在 'request_id'，记录错误日志并抛出异常
        if (jsonData.containsKey(REQUEST_ID_KEY)) {
            skinAnalysis.setRequestId((String) jsonData.get(REQUEST_ID_KEY));
        } else {
            log.error("Missing 'request_id' in JSON data");
            throw new IllegalArgumentException("Missing 'request_id' in JSON data");
        }

        // 设置皮肤分析结果，确保结果是 Map 类型
        // 如果 JSON 数据中不存在 'result' 或类型不正确，记录错误日志并抛出异常
        if (jsonData.containsKey(RESULT_KEY) && jsonData.get(RESULT_KEY) instanceof Map) {
            skinAnalysis.setResult((Map<String, Object>) jsonData.get(RESULT_KEY));
        } else {
            log.error("Missing or invalid 'result' in JSON data");
            throw new IllegalArgumentException("Missing or invalid 'result' in JSON data");
        }

        // 保存 SkinAnalysis 对象到数据库
        // 创建并保存对应的 History 记录
        SkinAnalysis savedSkinAnalysis = skinAnalysisRepository.save(skinAnalysis);
        createHistoryForSkinAnalysis(savedSkinAnalysis);

        return savedSkinAnalysis;
    }
    /**
     * 创建并保存与 SkinAnalysis 关联的历史记录。
     *
     * @param savedSkinAnalysis 已保存的 SkinAnalysis 实体对象
     */
    private void createHistoryForSkinAnalysis(SkinAnalysis savedSkinAnalysis) {
        History history = new History();
        history.setId(savedSkinAnalysis.getId());
        history.setUserId(savedSkinAnalysis.getUserId());
        history.setTimeStamp(savedSkinAnalysis.getTimeStamp());
        history.setImageKey(savedSkinAnalysis.getImageKey());

        Object scoreInfo = savedSkinAnalysis.getResult().get(SCORE_INFO_KEY);
        if (scoreInfo != null) {
            history.setScore(scoreInfo);
        } else {
            log.warn("Score info is null for SkinAnalysis ID: {}", savedSkinAnalysis.getId());
        }

        try {
            historyRepository.save(history);
        } catch (Exception e) {
            log.error("Failed to save History data", e);
            throw new RuntimeException("Failed to save History data", e);
        }
    }

}

