package com.github.petrovahel.tradeapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.petrovahel.tradeapp.dto.TradeDTO;
import com.github.petrovahel.tradeapp.exception.TradeProcessingException;
import com.github.petrovahel.tradeapp.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Log4j2
@Component
public class JsonTradeService extends AbstractTradeService {

    private final ObjectMapper objectMapper;

    public JsonTradeService(ProductRepository productRepository, ObjectMapper objectMapper) {
        super(productRepository);
        this.objectMapper = objectMapper;
    }

    @Override
    public String processFile(MultipartFile file) {
        try {
            List<TradeDTO> trades = objectMapper.readValue(file.getInputStream(), new TypeReference<>() {});

            CompletableFuture<List<TradeDTO>> processedTrades = CompletableFuture.supplyAsync(() ->
                    trades.parallelStream()
                            .filter(trade -> {
                                if (trade.getDate() == null) {
                                    log.error("Invalid date found for productId {}: Skipping entry", trade.getId());
                                    return false;
                                }
                                return true;
                            })
                            .peek(trade -> {
                                String productName = super.getProductNameFromCache(trade.getId());
                                if ("Missing Product Name".equals(productName)) {
                                    log.error("Product name not found for productId {}: Replacing with default", trade.getId());
                                }
                                trade.setProductName(productName);
                            })
                            .collect(Collectors.toList())
            );

            return processedTrades.thenApply(this::convertListToJson).join();

        } catch (Exception e) {
            log.error("Error processing JSON file: {}", e.getMessage());
            throw new TradeProcessingException("Error processing JSON file", e);
        }
    }

    private String convertListToJson(List<TradeDTO> trades) {
        if (trades == null || trades.isEmpty()) {
            log.warn("convertListToJson received empty list!");
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(trades);
        } catch (Exception e) {
            throw new TradeProcessingException("Error generating JSON", e);
        }
    }
}
