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
    import com.google.android.material.textfield.TextInputEditText;

    public class CheckoutFragment extends Fragment {
        private TextView totalAmountText;
        private TextInputEditText amountInput;
        private MaterialButton confirmOrderButton;
        private ProgressBar progressIndicator;
        private CheckoutViewModel viewModel;
        private View rootView;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                               @Nullable Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_checkout, container, false);
            initViews();
            setupViewModel();
            setupObservers();
            setupClickListeners();
            return rootView;
        }

        private void initViews() {
            totalAmountText = rootView.findViewById(R.id.totalAmountText);
            amountInput = rootView.findViewById(R.id.amountInput);
            confirmOrderButton = rootView.findViewById(R.id.confirmOrderButton);
            progressIndicator = rootView.findViewById(R.id.progressIndicator);
        }

        private void setupViewModel() {
            viewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);
        }

        private void setupObservers() {
            viewModel.getTotalAmount().observe(getViewLifecycleOwner(), totalAmount -> {
                totalAmountText.setText(String.format("Total: MWK%.2f", totalAmount));
            });

            viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading ->
                progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE));

            viewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
                if (message != null) {
                    Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
                }
            });
        }

        private void setupClickListeners() {
            confirmOrderButton.setOnClickListener(v -> {
                String amountStr = amountInput.getText().toString().trim();
                if (amountStr.isEmpty()) {
                    Snackbar.make(rootView, "Please enter the amount", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double enteredAmount = Double.parseDouble(amountStr);
                    viewModel.confirmOrder(enteredAmount, () ->
                        Navigation.findNavController(v)
                            .navigate(R.id.action_checkoutFragment_to_ordersFragment)
                    );
                } catch (NumberFormatException e) {
                    Snackbar.make(rootView, "Invalid amount format", Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }