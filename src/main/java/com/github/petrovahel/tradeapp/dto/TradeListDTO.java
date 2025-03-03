package com.github.petrovahel.tradeapp.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "Trades")
@XmlAccessorType(XmlAccessType.FIELD)
public class TradeListDTO {

    @XmlElement(name = "Trade")
    private List<TradeDTO> trades;

    public TradeListDTO() {}

    public TradeListDTO(List<TradeDTO> trades) {
        this.trades = trades;
    }

    public List<TradeDTO> getTrades() {
        return trades;
    }

    public void setTrades(List<TradeDTO> trades) {
        this.trades = trades;
    }
}
