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

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class XmlTradeServiceTest {

    @Autowired
    XmlTradeService xmlTradeService;

    @Test
    void shouldProcessFileSuccessfully() {
        String xmlContent = """
    <Trades>
        <Trade>
            <date>20240101</date>
            <productId>1</productId>
            <currency>USD</currency>
            <price>100.0</price>
        </Trade>
        <Trade>
            <date>20240201</date>
            <productId>2</productId>
            <currency>USD</currency>
            <price>200.0</price>
        </Trade>
    </Trades>
    """;


        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.xml",
                MediaType.APPLICATION_XML_VALUE,
                xmlContent.getBytes(StandardCharsets.UTF_8)
        );

        String result = xmlTradeService.processFile(file);

        assertThat(result).isNotEmpty();
        assertThat(result).contains("20240101", "20240101", "USD", "100.0", "200.0");
    }

    @Test
    void shouldNotAddDataWhenDateIsInvalid() {
        String xmlContent = """
    <Trades>
        <Trade>
            <date>2024010</date>
            <productId>1</productId>
            <currency>USD</currency>
            <price>100.0</price>
        </Trade>
    </Trades>
    """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.xml",
                MediaType.APPLICATION_XML_VALUE,
                xmlContent.getBytes(StandardCharsets.UTF_8)
        );
        String result = xmlTradeService.processFile(file);

        assertThat(result).doesNotContain("2024010,1,USD,100.0");
    }

    @Test
    void shouldPastMissingNameWhenProductNotExist() {
        String xmlContent = """
    <Trades>
        <Trade>
            <date>20240101</date>
            <productId>12312312</productId>
            <currency>USD</currency>
            <price>100.0</price>
        </Trade>
    </Trades>
    """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.xml",
                MediaType.APPLICATION_XML_VALUE,
                xmlContent.getBytes(StandardCharsets.UTF_8)
        );

        String result = xmlTradeService.processFile(file);

        assertThat(result).contains("2024010", "Missing Product Name", "USD", "100.0");
    }
}
