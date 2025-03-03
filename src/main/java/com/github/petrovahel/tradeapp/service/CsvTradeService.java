package com.github.petrovahel.tradeapp.service;

import com.github.petrovahel.tradeapp.dto.TradeDTO;
import com.github.petrovahel.tradeapp.exception.TradeProcessingException;
import com.github.petrovahel.tradeapp.repository.ProductRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Log4j2
public class CsvTradeService extends AbstractTradeService {

    public CsvTradeService(ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    public String processFile(MultipartFile file) {
        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVReader csvReader = new CSVReader(reader)) {

            CompletableFuture<List<TradeDTO>> processedTrades = CompletableFuture.supplyAsync(() ->
                    StreamSupport.stream(csvReader.spliterator(), true)
                            .skip(1)
                            .map(this::mapToTradeDTO)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
            );

            return processedTrades.thenApply(this::convertListToCsv).join();

        } catch (Exception e) {
            throw new TradeProcessingException("Error reading the file: " + e.getMessage(), e);
        }
    }

    private TradeDTO mapToTradeDTO(String[] record) {
        if (record.length < 4) {
            log.error("Invalid format: " + Arrays.toString(record));
            return null;
        }

        try {
            LocalDate date = LocalDate.parse(record[0].trim(), DateTimeFormatter.ofPattern("yyyyMMdd"));
            int id = Integer.parseInt(record[1].trim());
            String cur = record[2].trim();
            BigDecimal price = new BigDecimal(record[3].trim());

            if (cur.isBlank()) {
                log.error("Currency is empty: " + Arrays.toString(record));
                return null;
            }

            String productName = super.getProductNameFromCache(id);
            if ("Missing Product Name".equals(productName)) {
                log.error("Missing product name for productId: " + id);
            }

            return new TradeDTO(id, date, cur, price, productName);

        } catch (DateTimeParseException | NumberFormatException e) {
            log.error("Invalid data format: " + Arrays.toString(record), e);
            return null;
        }
    }

    private String convertListToCsv(List<TradeDTO> trades) {
        try (StringWriter stringWriter = new StringWriter();
             CSVWriter csvWriter = new CSVWriter(stringWriter,
                     ICSVWriter.DEFAULT_SEPARATOR,
                     ICSVWriter.NO_QUOTE_CHARACTER,
                     ICSVWriter.DEFAULT_ESCAPE_CHARACTER,
                     ICSVWriter.DEFAULT_LINE_END)) {

            String[] header = {"date", "productName", "currency", "price"};
            csvWriter.writeNext(header);

            trades.parallelStream()
                    .forEach(trade -> {
                        String[] row = {
                                trade.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                                trade.getProductName(),
                                trade.getCurrency(),
                                trade.getPrice().toString()
                        };
                        synchronized (csvWriter) {
                            csvWriter.writeNext(row);
                        }
                    });

            return stringWriter.toString();

        } catch (Exception e) {
            throw new TradeProcessingException("Error generating CSV", e);
        }
    }
}


