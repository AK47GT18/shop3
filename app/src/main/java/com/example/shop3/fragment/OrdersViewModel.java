package com.example.shop3.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.shop3.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class OrdersViewModel extends ViewModel {
    private final MutableLiveData<List<Order>> orders = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public OrdersViewModel() {
        loadOrders();
    }

    public LiveData<List<Order>> getOrders() {
        return orders;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    private void loadOrders() {
        isLoading.setValue(true);
        db.collection("users").document(userId)
            .collection("orders")
            .addSnapshotListener((value, error) -> {
                if (error != null) {
                    isLoading.setValue(false);
                    return;
                }

                List<Order> orderList = new ArrayList<>();
                if (value != null) {
                    for (QueryDocumentSnapshot document : value) {
                        Order order = document.toObject(Order.class);
                        order.setId(document.getId());
                        orderList.add(order);
                    }
                }
                orders.setValue(orderList);
                isLoading.setValue(false);
            });
    }
}