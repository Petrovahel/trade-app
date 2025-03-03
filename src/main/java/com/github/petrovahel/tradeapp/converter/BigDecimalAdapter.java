package com.github.petrovahel.tradeapp.converter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.math.BigDecimal;

public class BigDecimalAdapter extends XmlAdapter<String, BigDecimal> {

    @Override
    public BigDecimal unmarshal(String v) {
        return (v == null || v.isEmpty()) ? null : new BigDecimal(v);
    }

    @Override
    public String marshal(BigDecimal v) {
        return (v == null) ? null : v.toString();
    }
}
