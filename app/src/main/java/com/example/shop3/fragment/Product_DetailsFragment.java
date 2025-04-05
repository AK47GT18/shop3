package com.example.shop3.fragment;

    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.fragment.app.Fragment;
    import androidx.lifecycle.ViewModelProvider;
    import androidx.navigation.Navigation;

    import com.bumptech.glide.Glide;
    import com.example.shop3.ProductDetailsViewModelFactory;
    import com.example.shop3.R;
    import com.example.shop3.model.Product;
    import com.example.shop3.repository.WishlistRepository;
    import com.google.android.material.snackbar.Snackbar;

    public class Product_DetailsFragment extends Fragment {

        private ProductDetailsViewModel viewModel;
        private ImageView productImage;
        private TextView productName, productPrice, productDescription;
        private Button addToCartButton, addToWishlistButton;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Initialize repository and factory
            WishlistRepository wishlistRepository = new WishlistRepository();
            ProductDetailsViewModelFactory factory = new ProductDetailsViewModelFactory(wishlistRepository);

            // Initialize ViewModel with factory
            viewModel = new ViewModelProvider(this, factory).get(ProductDetailsViewModel.class);

            String productId = getArguments() != null ? getArguments().getString("productId") : null;
            if (productId != null) {
                viewModel.loadProductById(productId);
            }
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_product__details, container, false);
            initViews(view);
            setupListeners();
            observeViewModel();
            return view;
        }

        private void initViews(View view) {
            productImage = view.findViewById(R.id.productImage);
            productName = view.findViewById(R.id.productName);
            productPrice = view.findViewById(R.id.productPrice);
            productDescription = view.findViewById(R.id.productDescription);
            addToCartButton = view.findViewById(R.id.addToCartButton);
            addToWishlistButton = view.findViewById(R.id.addToWishlistButton);
        }

        private void setupListeners() {
            addToCartButton.setOnClickListener(v -> {
                Product product = viewModel.getProduct().getValue();
                if (product != null) {
                    viewModel.addToCart(product);
                }
            });

            addToWishlistButton.setOnClickListener(v -> {
                Product product = viewModel.getProduct().getValue();
                if (product != null) {
                    viewModel.addToWishlist(product);
                    Snackbar.make(requireView(), "Added to Wishlist", Snackbar.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView())
                        .navigate(R.id.action_productDetailFragment_to_navigation_wishlist);
                }
            });
        }

        private void observeViewModel() {
            viewModel.getProduct().observe(getViewLifecycleOwner(), this::updateUI);

            viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
                addToCartButton.setEnabled(!isLoading);
                addToWishlistButton.setEnabled(!isLoading);
            });

            viewModel.getError().observe(getViewLifecycleOwner(), message -> {
                if (message != null && !message.isEmpty()) {
                    Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
                }
            });

            viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
                if (message != null) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                }
            });

            viewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
                if (message != null) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void updateUI(Product product) {
            if (product == null) return;

            productName.setText(product.getName());
            productPrice.setText("Price: MWK" + product.getPrice());
            productDescription.setText(product.getDescription());

            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(product.getImageUrl())
                        .into(productImage);
            }
        }
    }