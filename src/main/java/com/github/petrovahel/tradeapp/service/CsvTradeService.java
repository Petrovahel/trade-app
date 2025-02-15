package com.github.petrovahel.tradeapp.service;

import com.github.petrovahel.tradeapp.dto.TradeDTO;
import com.github.petrovahel.tradeapp.exception.TradeProcessingException;
import com.github.petrovahel.tradeapp.repository.ProductRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Component
public class CsvTradeService extends AbstractTradeService {

    public CsvTradeService(ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    public String processFile(MultipartFile file) {
        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVReader csvReader = new CSVReader(reader)) {

            return convertListToCsv(StreamSupport.stream(csvReader.spliterator(), false)
                    .skip(1)
                    .map(this::mapToTradeDTO)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));

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

        } catch (DateTimeParseException e) {
            log.error("Invalid date format: " + record[0], e);
        } catch (NumberFormatException e) {
            log.error("Invalid number format: " + record[1], e);
        }
        return null;
    }

    public String convertListToCsv(List<TradeDTO> trades) {
        try (StringWriter stringWriter = new StringWriter();
             CSVWriter csvWriter = new CSVWriter(stringWriter,
                     ICSVWriter.DEFAULT_SEPARATOR,
                     ICSVWriter.NO_QUOTE_CHARACTER,
                     ICSVWriter.DEFAULT_ESCAPE_CHARACTER,
                     ICSVWriter.DEFAULT_LINE_END)) {

            String[] header = {"Date", "Product Name", "Currency", "Price"};
            csvWriter.writeNext(header);

            for (TradeDTO trade : trades) {
                String[] row = {
                        trade.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                        trade.getProductName(),
                        trade.getCurrency(),
                        trade.getPrice().toString()
                };
                csvWriter.writeNext(row);
            }

            return stringWriter.toString();

        } catch (Exception e) {
            throw new TradeProcessingException("Error generating CSV", e);
        }
    }
}
