package com.fuzhi.fuzhisever.Repository;

import com.fuzhi.fuzhisever.DTO.TimestampAndScoreDTO;
import com.fuzhi.fuzhisever.Model.SkinAnalysis;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkinAnalysisRepository extends MongoRepository<SkinAnalysis, String> {



    @Aggregation(pipeline = {
            "{ $match: { 'userId': ?0 } }",
            "{ $project: { 'timeStamp': 1, 'score': '$result.score_info.sensitivity_score' } }"
    })
    List<TimestampAndScoreDTO> findTimestampAndSensitivityScoreByUserId(String userId);


    @Aggregation(pipeline = {
            "{ $match: { 'userId': ?0 } }",
            "{ $project: { 'timeStamp': 1, 'score': '$result.score_info.acne_score' } }"
    })
    List<TimestampAndScoreDTO> findTimestampAndAcneScoreByUserId(String userId);


    @Aggregation(pipeline = {
            "{ $match: { 'userId': ?0 } }",
            "{ $project: { 'timeStamp': 1, 'score': '$result.score_info.blackhead_score' } }"
    })
    List<TimestampAndScoreDTO> findTimestampAndBlackheadScoreByUserId(String userId);


    @Aggregation(pipeline = {
            "{ $match: { 'userId': ?0 } }",
            "{ $project: { 'timeStamp': 1, 'score': '$result.score_info.rough_score' } }"
    })
    List<TimestampAndScoreDTO> findTimestampAndRoughScoreByUserId(String userId);



    @Aggregation(pipeline = {
            "{ $match: { 'userId': ?0 } }",
            "{ $project: { 'timeStamp': 1, 'score': '$result.score_info.total_score' } }"
    })
    List<TimestampAndScoreDTO> findTimestampAndTotalScoreByUserId(String userId);
}
