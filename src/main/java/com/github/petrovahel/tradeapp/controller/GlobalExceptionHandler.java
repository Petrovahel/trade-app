package com.github.petrovahel.tradeapp.controller;

import com.github.petrovahel.tradeapp.exception.TradeProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TradeProcessingException.class)
    public ResponseEntity<String> handleTradeProcessingException(TradeProcessingException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}

