package com.fuzhi.fuzhisever.Controller;


import cn.dev33.satoken.annotation.SaIgnore;
import com.fuzhi.fuzhisever.Service.CommunicationService;
import com.fuzhi.fuzhisever.Service.SkinAnalysisService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/test")
@AllArgsConstructor
public class testController {

    private final SkinAnalysisService skinAnalysisService;

    private final CommunicationService communicationService;



}
