package com.github.petrovahel.tradeapp.service;

import com.github.petrovahel.tradeapp.exception.TradeProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
@Slf4j
public class FileProcessingService {

    private final CsvTradeService csvTradeService;
    private final JsonTradeService jsonTradeService;
    private final XmlTradeService xmlTradeService;

    public String processFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new TradeProcessingException("File name is missing");
        }

        String format = determineFormatByExtension(filename);

        if (format.equals("csv")) {
            return csvTradeService.processFile(file);
        } else if (format.equals("json")) {
            return jsonTradeService.processFile(file);
        } else if (format.equals("xml")) {
            return xmlTradeService.processFile(file);
        }
        throw new TradeProcessingException("File type not supported " + format);

    }

    private String determineFormatByExtension(String filename) {
        if (filename.endsWith(".csv")) {
            return "csv";
        } else if (filename.endsWith(".json")) {
            return "json";
        } else if (filename.endsWith(".xml")) {
            return "xml";
        } else {
            throw new TradeProcessingException("Unsupported file extension");
        }
    }
}
