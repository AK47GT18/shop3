package com.example.shop3.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shop3.R;
import com.example.shop3.Adapter.ProductAdapter;
import com.example.shop3.model.Product;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;
import java.util.List;
import android.text.TextWatcher;
import android.text.Editable;

public class SearchFragment extends Fragment {
    private TextInputEditText searchEditText;
    private MaterialAutoCompleteTextView categorySpinner;
    private RangeSlider priceRangeSlider;
    private RecyclerView productsRecyclerView;
    private ProductAdapter productAdapter;
    private DatabaseReference database;
    private SearchViewModel viewModel;
    private List<Product> productList;
    private float minPrice = 0f;
    private float maxPrice = 10000f;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, 
                            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initViews(view);
        setupViews();
        initDatabase();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        setupObservers();
    }

    private void initViews(View view) {
        searchEditText = view.findViewById(R.id.searchEditText);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        priceRangeSlider = view.findViewById(R.id.priceRangeSlider);
        productsRecyclerView = view.findViewById(R.id.productsRecyclerView);
    }

    private void setupViews() {
        productAdapter = new ProductAdapter(new ArrayList<>());
        productsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productsRecyclerView.setAdapter(productAdapter);

        // Setup category spinner
        String[] categories = {"All Categories", "Electronics", "Clothing", "Books", "Home & Kitchen"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
            android.R.layout.simple_dropdown_item_1line, categories);
        categorySpinner.setAdapter(adapter);

        // Setup price range slider
        priceRangeSlider.setValueFrom(minPrice);
        priceRangeSlider.setValueTo(maxPrice);
        priceRangeSlider.setValues(minPrice, maxPrice);

        // Setup listeners
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        categorySpinner.setOnItemClickListener((parent, view, position, id) -> performSearch());

        priceRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                performSearch();
            }
        });
    }

    private void initDatabase() {
        database = FirebaseDatabase.getInstance().getReference().child("products");
    }

    private void setupObservers() {
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            productAdapter.setProducts(products);
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Handle loading state
        });
    }

    private void performSearch() {
        String query = searchEditText.getText().toString();
        String category = categorySpinner.getText().toString();
        viewModel.searchProducts(query, category, minPrice, maxPrice);
    }
}