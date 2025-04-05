package com.example.shop3.fragment;

    import androidx.lifecycle.LiveData;
    import androidx.lifecycle.MutableLiveData;
    import androidx.lifecycle.ViewModel;
    import com.example.shop3.model.CartItem;
    import com.example.shop3.model.Order;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;
    import java.util.ArrayList;
    import java.util.Date;
    import java.util.List;

    public class CheckoutViewModel extends ViewModel {
        private final MutableLiveData<Double> totalAmount = new MutableLiveData<>();
        private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
        private final MutableLiveData<String> message = new MutableLiveData<>();
        private final DatabaseReference database;
        private final String userId;

        public interface CheckoutCallback {
            void onSuccess();
        }

        public CheckoutViewModel() {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            database = FirebaseDatabase.getInstance().getReference();
            calculateTotalAmount();
        }

        public LiveData<Double> getTotalAmount() {
            return totalAmount;
        }

        public LiveData<Boolean> getIsLoading() {
            return isLoading;
        }

        public LiveData<String> getMessage() {
            return message;
        }

        private void calculateTotalAmount() {
            isLoading.setValue(true);
            database.child("users").child(userId).child("cart")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        double total = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            CartItem item = snapshot.getValue(CartItem.class);
                            if (item != null) {
                                total += item.getProduct().getPrice() * item.getQuantity();
                            }
                        }
                        totalAmount.setValue(total);
                        isLoading.setValue(false);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        isLoading.setValue(false);
                        message.setValue("Error calculating total");
                    }
                });
        }

        public void confirmOrder(double enteredAmount, CheckoutCallback callback) {
            if (totalAmount.getValue() == null) {
                message.setValue("Error: Total amount not available");
                return;
            }

            double total = totalAmount.getValue();
            if (Math.abs(enteredAmount - total) > 0.01) { // Using small delta for double comparison
                message.setValue("Entered amount does not match the total");
                return;
            }

            isLoading.setValue(true);
            database.child("users").child(userId).child("cart")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<CartItem> cartItems = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            CartItem item = snapshot.getValue(CartItem.class);
                            if (item != null) {
                                cartItems.add(item);
                            }
                        }

                        Order order = new Order(null, cartItems, total, new Date().getTime());

                        String orderId = database.child("users").child(userId)
                            .child("orders").push().getKey();

                        if (orderId != null) {
                            order.setId(orderId);
                            order.setItems(cartItems);
                            database.child("users").child(userId)
                                .child("orders").child(orderId)
                                .setValue(order)
                                .addOnSuccessListener(aVoid -> {
                                    clearCart();
                                    message.setValue("Order confirmed successfully");
                                    callback.onSuccess();
                                })
                                .addOnFailureListener(e -> {
                                    isLoading.setValue(false);
                                    message.setValue("Error confirming order");
                                });
                        } else {
                            isLoading.setValue(false);
                            message.setValue("Error generating order ID");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        isLoading.setValue(false);
                        message.setValue("Error processing order");
                    }
                });
        }

        private void clearCart() {
            database.child("users").child(userId).child("cart")
                .removeValue()
                .addOnSuccessListener(aVoid -> isLoading.setValue(false))
                .addOnFailureListener(e -> {
                    isLoading.setValue(false);
                    message.setValue("Error clearing cart");
                });
        }
    }