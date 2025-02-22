package com.github.petrovahel.tradeapp.service;

import com.github.petrovahel.tradeapp.exception.TradeProcessingException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


@SpringBootTest
class FileProcessingServiceTest {

    @Autowired
    FileProcessingService fileProcessingService;

    @MockBean
    private CsvTradeService csvTradeService;

   @MockBean
   private JsonTradeService jsonTradeService;

    @Test
    void shouldThrowExceptionWhenFileNameIsMissing() {
        MultipartFile file = new MockMultipartFile("file", null, MediaType.TEXT_PLAIN_VALUE, "some data".getBytes());

        assertThrows(TradeProcessingException.class, () -> fileProcessingService.processFile(file));
    }

    @Test
    void shouldThrowExceptionWhenFileTypeIsNotSupported() {
        MultipartFile file = new MockMultipartFile("file", "test.xml", MediaType.TEXT_PLAIN_VALUE, "some data".getBytes());

        assertThrows(TradeProcessingException.class, () -> fileProcessingService.processFile(file));
    }

    @Test
    void shouldTrowExceptionInCsvService () {

        MultipartFile file = new MockMultipartFile("file", "test.csv", MediaType.TEXT_PLAIN_VALUE, "some,test,data".getBytes());

        Mockito.when(csvTradeService.processFile(any(MultipartFile.class))).thenReturn("Error");

        String result = fileProcessingService.processFile(file);

        assertEquals("Error", result);

        Mockito.verify(csvTradeService, Mockito.times(1)).processFile(any(MultipartFile.class));
    }

    @Test
    void shouldCallCvsServiceAndReturnResponse() {

        MultipartFile file = new MockMultipartFile("file", "test.csv", MediaType.TEXT_PLAIN_VALUE, "some,test,data".getBytes());

        Mockito.when(csvTradeService.processFile(any(MultipartFile.class))).thenReturn("Success");

        String result = fileProcessingService.processFile(file);

        assertEquals("Success", result);

        Mockito.verify(csvTradeService, Mockito.times(1)).processFile(any(MultipartFile.class));
    }

    @Test
    void shouldTrowExceptionInJsonService() {

        MultipartFile file = new MockMultipartFile("file", "test.json", MediaType.TEXT_PLAIN_VALUE, "some,test,data".getBytes());

        Mockito.when(jsonTradeService.processFile(any(MultipartFile.class))).thenReturn("Error");

        String result = fileProcessingService.processFile(file);

        assertEquals("Error", result);

        Mockito.verify(jsonTradeService, Mockito.times(1)).processFile(any(MultipartFile.class));
    }

    @Test
    void shouldCallJsonServiceAndReturnResponse() {

        MultipartFile file = new MockMultipartFile("file", "test.json", MediaType.TEXT_PLAIN_VALUE, "some,test,data".getBytes());

        Mockito.when(jsonTradeService.processFile(any(MultipartFile.class))).thenReturn("Success");

        String result = fileProcessingService.processFile(file);

        assertEquals("Success", result);

        Mockito.verify(jsonTradeService, Mockito.times(1)).processFile(any(MultipartFile.class));
    }
}