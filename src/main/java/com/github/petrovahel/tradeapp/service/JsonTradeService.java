package com.github.petrovahel.tradeapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.petrovahel.tradeapp.dto.TradeDTO;
import com.github.petrovahel.tradeapp.exception.TradeProcessingException;
import com.github.petrovahel.tradeapp.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

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
        List<TradeDTO> tradeList = new ArrayList<>();
        try {
            List<TradeDTO> trades = objectMapper.readValue(file.getInputStream(), new TypeReference<>() {
            });

            for (TradeDTO trade : trades) {
                if (trade.getDate() == null) {
                    log.error("Invalid date found for productId {}: Skipping entry", trade.getId());
                    continue;
                }

                String productName = super.getProductNameFromCache(trade.getId());
                if (productName.equals("Missing Product Name")) {
                    log.error("Product name not found for productId {}: Replacing with default", trade.getId());
                }
                trade.setProductName(productName);

                tradeList.add(trade);
            }

        } catch (Exception e) {
            log.error("Error processing JSON file: {}", e.getMessage());
        }
        return convertListToJson(tradeList);
    }

    public String convertListToJson(List<TradeDTO> trades) {
        try {
            return objectMapper.writeValueAsString(trades);
        } catch (Exception e) {
            throw new TradeProcessingException("Error generating JSON", e);
        }
    }
}
