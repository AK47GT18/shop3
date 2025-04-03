package com.example.shop3.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.shop3.model.CartItem;
import com.example.shop3.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;
import java.util.List;

public class CheckoutViewModel extends ViewModel {
    private final MutableLiveData<Double> totalAmount = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public CheckoutViewModel() {
        calculateTotalAmount();
    }

    public LiveData<Double> getTotalAmount() {
        return totalAmount;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    private void calculateTotalAmount() {
        isLoading.setValue(true);
        db.collection("users").document(userId)
            .collection("cart")
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    double total = 0;
                    for (CartItem item : task.getResult().toObjects(CartItem.class)) {
                        total += item.getProduct().getPrice() * item.getQuantity();
                    }
                    totalAmount.setValue(total);
                }
                isLoading.setValue(false);
            });
    }

    public void confirmOrder() {
        isLoading.setValue(true);
        db.collection("users").document(userId)
            .collection("cart")
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<CartItem> cartItems = task.getResult().toObjects(CartItem.class);
                    double total = 0;
                    for (CartItem item : cartItems) {
                        total += item.getProduct().getPrice() * item.getQuantity();
                    }
                    Order order = new Order(null, new Date(), total);
                    db.collection("users").document(userId)
                        .collection("orders")
                        .add(order)
                        .addOnCompleteListener(orderTask -> {
                            if (orderTask.isSuccessful()) {
                                clearCart();
                            }
                            isLoading.setValue(false);
                        });
                } else {
                    isLoading.setValue(false);
                }
            });
    }

    private void clearCart() {
        db.collection("users").document(userId)
            .collection("cart")
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (CartItem item : task.getResult().toObjects(CartItem.class)) {
                        db.collection("users").document(userId)
                            .collection("cart")
                            .document(item.getId())
                            .delete();
                    }
                }
            });
    }
}