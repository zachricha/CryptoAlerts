package com.zachrrichards.CryptoAlerts.models;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String symbol;

    private Date date;

    private double price;

    private String formatPrice;

    private boolean greater;

    @ManyToOne
    private User user;

    public Alert() { date = new Date(); };

    public Alert(String symbol, double price, boolean greater, String formatPrice) {
        this();
        this.symbol = symbol;
        this.price = price;
        this.greater = greater;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isGreater() {
        return greater;
    }

    public void setGreater(boolean greater) {
        this.greater = greater;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFormatPrice() {
        return formatPrice;
    }

    public void setFormatPrice(String formatPrice) {
        this.formatPrice = formatPrice;
    }
}
