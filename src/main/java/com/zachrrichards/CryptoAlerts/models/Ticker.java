package com.zachrrichards.CryptoAlerts.models;

import javax.persistence.*;

@Entity
public class Ticker {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(unique = true)
    private String symbol;

    private double lastPrice;

    private String formatPrice;

    public Ticker() { super(); };

    public Ticker(String symbol, double lastPrice, String formatPrice) {
        this();
        this.symbol = symbol;
        this.lastPrice = lastPrice;
        this.formatPrice = formatPrice;
    }

    public int getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getFormatPrice() {
        return formatPrice;
    }

    public void setFormatPrice(String formatPrice) {
        this.formatPrice = formatPrice;
    }
}
