package com.example.shop3.repository;

import com.example.shop3.model.Product;

public class CartRepository {
    public interface CartCallback {
        void onSuccess();
        void onError(Exception e);
    }

    public void addToCart(Product product, int quantity, CartCallback callback) {
        // TODO: Implement actual API call
    }
}