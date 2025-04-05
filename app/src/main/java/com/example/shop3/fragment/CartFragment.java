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
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;
        import com.example.shop3.Adapter.CartAdapter;
        import com.example.shop3.model.CartItem;
                        import com.example.shop3.R;
        import com.google.android.material.button.MaterialButton;
        import com.google.android.material.snackbar.Snackbar;
        import java.util.List;

        public class CartFragment extends Fragment implements CartAdapter.OnCartItemClickListener {
            private RecyclerView cartRecyclerView;
            private ProgressBar progressIndicator;
            private TextView totalPriceText;
            private MaterialButton checkoutButton;
            private CartViewModel viewModel;
            private CartAdapter adapter;
            private View rootView;

            @Nullable
            @Override
            public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                rootView = inflater.inflate(R.layout.fragment_cart, container, false);
                initViews();
                setupRecyclerView();
                setupViewModel();
                setupObservers();
                setupClickListeners();
                return rootView;
            }

            private void initViews() {
                cartRecyclerView = rootView.findViewById(R.id.cartRecyclerView);
                progressIndicator = rootView.findViewById(R.id.progressIndicator);
                totalPriceText = rootView.findViewById(R.id.totalPriceText);
                checkoutButton = rootView.findViewById(R.id.checkoutButton);
            }

            private void setupRecyclerView() {
                adapter = new CartAdapter(this);
                cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                cartRecyclerView.setAdapter(adapter);
            }

            private void setupViewModel() {
                viewModel = new ViewModelProvider(this).get(CartViewModel.class);
            }

            private void setupObservers() {
                viewModel.getCartItems().observe(getViewLifecycleOwner(), cartItems -> {
                    adapter.setCartItems(cartItems);
                    progressIndicator.setVisibility(View.GONE);
                    updateTotalPrice(cartItems);
                });

                viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading ->
                    progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE));
            }

            private void setupClickListeners() {
                checkoutButton.setOnClickListener(v -> {
                    if (adapter.getItemCount() > 0) {
                        Navigation.findNavController(v).navigate(R.id.action_cartFragment_to_checkoutFragment);
                    } else {
                        Snackbar.make(rootView, "Your cart is empty", Snackbar.LENGTH_SHORT).show();
                    }
                });
            }

            private void updateTotalPrice(List<CartItem> cartItems) {
                double total = 0;
                for (CartItem item : cartItems) {
                    total += item.getProduct().getPrice() * item.getQuantity();
                }
                totalPriceText.setText(String.format("Total:MWK%.2f", total));
                checkoutButton.setEnabled(!cartItems.isEmpty());
            }

            @Override
            public void onRemoveFromCartClick(CartItem cartItem) {
                viewModel.removeFromCart(cartItem);
                Snackbar.make(rootView, "Item removed from cart", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onQuantityChanged(CartItem cartItem, int quantity) {
                viewModel.updateQuantity(cartItem, quantity);
            }
        }