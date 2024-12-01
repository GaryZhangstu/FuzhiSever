package com.fuzhi.fuzhisever.Controller;


import cn.dev33.satoken.annotation.SaIgnore;
import com.fuzhi.fuzhisever.Service.CommunicationService;
import com.fuzhi.fuzhisever.Service.SkinAnalysisService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/test")
@AllArgsConstructor
public class testController {

    private final SkinAnalysisService skinAnalysisService;

    private final CommunicationService communicationService;

    @RequestMapping("/test")
    @SaIgnore
    public String test() throws IOException {
        communicationService.getFacialReport(new File("C:\\Users\\zhang\\Downloads\\屏幕截图 2024-12-01 183547.png"));
        return "test";
    }

}
