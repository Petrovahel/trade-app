package com.github.petrovahel.tradeapp.converter;

import com.opencsv.bean.AbstractBeanField;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateConverter extends AbstractBeanField<LocalDate, String> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    protected LocalDate convert(String value) {
        return LocalDate.parse(value, formatter);
    }
}
