package com.github.petrovahel.tradeapp.service;

import com.github.petrovahel.tradeapp.dto.TradeDTO;
import com.github.petrovahel.tradeapp.dto.TradeListDTO;
import com.github.petrovahel.tradeapp.exception.TradeProcessingException;
import com.github.petrovahel.tradeapp.repository.ProductRepository;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Component
public class XmlTradeService extends AbstractTradeService {

    public XmlTradeService(ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    public String processFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            JAXBContext context = JAXBContext.newInstance(TradeListDTO.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            TradeListDTO tradeList = (TradeListDTO) unmarshaller.unmarshal(inputStream);

            CompletableFuture<List<TradeDTO>> processedTrades = CompletableFuture.supplyAsync(() ->
                    tradeList.getTrades().parallelStream()
                            .map(this::processTrade)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
            );

            return processedTrades.thenApply(this::convertListToXml).join();
        } catch (JAXBException | IOException e) {
            log.error("Error processing XML file: {}", e.getMessage(), e);
            throw new TradeProcessingException("Error reading the file", e);
        }
    }

    private TradeDTO processTrade(TradeDTO trade) {
        if (trade.getDate() == null) {
            log.error("Invalid date found for productId {}: Skipping entry", trade.getId());
            return null;
        }

        String productName = super.getProductNameFromCache(trade.getId());
        if ("Missing Product Name".equals(productName)) {
            log.warn("Product name not found for productId {}: Replacing with default", trade.getId());
        }

        trade.setProductName(productName);
        return trade;
    }

    private String convertListToXml(List<TradeDTO> trades) {
        try {
            TradeListDTO tradeListDTO = new TradeListDTO(trades);
            JAXBContext context = JAXBContext.newInstance(TradeListDTO.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            try (StringWriter writer = new StringWriter()) {
                marshaller.marshal(tradeListDTO, writer);
                return writer.toString();
            }
        } catch (JAXBException | IOException e) {
            log.error("Error converting to XML: {}", e.getMessage(), e);
            throw new TradeProcessingException("Error converting to XML", e);
        }
    }
}
