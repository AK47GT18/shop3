package com.example.shop3.model;

import java.util.Date;

public class Order {
    private String id;
    private Date date;
    private double total;

    public Order() {
        // Required empty constructor for Firestore
    }

    public Order(String id, Date date, double total) {
        this.id = id;
        this.date = date;
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}