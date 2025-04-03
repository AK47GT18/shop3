package com.example.shop3.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shop3.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// HomeViewModel.java
public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<Product>> products = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public HomeViewModel() {
        loadProducts();
    }

    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void refreshProducts() {
        loadProducts();
    }

    private void loadProducts() {
        isLoading.setValue(true);
        db.collection("products")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Product> productList = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    Product product = document.toObject(Product.class);
                    if (product != null) {
                        product.setId(document.getId());
                        productList.add(product);
                    }
                }
                products.setValue(productList);
                isLoading.setValue(false);
            })
            .addOnFailureListener(e -> {
                isLoading.setValue(false);
                // Handle error
            });
    }

    public void addToCart(Product product) {
        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("productId", product.getId());
        cartItem.put("name", product.getName());
        cartItem.put("price", product.getPrice());
        cartItem.put("imageUrl", product.getImageUrl());
        cartItem.put("quantity", 1);

        db.collection("users").document(userId)
            .collection("cart")
            .add(cartItem);
    }
}