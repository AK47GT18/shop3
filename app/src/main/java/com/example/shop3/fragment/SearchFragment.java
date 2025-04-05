package com.example.shop3.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shop3.R;
import com.example.shop3.Adapter.ProductAdapter;
import com.example.shop3.model.Product;
import com.example.shop3.repository.ProductRepository;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

public class SearchFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private SearchViewModel viewModel;
    private ProductAdapter adapter;
    private TextInputEditText searchEditText;
    private TextInputEditText priceEditText;
    private MaterialAutoCompleteTextView categorySpinner;
    private ProgressBar progressIndicator;
    private RecyclerView productsRecyclerView;
    private ProductRepository productRepository;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initViews(view);
        setupViewModel();
        setupRecyclerView();
        setupSearch();
        observeViewModel();
        return view;
    }

    private void initViews(View view) {
        searchEditText = view.findViewById(R.id.searchEditText);
        priceEditText = view.findViewById(R.id.priceEditText);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        progressIndicator = view.findViewById(R.id.progressIndicator);
        productsRecyclerView = view.findViewById(R.id.productsRecyclerView);
        productRepository = new ProductRepository();

        // Add categories to spinner
        String[] categories = {"All", "Electronics", "Clothing", "Books", "Home", "Beauty"};
        categorySpinner.setSimpleItems(categories);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(requireContext());
        adapter.setOnProductClickListener(this);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        productsRecyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> performSearch();
                handler.postDelayed(searchRunnable, 300); // 300ms debounce time
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        searchEditText.addTextChangedListener(textWatcher);
        priceEditText.addTextChangedListener(textWatcher);

        categorySpinner.setOnItemClickListener((parent, view, position, id) -> {
            if (searchRunnable != null) {
                handler.removeCallbacks(searchRunnable);
            }
            searchRunnable = this::performSearch;
            handler.postDelayed(searchRunnable, 300); // 300ms debounce time
        });
    }

    private void performSearch() {
        String query = searchEditText.getText() != null ?
                searchEditText.getText().toString().trim() : "";
        String category = categorySpinner.getText() != null ?
                categorySpinner.getText().toString().trim() : "All";
        String price = priceEditText.getText() != null ?
                priceEditText.getText().toString().trim() : "";

        if (price.isEmpty()) {
            price = "999999"; // Default max price
        }

        viewModel.searchProducts(query, category, price);
    }

    private void observeViewModel() {
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                adapter.setProducts(products);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            productsRecyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAddToCartClick(Product product) {
        productRepository.addToCart(product, new ProductRepository.CartCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(requireContext(),
                        "Added to cart: " + product.getName(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(requireContext(),
                        "Error adding to cart: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onProductClick(Product product) {
        Bundle args = new Bundle();
        args.putString("productId", product.getId());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_navigation_search_to_productDetailFragment, args);
    }
}
