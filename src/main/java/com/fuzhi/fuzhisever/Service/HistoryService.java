package com.fuzhi.fuzhisever.Service;

import com.fuzhi.fuzhisever.Model.History;
import com.fuzhi.fuzhisever.Repository.HistoryRepository;
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
