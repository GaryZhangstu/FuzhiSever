package com.fuzhi.fuzhisever.DTO;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class HistoryDTO {

    private String id;

    private LocalDateTime  timeStamp;

    private Integer score;

    private String imageKey;


}
