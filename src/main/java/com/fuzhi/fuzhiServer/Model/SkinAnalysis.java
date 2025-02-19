package com.fuzhi.fuzhiServer.Model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

@Document(collection = "SkinAnalysis")
@Data
public class SkinAnalysis {

    @Id
    private String id;

    @CreatedDate
    private Date timeStamp;

    private String userId;

    private Map<String,String> faceMaps;

    private String requestId;

    private String imageKey;

    private Map<String, Object> result;
}
