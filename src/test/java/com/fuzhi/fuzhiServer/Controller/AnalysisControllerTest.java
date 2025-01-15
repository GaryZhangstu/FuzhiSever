package com.fuzhi.fuzhiServer.Controller;

import com.fuzhi.fuzhiServer.DTO.ApiResponse;
import com.fuzhi.fuzhiServer.Model.History;
import com.fuzhi.fuzhiServer.Model.SkinAnalysis;
import com.fuzhi.fuzhiServer.Repository.SkinAnalysisRepository;
import com.fuzhi.fuzhiServer.Service.CommunicationService;
import com.fuzhi.fuzhiServer.Service.HistoryService;
import com.fuzhi.fuzhiServer.Service.SkinAnalysisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
@Testcontainers
public class AnalysisControllerTest {

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

    private MockMvc mockMvc;

    @Mock
    private CommunicationService communicationService;

    @Mock
    private SkinAnalysisService skinAnalysisService;

    @Mock
    private SkinAnalysisRepository skinAnalysisRepository;

    @Mock
    private HistoryService historyService;

    @InjectMocks
    private AnalysisController analysisController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(analysisController).build();
    }

    @Test
    public void testGetFacialReport() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test data".getBytes());

        when(communicationService.uploadFileToS3(any(), any())).thenReturn(null);
        when(communicationService.getFacialReport(any())).thenReturn(new Object());
        when(skinAnalysisService.saveSkinAnalysisData(any(), any(), any())).thenReturn(new SkinAnalysis());

        mockMvc.perform(multipart("/analysis/facialReport").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("Success"));
    }

    @Test
    public void testGetSkinAnalysisHistory() throws Exception {
        when(historyService.findAllByUserIdOrderByTimeStamp(any())).thenReturn(List.of(new History()));

        mockMvc.perform(get("/analysis/getSkinAnalysisHistory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("Success"));
    }

    @Test
    public void testGetSkinAnalysis() throws Exception {
        SkinAnalysis skinAnalysis = new SkinAnalysis();
        when(skinAnalysisRepository.findById(any())).thenReturn(Optional.of(skinAnalysis));

        mockMvc.perform(get("/analysis/getSkinAnalysisReport").param("id", UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("Success"));
    }
}
