package com.fuzhi.fuzhisever.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
public class History {

    @Id
    private String id;

    private Date timeStamp;

    private Object score;

    private String imageKey;
}
