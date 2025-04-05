package com.example.shop3.fragment;

                   import androidx.lifecycle.LiveData;
                   import androidx.lifecycle.MutableLiveData;
                   import androidx.lifecycle.ViewModel;
                   import com.example.shop3.model.Order;
                   import com.google.firebase.auth.FirebaseAuth;
                   import com.google.firebase.database.DataSnapshot;
                   import com.google.firebase.database.DatabaseError;
                   import com.google.firebase.database.DatabaseReference;
                   import com.google.firebase.database.FirebaseDatabase;
                   import com.google.firebase.database.ValueEventListener;
                   import java.util.ArrayList;
                   import java.util.List;

                   public class OrdersViewModel extends ViewModel {
                       private final MutableLiveData<List<Order>> orders = new MutableLiveData<>();
                       private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
                       private final MutableLiveData<String> message = new MutableLiveData<>();
                       private final DatabaseReference database;
                       private final String userId;

                       public OrdersViewModel() {
                           userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                           database = FirebaseDatabase.getInstance().getReference();
                           loadOrders();
                       }

                       public LiveData<List<Order>> getOrders() {
                           return orders;
                       }

                       public LiveData<Boolean> getIsLoading() {
                           return isLoading;
                       }

                       public LiveData<String> getMessage() {
                           return message;
                       }

                       private void loadOrders() {
                           isLoading.setValue(true);
                           database.child("users").child(userId).child("orders")
                                   .addListenerForSingleValueEvent(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(DataSnapshot dataSnapshot) {
                                           List<Order> orderList = new ArrayList<>();
                                           for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                               Order order = snapshot.getValue(Order.class);
                                               if (order != null) {
                                                   order.setId(snapshot.getKey());
                                                   orderList.add(order);
                                               }
                                           }
                                           orders.setValue(orderList);
                                           isLoading.setValue(false);
                                       }

                                       @Override
                                       public void onCancelled(DatabaseError error) {
                                           isLoading.setValue(false);
                                           message.setValue("Error loading orders: " + error.getMessage());
                                       }
                                   });
                       }

                       public void deleteOrder(Order order) {
                           isLoading.setValue(true);
                           database.child("users").child(userId).child("orders").child(order.getId())
                                   .removeValue()
                                   .addOnSuccessListener(aVoid -> {
                                       List<Order> currentOrders = orders.getValue();
                                       if (currentOrders != null) {
                                           currentOrders.remove(order);
                                           orders.setValue(currentOrders);
                                       }
                                       message.setValue("Order deleted successfully");
                                       isLoading.setValue(false);
                                   })
                                   .addOnFailureListener(e -> {
                                       message.setValue("Error deleting order: " + e.getMessage());
                                       isLoading.setValue(false);
                                   });
                       }
                   }