package com.github.petrovahel.tradeapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.petrovahel.tradeapp.dto.TradeDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class JsonTradeServiceTest {

    @Autowired
    JsonTradeService jsonTradeService;

    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }


    @Test
    void shouldProcessFileSuccessfully() throws Exception {
        String jsonContent = """
                [
                            {"date": "20240101", "productId": 1, "currency": "USD", "price": 100.0},
                            {"date": "20240201", "productId": 2, "currency": "USD", "price": 200.0}
                 ]
                                
                """;

        MockMultipartFile file = new MockMultipartFile("file", "test.json", MediaType.APPLICATION_JSON_VALUE, jsonContent.getBytes());

        String result = jsonTradeService.processFile(file);
        System.out.println(result);

        assertThat(result).isNotEmpty();
        assertThat(result).isNotNull();

        List<TradeDTO> trades = objectMapper().readValue(result, new TypeReference<>() {
        });

        assertThat(trades).hasSize(2);
    }

    @Test
    void shouldNotAddDataWhenDateIsInvalid() throws JsonProcessingException {
        String jsonContent = """
                [
                    {"date": "2024010", "productId": 1, "currency": "USD", "price": 100.0}
                    ]
                """;

        MockMultipartFile file = new MockMultipartFile("file", "test.json", MediaType.APPLICATION_JSON_VALUE, jsonContent.getBytes());

        String result = jsonTradeService.processFile(file);

        List<TradeDTO> trades = objectMapper().readValue(result, new TypeReference<>() {
        });

        assertThat(trades).hasSize(0);
    }

    @Test
    void shouldPastMissingNameWhenProductNotExist() throws JsonProcessingException {
        String jsonContent = """
                [
                    {"date": "20240101", "productId": 12312312, "currency": "USD", "price": 100.0}
                    ]
                """;

        MockMultipartFile file = new MockMultipartFile("file", "test.json", MediaType.APPLICATION_JSON_VALUE, jsonContent.getBytes());

        String result = jsonTradeService.processFile(file);

        List<TradeDTO> trades = objectMapper().readValue(result, new TypeReference<>() {
        });

        assertThat(trades.get(0).getProductName()).isEqualTo("Missing Product Name");
    }

}
