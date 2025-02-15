package com.github.petrovahel.tradeapp.service;

import com.github.petrovahel.tradeapp.entity.Product;
import com.github.petrovahel.tradeapp.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
public abstract class AbstractTradeService {
    private final ProductRepository productRepository;

    public abstract String processFile(MultipartFile file);

    public String getProductNameFromCache(int productId) {
        return productRepository.findById(productId)
                .map(Product::getName)
                .orElse("Missing Product Name");
    }
}
