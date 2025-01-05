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

    @Cacheable(value = "history", key = "#userId")
    public List<History> findAllByUserIdOrderByTimeStamp(String userId) {
        return HistoryRepository.findAllByUserIdOrderByTimeStamp(userId);
    }

}
