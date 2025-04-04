package com.example.shop3.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
                import com.example.shop3.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

public class CheckoutFragment extends Fragment {
    private TextView totalAmountText;
    private MaterialButton confirmOrderButton;
    private ProgressBar progressIndicator;
    private CheckoutViewModel viewModel;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_checkout, container, false);
        initViews();
        setupViewModel();
        setupObservers();
        setupClickListeners();
        return rootView;
    }

    private void initViews() {
        totalAmountText = rootView.findViewById(R.id.totalAmountText);
        confirmOrderButton = rootView.findViewById(R.id.confirmOrderButton);
        progressIndicator = rootView.findViewById(R.id.progressIndicator);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);
    }

    private void setupObservers() {
        viewModel.getTotalAmount().observe(getViewLifecycleOwner(), totalAmount -> {
            totalAmountText.setText(String.format("Total: $%.2f", totalAmount));
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading ->
            progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE));
    }

    private void setupClickListeners() {
        confirmOrderButton.setOnClickListener(v -> {
            viewModel.confirmOrder();
            Snackbar.make(rootView, "Order confirmed", Snackbar.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigate(R.id.action_checkoutFragment_to_ordersFragment);
        });
    }
}