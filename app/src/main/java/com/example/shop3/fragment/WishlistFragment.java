package com.example.shop3.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shop3.R;
import com.example.shop3.Adapter.WishlistAdapter;
import com.example.shop3.factory.ViewModelFactory;
import com.example.shop3.model.Product;
import com.example.shop3.repository.CartRepository;
import com.example.shop3.repository.WishlistRepository;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;

public class WishlistFragment extends Fragment implements WishlistAdapter.OnWishlistItemClickListener {
    private RecyclerView wishlistRecyclerView;
    private CircularProgressIndicator progressIndicator;
    private WishlistViewModel viewModel;
    private WishlistAdapter adapter;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_wishlist, container, false);
        initViews();
        setupRecyclerView();
        setupViewModel();
        setupObservers();
        return rootView;
    }

    private void initViews() {
        wishlistRecyclerView = rootView.findViewById(R.id.wishlistRecyclerView);
        progressIndicator = rootView.findViewById(R.id.progressIndicator);
    }

    private void setupRecyclerView() {
        adapter = new WishlistAdapter(new ArrayList<>());
        adapter.setOnWishlistItemClickListener(this);
        wishlistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        wishlistRecyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        WishlistRepository wishlistRepository = new WishlistRepository();
        CartRepository cartRepository = new CartRepository();
        ViewModelFactory factory = new ViewModelFactory(wishlistRepository, cartRepository);
        viewModel = new ViewModelProvider(this, factory).get(WishlistViewModel.class);
    }

    private void setupObservers() {
        viewModel.getWishlist().observe(getViewLifecycleOwner(), products -> {
            adapter.setProducts(products);
            progressIndicator.setVisibility(View.GONE);
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading ->
            progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Snackbar.make(rootView, error, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onProductClick(Product product) {
        Bundle args = new Bundle();
        args.putString("productId", product.getId());
        Navigation.findNavController(requireView())
            .navigate(R.id.action_navigation_wishlist_to_productDetailFragment, args);
    }

    @Override
    public void onRemoveClick(Product product) {
        viewModel.removeFromWishlist(product);
    }

    @Override
    public void onAddToCartClick(Product product) {
        viewModel.addToCart(product);  // Add the product to the cart
    }
}