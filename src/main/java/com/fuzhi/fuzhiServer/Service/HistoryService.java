package com.fuzhi.fuzhiServer.Service;

import com.fuzhi.fuzhiServer.Model.History;
import com.fuzhi.fuzhiServer.Repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository HistoryRepository;

    /**
     * 根据用户ID查找所有历史记录，并按时间戳排序。
     * 该方法会尝试从缓存中获取历史记录，如果缓存中不存在，则从数据库中查找。
     *
     * @param userId 用户的唯一标识符
     * @return 按时间戳排序的历史记录列表
     */
    @Cacheable(value = "history", key = "#userId")
    public List<History> findAllByUserIdOrderByTimeStamp(String userId) {
        return HistoryRepository.findAllByUserIdOrderByTimeStamp(userId);
    }

}
