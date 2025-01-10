package com.fuzhi.fuzhiServer.Controller;

import com.fuzhi.fuzhiServer.DTO.ApiResponse;
import com.fuzhi.fuzhiServer.Exception.BusinessException;
import com.fuzhi.fuzhiServer.Exception.ErrorCode;
import com.fuzhi.fuzhiServer.Model.History;
import com.fuzhi.fuzhiServer.Model.SkinAnalysis;
import com.fuzhi.fuzhiServer.Repository.SkinAnalysisRepository;
import com.fuzhi.fuzhiServer.Service.CommunicationService;
import com.fuzhi.fuzhiServer.Service.HistoryService;
import com.fuzhi.fuzhiServer.Service.SkinAnalysisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AnalysisControllerTest {

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
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetFacialReport() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        SkinAnalysis skinAnalysis = new SkinAnalysis();
        when(communicationService.getFacialReport(any(MultipartFile.class))).thenReturn(new Object());
        when(skinAnalysisService.saveSkinAnalysisData(any(), anyString(), anyString())).thenReturn(skinAnalysis);

        ResponseEntity<ApiResponse<SkinAnalysis>> response = analysisController.getFacialReport(file);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(skinAnalysis, response.getBody().getData());
    }

    @Test
    void testGetFacialReport_FileEmpty() {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[0]);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            analysisController.getFacialReport(file);
        });

        assertEquals(ErrorCode.FILE_EMPTY.getCode(), exception.getCode());
    }

    @Test
    void testGetSkinAnalysisHistory() {
        List<History> historyList = new ArrayList<>();
        when(historyService.findAllByUserIdOrderByTimeStamp(anyString())).thenReturn(historyList);

        ResponseEntity<ApiResponse<List<History>>> response = analysisController.getSkinAnalysisHistory();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(historyList, response.getBody().getData());
    }

    @Test
    void testGetSkinAnalysis() {
        SkinAnalysis skinAnalysis = new SkinAnalysis();
        when(skinAnalysisRepository.findById(anyString())).thenReturn(Optional.of(skinAnalysis));

        ResponseEntity<ApiResponse<SkinAnalysis>> response = analysisController.getSkinAnalysis("testId");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(skinAnalysis, response.getBody().getData());
    }

    @Test
    void testGetSkinAnalysis_NotFound() {
        when(skinAnalysisRepository.findById(anyString())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            analysisController.getSkinAnalysis("testId");
        });

        assertEquals(ErrorCode.SKIN_ANALYSIS_NOT_FOUND.getCode(), exception.getCode());
    }
}
