package com.example.shop3.repository;

import com.example.shop3.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class OrdersRepository {
    private final DatabaseReference ordersRef;
    private final String userId;

    public OrdersRepository() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.ordersRef = database.getReference("orders");
        // TODO: Get actual user ID from authentication
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public interface OrderCallback {
        void onSuccess();
        void onError(Exception e);
    }

    public interface OrdersCallback {
        void onSuccess(List<Order> orders);
        void onError(Exception e);
    }

    public void createOrder(Order order, OrderCallback callback) {
        String orderId = ordersRef.child(userId).push().getKey();
        order.setId(orderId);
        ordersRef.child(userId).child(orderId)
                .setValue(order)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e));
    }

    public void getUserOrders(OrdersCallback callback) {
        ordersRef.child(userId).get()
                .addOnSuccessListener(snapshot -> {
                    List<Order> orders = new ArrayList<>();
                    for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                        Order order = orderSnapshot.getValue(Order.class);
                        if (order != null) {
                            orders.add(order);
                        }
                    }
                    callback.onSuccess(orders);
                })
                .addOnFailureListener(e -> callback.onError(e));
    }
}