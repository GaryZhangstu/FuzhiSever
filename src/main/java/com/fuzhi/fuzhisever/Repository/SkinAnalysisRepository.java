package com.fuzhi.fuzhisever.Repository;

import com.fuzhi.fuzhisever.Model.SkinAnalysis;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkinAnalysisRepository extends MongoRepository<SkinAnalysis, String> {


    @Query(value = "{ 'userId': ?0 }", fields = "{ 'timeStamp': 1, 'result.score_info.sensitivity_score': 1 }")
    List<SkinAnalysis> findTimestampAndSensitivityScoreByUserId(String userId);

    @Query(value = "{ 'userId': ?0 }", fields = "{ 'timeStamp': 1, 'result.score_info.acne_score': 1 }")
    List<SkinAnalysis> findTimestampAndAcneScoreByUserId(String userId);

    @Query(value = "{ 'userId': ?0 }", fields = "{ 'timeStamp': 1, 'result.score_info.blackhead_score': 1 }")
    List<SkinAnalysis> findTimestampAndBlackheadScoreByUserId(String userId);

    @Query(value = "{ 'userId': ?0 }", fields = "{ 'timeStamp': 1, 'result.score_info.rough_score': 1 }")
    List<SkinAnalysis> findTimestampAndRoughScoreByUserId(String userId);

    @Query(value = "{ 'userId': ?0 }", fields = "{ 'timeStamp': 1, 'result.score_info.total_score': 1 }")
    List<SkinAnalysis> findTimestampAndTotalScoreByUserId(String userId);
}
