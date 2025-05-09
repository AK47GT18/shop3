package com.example.shop3.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.shop3.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<Product>> products = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final DatabaseReference database;
    private String userId;

    public HomeViewModel() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid();
        }
        database = FirebaseDatabase.getInstance().getReference();
        loadProducts();
    }

    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void refreshProducts() {
        loadProducts();
    }

    private void loadProducts() {
        isLoading.setValue(true);
        database.child("products")
            .orderByChild("timestamp")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Product> productList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Product product = snapshot.getValue(Product.class);
                        if (product != null) {
                            product.setId(snapshot.getKey());
                            productList.add(product);
                        }
                    }
                    products.setValue(productList);
                    isLoading.setValue(false);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    error.setValue(databaseError.getMessage());
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

        database.child("users")
            .child(userId)
            .child("cart")
            .push()
            .setValue(cartItem)
            .addOnSuccessListener(unused -> {
                // Handle success if needed
            })
            .addOnFailureListener(e -> error.setValue(e.getMessage()));
    }
}