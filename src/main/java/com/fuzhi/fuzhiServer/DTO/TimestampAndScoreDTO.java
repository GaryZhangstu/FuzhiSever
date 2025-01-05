package com.fuzhi.fuzhiServer.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class TimestampAndScoreDTO {
    private Date timeStamp;
    private Integer score;
}
