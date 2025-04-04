package com.example.shop3.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
        import com.example.shop3.R;
import com.example.shop3.Adapter.OrdersAdapter;

public class OrdersFragment extends Fragment {
    private RecyclerView ordersRecyclerView;
    private ProgressBar progressIndicator;
    private OrdersViewModel viewModel;
    private OrdersAdapter adapter;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_orders, container, false);
        initViews();
        setupRecyclerView();
        setupViewModel();
        setupObservers();
        return rootView;
    }

    private void initViews() {
        ordersRecyclerView = rootView.findViewById(R.id.ordersRecyclerView);
        progressIndicator = rootView.findViewById(R.id.progressIndicator);
    }

    private void setupRecyclerView() {
        adapter = new OrdersAdapter();
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ordersRecyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(OrdersViewModel.class);
    }

    private void setupObservers() {
        viewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            adapter.setOrders(orders);
            progressIndicator.setVisibility(View.GONE);
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading ->
            progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE));
    }
}