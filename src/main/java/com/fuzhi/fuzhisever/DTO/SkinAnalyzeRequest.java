package com.fuzhi.fuzhisever.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SkinAnalyzeRequest {
    private String api_key;
    private String api_secret;

}

