package com.github.petrovahel.tradeapp.converter;


import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public LocalDate unmarshal(String v) {
        return (v == null || v.isEmpty()) ? null : LocalDate.parse(v, FORMATTER);
    }

    @Override
    public String marshal(LocalDate v) {
        return (v == null) ? null : FORMATTER.format(v);
    }
}
