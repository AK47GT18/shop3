package com.example.shop3.fragment;

    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageView;
    import android.widget.TextView;
    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.fragment.app.Fragment;
    import androidx.lifecycle.ViewModelProvider;
    import com.bumptech.glide.Glide;
            import com.example.shop3.R;
    import com.example.shop3.model.Product;
    import com.google.android.material.button.MaterialButton;

    public class Product_DetailsFragment extends Fragment {
        private ImageView productImage;
        private TextView productName;
        private TextView productPrice;
        private TextView productDescription;
        private MaterialButton addToCartButton;
        private MaterialButton addToWishlistButton;
        private ProductDetailsViewModel viewModel;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_product__details, container, false);
            initViews(view);
            setupViewModel();
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

        private void setupViewModel() {
            viewModel = new ViewModelProvider(this).get(ProductDetailsViewModel.class);
            String productId = getArguments().getString("productId");
            viewModel.loadProduct(productId);

            viewModel.getProduct().observe(getViewLifecycleOwner(), this::bindProduct);
        }

        private void bindProduct(Product product) {
            productName.setText(product.getName());
            productPrice.setText(String.format("$%.2f", product.getPrice()));
            productDescription.setText(product.getDescription());

            Glide.with(this)
                .load(product.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(productImage);

            addToCartButton.setOnClickListener(v -> viewModel.addToCart(product));
            addToWishlistButton.setOnClickListener(v -> viewModel.addToWishlist(product));
        }
    }