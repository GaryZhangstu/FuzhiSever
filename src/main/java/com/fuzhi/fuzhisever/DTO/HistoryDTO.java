package com.fuzhi.fuzhisever.DTO;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class HistoryDTO {

    private String id;

    private LocalDateTime time;

    private String requestId;

    private String imageKey;


}
