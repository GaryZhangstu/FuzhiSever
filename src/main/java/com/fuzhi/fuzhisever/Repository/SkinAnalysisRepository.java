package com.fuzhi.fuzhisever.Repository;

import com.fuzhi.fuzhisever.Model.SkinAnalysis;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkinAnalysisRepository extends MongoRepository<SkinAnalysis, String> {
}
