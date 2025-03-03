package com.github.petrovahel.tradeapp.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.petrovahel.tradeapp.converter.*;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "Trade")
@XmlAccessorType(XmlAccessType.FIELD)
public class TradeDTO {

    @CsvBindByName(column = "productId")
    @XmlElement(name = "productId")
    @JsonAlias("productId")
    @JsonProperty("id")
    private int id;

    @CsvCustomBindByName(column = "date", converter = DateConverter.class)
    @JsonDeserialize(using = SafeLocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    @XmlElement(name = "date")
    private LocalDate date;

    @CsvBindByName(column = "currency")
    @XmlElement(name = "currency")
    private String currency;

    @CsvBindByName(column = "price")
    @XmlJavaTypeAdapter(BigDecimalAdapter.class)
    @XmlElement(name = "price")
    private BigDecimal price;

    @CsvBindByName(column = "productName")
    @XmlElement(name = "productName")
    private String productName;

    @JsonIgnore
    public int getId() {
        return id;
    }

}

