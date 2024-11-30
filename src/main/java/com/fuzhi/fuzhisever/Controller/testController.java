package com.fuzhi.fuzhisever.Controller;


import com.fuzhi.fuzhisever.Service.SkinAnalysisService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping("/test")
@AllArgsConstructor
public class testController {

    private final SkinAnalysisService skinAnalysisService;

    @RequestMapping("/test")
    public String test() {
        return "test";
    }
    @RequestMapping("/test2")
    public String test2() throws Exception {
        skinAnalysisService.saveSkinAnalysisData(new File("C:\\Users\\zhang\\IdeaProjects\\FuzhiSever\\src\\main\\java\\com\\fuzhi\\fuzhisever\\Service\\simpleResponse.json"));
        return "test2";
    }
}
