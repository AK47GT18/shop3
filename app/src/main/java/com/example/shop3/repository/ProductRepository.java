package com.example.shop3.repository;

import com.example.shop3.model.Product;

public class ProductRepository {
    public interface ProductCallback {
        void onSuccess(Product product);
        void onError(Exception e);
    }

    public void getProduct(String productId, ProductCallback callback) {
        // TODO: Implement actual API call
    }
}