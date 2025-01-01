package com.fuzhi.fuzhisever.Repository;

import com.fuzhi.fuzhisever.Model.History;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends MongoRepository<History, String> {


}
