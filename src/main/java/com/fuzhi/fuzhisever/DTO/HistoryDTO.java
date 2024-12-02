package com.fuzhi.fuzhisever.DTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;


@Data
public class HistoryDTO {

    private String id;

    private Date timeStamp;

    private Integer score;

    private String imageKey;


}
