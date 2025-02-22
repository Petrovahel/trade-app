package com.github.petrovahel.tradeapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.petrovahel.tradeapp.converter.DateConverter;
import com.github.petrovahel.tradeapp.converter.LocalDateSerializer;
import com.github.petrovahel.tradeapp.converter.SafeLocalDateDeserializer;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeDTO {

    @CsvBindByName(column = "productId")
    private int id;

    @CsvCustomBindByName(column = "date", converter = DateConverter.class)
    @JsonDeserialize(using = SafeLocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;

    @CsvBindByName(column = "currency")
    private String currency;

    @CsvBindByName(column = "price")
    private BigDecimal price;

    @CsvBindByName(column = "productName")
    private String productName;

    @JsonIgnore
    public int getId() {
        return id;
    }

    @JsonProperty("productId")
    public void setId(int id) {
        this.id = id;
    }

}

