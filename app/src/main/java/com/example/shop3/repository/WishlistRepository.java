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
    private final DatabaseReference wishlistRef;
    private final String userId;

    public WishlistRepository() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.wishlistRef = database.getReference("wishlists");
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public interface WishlistCallback {
        void onSuccess(List<Product> products);
        void onError(Exception e);
    }

    public void getWishlist(WishlistCallback callback) {
        wishlistRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Product> products = new ArrayList<>();
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    if (product != null) {
                        products.add(product);
                    }
                }
                callback.onSuccess(products);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(new Exception("Failed to load wishlist: " + databaseError.getMessage()));
            }
        });
    }

    public void addToWishlist(Product product, WishlistCallback callback) {
        wishlistRef.child(userId).child(product.getId())
                .setValue(product)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e));
    }

    public void removeFromWishlist(Product product, WishlistCallback callback) {
        wishlistRef.child(userId).child(product.getId())
                .removeValue()
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e));
    }
}
