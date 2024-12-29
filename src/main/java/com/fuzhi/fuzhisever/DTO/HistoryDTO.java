package com.fuzhi.fuzhisever.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoryDTO {

    private String id;

    private Date timeStamp;

    private Object score;

    private String imageKey;


}
