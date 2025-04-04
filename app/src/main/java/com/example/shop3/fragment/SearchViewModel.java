// SearchViewModel.java
package com.example.shop3.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.shop3.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends ViewModel {
    private final MutableLiveData<List<Product>> products = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final DatabaseReference database;

    public SearchViewModel() {
        database = FirebaseDatabase.getInstance().getReference().child("products");
    }

    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void searchProducts(String query, String category, float minPrice, float maxPrice) {
        isLoading.setValue(true);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Product> filteredList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        product.setId(snapshot.getKey());
                        if (matchesSearchCriteria(product, query, category, minPrice, maxPrice)) {
                            filteredList.add(product);
                        }
                    }
                }
                products.setValue(filteredList);
                isLoading.setValue(false);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                isLoading.setValue(false);
            }
        });
    }

    private boolean matchesSearchCriteria(Product product, String query, String category, 
                                        float minPrice, float maxPrice) {
        boolean matchesQuery = query.isEmpty() || 
            product.getName().toLowerCase().contains(query.toLowerCase());
        boolean matchesCategory = category.isEmpty() || 
            product.getCategory().equals(category);
        boolean matchesPrice = product.getPrice() >= minPrice && 
            product.getPrice() <= maxPrice;
        
        return matchesQuery && matchesCategory && matchesPrice;
    }
}