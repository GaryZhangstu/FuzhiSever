package com.fuzhi.fuzhisever.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InsightsDto {

    private Object totalScoreAndTimestamp;
    private Object acneScoreAndTimestamp;
    private Object blackheadScoreAndTimestamp;
    private Object roughScoreAndTimestamp;
    private Object sensitivityScoreAndTimestamp;


}
