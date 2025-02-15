package com.github.petrovahel.tradeapp.service;

import com.github.petrovahel.tradeapp.entity.Product;
import com.github.petrovahel.tradeapp.exception.TradeProcessingException;
import com.github.petrovahel.tradeapp.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@Log4j2
@AllArgsConstructor
public class ProductService {

    private static final String PATH = "src/main/resources/product.csv";
    private final ProductRepository productRepository;


    @PostConstruct
    public void loadProductData() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(PATH))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                Product product = parseProduct(line);
                if (product != null) {
                    productRepository.save(product);
                }
            }
        } catch (IOException e) {
            throw new TradeProcessingException("Error reading product.csv", e);
        }
    }

    private Product parseProduct(String line) {
        try {
            String[] fields = line.split(",");
            if (fields.length < 2) {
                log.error("Product invalid " + line);
                return null;
            }

            String id = fields[0].trim();
            String name = fields[1].trim();

            if (!name.isBlank()) {
                return new Product(Integer.parseInt(id), name);
            } else {
                log.error("Product name is empty ");
            }
        } catch (NumberFormatException e) {
            log.error("Product invalid " + line, e);
        }
        return null;
    }
}
