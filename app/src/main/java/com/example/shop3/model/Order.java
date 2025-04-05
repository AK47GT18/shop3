package com.example.shop3.model;

import java.util.List;

public class Order {
    private String id;
    private List<CartItem> items;
    private double total;
    private long date; // Use timestamp (milliseconds) for compatibility with Date()

    public Order() {
        // Required empty constructor for Firebase
    }

    public Order(String id, List<CartItem> items, double total, long date) {
        this.id = id;
        this.items = items;
        this.total = total;
        this.date = date;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }
}
