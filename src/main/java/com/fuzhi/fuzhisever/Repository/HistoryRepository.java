package com.fuzhi.fuzhisever.Repository;

import com.fuzhi.fuzhisever.Model.History;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends MongoRepository<History, String> {


    List<History> findAllByUserIdOrderByTimeStamp(String userId);
}
