package com.example.shop3.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.shop3.model.CartItem;
import com.example.shop3.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends ViewModel {
    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public CartViewModel() {
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
        db.collection("users").document(userId)
            .collection("cart")
            .addSnapshotListener((value, error) -> {
                if (error != null) {
                    isLoading.setValue(false);
                    return;
                }

                List<CartItem> items = new ArrayList<>();
                if (value != null) {
                    for (QueryDocumentSnapshot document : value) {
                        CartItem item = document.toObject(CartItem.class);
                        item.setId(document.getId());
                        items.add(item);
                    }
                }
                cartItems.setValue(items);
                isLoading.setValue(false);
            });
    }

    public void removeFromCart(CartItem cartItem) {
        db.collection("users").document(userId)
            .collection("cart")
            .document(cartItem.getId())
            .delete();
    }

    public void updateQuantity(CartItem cartItem, int quantity) {
        if (quantity <= 0) {
            removeFromCart(cartItem);
            return;
        }

        db.collection("users").document(userId)
            .collection("cart")
            .document(cartItem.getId())
            .update("quantity", quantity);
    }

    public void clearCart() {
        List<CartItem> items = cartItems.getValue();
        if (items != null) {
            for (CartItem item : items) {
                removeFromCart(item);
            }
        }
    }

    public void addToCart(Product product) {
        CartItem cartItem = new CartItem(product, 1);
        db.collection("users").document(userId)
            .collection("cart")
            .add(cartItem);
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