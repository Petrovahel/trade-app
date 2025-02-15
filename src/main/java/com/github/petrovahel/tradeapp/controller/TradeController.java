package com.github.petrovahel.tradeapp.controller;

import com.github.petrovahel.tradeapp.service.FileProcessingService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class TradeController {

    private final FileProcessingService fileProcessingService;

    @PostMapping(value = "/enrich")
    public ResponseEntity<String> enrich(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(fileProcessingService.processFile(file));
    }
}
