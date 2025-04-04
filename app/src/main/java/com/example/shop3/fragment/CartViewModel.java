package com.example.shop3.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.shop3.model.CartItem;
import com.example.shop3.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends ViewModel {
    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final DatabaseReference database;
    private final String userId;

    public CartViewModel() {
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance().getReference()
            .child("users").child(userId).child("cart");
        loadCartItems();
    }

    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    private void loadCartItems() {
        isLoading.setValue(true);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<CartItem> items = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CartItem item = snapshot.getValue(CartItem.class);
                    if (item != null) {
                        item.setId(snapshot.getKey());
                        items.add(item);
                    }
                }
                cartItems.setValue(items);
                isLoading.setValue(false);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                isLoading.setValue(false);
            }
        });
    }

    public void removeFromCart(CartItem cartItem) {
        database.child(cartItem.getId()).removeValue();
    }

    public void updateQuantity(CartItem cartItem, int quantity) {
        if (quantity <= 0) {
            removeFromCart(cartItem);
            return;
        }
        database.child(cartItem.getId()).child("quantity").setValue(quantity);
    }

    public void clearCart() {
        database.removeValue();
    }

    public void addToCart(Product product) {
        CartItem cartItem = new CartItem(product, 1);
        database.push().setValue(cartItem);
    }

    public double calculateTotal() {
        List<CartItem> items = cartItems.getValue();
        if (items == null) return 0;

        double total = 0;
        for (CartItem item : items) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        return total;
    }
}