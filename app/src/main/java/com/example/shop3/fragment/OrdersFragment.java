package com.example.shop3.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shop3.R;
import com.example.shop3.Adapter.OrdersAdapter;
import com.example.shop3.model.Order;
import android.widget.ProgressBar;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;

public class OrdersFragment extends Fragment {
    private OrdersViewModel viewModel;
    private RecyclerView ordersRecyclerView;
    private OrdersAdapter adapter;
    private ProgressBar progressIndicator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        initViews(view);
        setupRecyclerView();
        setupViewModel();
        observeViewModel();
        return view;
    }

    private void initViews(View view) {
        ordersRecyclerView = view.findViewById(R.id.ordersRecyclerView);
        progressIndicator = view.findViewById(R.id.progressIndicator);
    }

    private void setupRecyclerView() {
        adapter = new OrdersAdapter(new ArrayList<>());
        adapter.setOnOrderDeleteListener(order -> viewModel.deleteOrder(order));
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ordersRecyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(OrdersViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            adapter.setOrders(orders);
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}