package com.fuzhi.fuzhiServer.Repository;

import com.fuzhi.fuzhiServer.Model.SkinAnalysis;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkinAnalysisRepository extends MongoRepository<SkinAnalysis, String> {


}
