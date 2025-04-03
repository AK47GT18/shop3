package com.example.shop3.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shop3.R;
import com.example.shop3.Adapter.ProductAdapter;
import com.example.shop3.model.Product;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;
import android.text.TextWatcher;
import android.text.Editable;
import com.google.firebase.firestore.DocumentSnapshot;
import android.widget.Toast;
public class SearchFragment extends Fragment {
    private TextInputEditText searchEditText;
    private MaterialAutoCompleteTextView categorySpinner;
    private RangeSlider priceRangeSlider;
    private RecyclerView productsRecyclerView;
    private ProductAdapter productAdapter;
    private FirebaseFirestore db;
    private List<Product> productList;
    private float minPrice = 0f;
    private float maxPrice = 10000f;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupViews();
        initFirestore();
        loadProducts();
    }

    private void initViews(View view) {
        searchEditText = view.findViewById(R.id.searchEditText);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        priceRangeSlider = view.findViewById(R.id.priceRangeSlider);
        productsRecyclerView = view.findViewById(R.id.productsRecyclerView);
    }

    private void setupViews() {
        // Setup category spinner
        String[] categories = {"All Categories", "Electronics", "Clothing", "Books", "Home & Kitchen"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
            android.R.layout.simple_dropdown_item_1line, categories);
        categorySpinner.setAdapter(adapter);

        // Setup price range slider
        priceRangeSlider.setValueFrom(minPrice);
        priceRangeSlider.setValueTo(maxPrice);
        priceRangeSlider.setValues(minPrice, maxPrice);

        // Setup RecyclerView
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList);
        productsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        productsRecyclerView.setAdapter(productAdapter);

        // Setup listeners
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        categorySpinner.setOnItemClickListener((parent, view, position, id) -> filterProducts());

        priceRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                filterProducts();
            }
        });
    }

    private void initFirestore() {
        db = FirebaseFirestore.getInstance();
    }

    private void loadProducts() {
        db.collection("products")
            .orderBy("name")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                productList.clear();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    Product product = document.toObject(Product.class);
                    if (product != null) {
                        product.setId(document.getId());
                        productList.add(product);
                    }
                }
                productAdapter.notifyDataSetChanged();
            })
            .addOnFailureListener(e ->
                Toast.makeText(requireContext(), "Error loading products", Toast.LENGTH_SHORT).show());
    }

    private void filterProducts() {
        String searchQuery = searchEditText.getText().toString().toLowerCase().trim();
        String selectedCategory = categorySpinner.getText().toString();
        List<Float> priceRange = priceRangeSlider.getValues();
        float minSelectedPrice = priceRange.get(0);
        float maxSelectedPrice = priceRange.get(1);

        Query query = db.collection("products");

        if (!selectedCategory.equals("All Categories")) {
            query = query.whereEqualTo("category", selectedCategory);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Product> filteredList = new ArrayList<>();
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                Product product = document.toObject(Product.class);
                if (product != null) {
                    product.setId(document.getId());
                    boolean matchesSearch = product.getName().toLowerCase().contains(searchQuery) ||
                                         product.getDescription().toLowerCase().contains(searchQuery);
                    boolean matchesPrice = product.getPrice() >= minSelectedPrice &&
                                         product.getPrice() <= maxSelectedPrice;

                    if (matchesSearch && matchesPrice) {
                        filteredList.add(product);
                    }
                }
            }
            productList.clear();
            productList.addAll(filteredList);
            productAdapter.notifyDataSetChanged();
        });
    }
}