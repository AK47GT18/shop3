package com.example.shop3.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shop3.model.Product;
import com.example.shop3.repository.WishlistRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailsViewModel extends ViewModel {

    private final MutableLiveData<Product> product = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final DatabaseReference database;
    private final String userId;
    private final WishlistRepository wishlistRepository;
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public ProductDetailsViewModel(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
        database = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Product> getProduct() {
        return product;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadProductById(String productId) {
        if (productId == null) {
            error.setValue("Product ID is null");
            return;
        }

        isLoading.setValue(true);
        database.child("products").child(productId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Product p = snapshot.getValue(Product.class);
                        if (p != null) {
                            p.setId(snapshot.getKey());
                            product.setValue(p);
                        } else {
                            error.setValue("Product not found");
                        }
                        isLoading.setValue(false);
                    }

                    @Override
                    public void onCancelled(DatabaseError e) {
                        error.setValue("Error: " + e.getMessage());
                        isLoading.setValue(false);
                    }
                });
    }

    public void addToCart(Product product) {
        if (userId == null) {
            error.setValue("Please login to add items to cart");
            return;
        }

        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("product", product);
        cartItem.put("quantity", 1);

        database.child("users").child(userId).child("cart").push()
                .setValue(cartItem)
                .addOnSuccessListener(unused -> successMessage.setValue("Product added to cart"))
                .addOnFailureListener(e -> error.setValue("Failed to add to cart: " + e.getMessage()));
    }

    public void addToWishlist(Product product) {
        wishlistRepository.addToWishlist(product, new WishlistRepository.WishlistCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                successMessage.setValue("Product added to wishlist");
            }

            @Override
            public void onError(Exception e) {
                errorMessage.setValue("Failed to add to wishlist: " + e.getMessage());
            }
        });
    }
}
