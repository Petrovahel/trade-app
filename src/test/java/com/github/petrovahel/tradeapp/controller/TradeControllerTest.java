package com.github.petrovahel.tradeapp.controller;

import com.github.petrovahel.tradeapp.exception.TradeProcessingException;
import com.github.petrovahel.tradeapp.service.FileProcessingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileProcessingService fileProcessingService;

    @Test
    void enrich_returnSuccess_ifServiceReturnSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.csv", MediaType.TEXT_PLAIN_VALUE, "some,test,data".getBytes()
        );

        Mockito.when(fileProcessingService.processFile(any(MultipartFile.class)))
                .thenReturn("File processed successfully");

        mockMvc.perform(multipart("/api/v1/enrich")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().string("File processed successfully"));
    }

    @Test
    void enrich_response404_whenFileNameMissing() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "", MediaType.TEXT_PLAIN_VALUE, "some,test,data".getBytes()
        );

        Mockito.when(fileProcessingService.processFile(any(MultipartFile.class)))
                .thenThrow(new TradeProcessingException("File name is missing"));

        mockMvc.perform(multipart("/api/v1/enrich")
                .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("File name is missing"));
    }

    @Test
    void enrich_response404_whenFileTypeNotSupported() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.xml", MediaType.TEXT_PLAIN_VALUE, "some,test,data".getBytes()
        );

        Mockito.when(fileProcessingService.processFile(any(MultipartFile.class)))
                .thenThrow(new TradeProcessingException("File type not supported"));

        mockMvc.perform(multipart("/api/v1/enrich")
                .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("File type not supported"));
    }
}