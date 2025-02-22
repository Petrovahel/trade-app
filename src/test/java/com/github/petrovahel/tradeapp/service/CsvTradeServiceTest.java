package com.github.petrovahel.tradeapp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CsvTradeServiceTest {

    @Autowired
    CsvTradeService csvTradeService;

    @Test
    void shouldProcessFileSuccessfully() {
        String csvContent = """
                date,productId,currency,price
                20240101,1,USD,100.0
                20240201,2,USD,200.0
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                MediaType.TEXT_PLAIN_VALUE,
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        String result = csvTradeService.processFile(file);

        assertThat(result).isNotEmpty();
        assertThat(result).contains("20240101", "20240101", "USD", "100.0", "200.0");
    }

    @Test
    void shouldNotAddDataWhenDateIsInvalid() {
        String csvContent = """
                date,productId,currency,price
                2024010,1,USD,100.0
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                MediaType.TEXT_PLAIN_VALUE,
                csvContent.getBytes(StandardCharsets.UTF_8)
        );
        String result = csvTradeService.processFile(file);

        assertEquals("date,productName,currency,price\n", result);
    }

    @Test
    void shouldPastMissingNameWhenProductNotExist() {
        String csvContent = """
                date,productId,currency,price
                20240101,12312312,USD,100.0
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                MediaType.TEXT_PLAIN_VALUE,
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        String result = csvTradeService.processFile(file);

        assertThat(result).contains("2024010", "Missing Product Name", "USD", "100.0");
    }
}
