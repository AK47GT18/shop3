package com.example.shop3.repository;

import com.example.shop3.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class WishlistRepository {
    private final DatabaseReference database;
    private final String userId;

    public interface WishlistCallback {
        void onSuccess(List<Product> products);
        void onError(Exception e);
    }

    public WishlistRepository() {
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance().getReference()
            .child("users").child(userId).child("wishlist");
    }

    public void getWishlist(WishlistCallback callback) {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Product> products = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        product.setId(snapshot.getKey());
                        products.add(product);
                    }
                }
                callback.onSuccess(products);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }

    public void removeFromWishlist(Product product, WishlistCallback callback) {
        database.child(product.getId()).removeValue()
            .addOnSuccessListener(aVoid -> getWishlist(callback))
            .addOnFailureListener(callback::onError);
    }

    public void addToWishlist(Product product, WishlistCallback callback) {
        database.child(product.getId()).setValue(product)
            .addOnSuccessListener(aVoid -> getWishlist(callback))
            .addOnFailureListener(callback::onError);
    }
}