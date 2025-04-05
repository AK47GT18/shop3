package com.example.shop3.Adapter;

    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageView;
    import android.widget.TextView;
    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;
    import com.bumptech.glide.Glide;
    import com.example.shop3.R;
    import com.example.shop3.model.Product;
    import com.google.android.material.button.MaterialButton;
    import java.util.List;

    public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {
        private List<Product> products;
        private OnWishlistItemClickListener listener;

        public WishlistAdapter(List<Product> products) {
            this.products = products;
        }

        public interface OnWishlistItemClickListener {
            void onProductClick(Product product);
            void onRemoveClick(Product product);
            void onAddToCartClick(Product product);
        }

        public void setOnWishlistItemClickListener(OnWishlistItemClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_wishlist, parent, false);
            return new WishlistViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
            Product product = products.get(position);
            holder.bind(product);
        }

        @Override
        public int getItemCount() {
            return products != null ? products.size() : 0;
        }

        public void setProducts(List<Product> products) {
            this.products = products;
            notifyDataSetChanged();
        }

        class WishlistViewHolder extends RecyclerView.ViewHolder {
            private final ImageView productImage;
            private final TextView productName;
            private final TextView productPrice;
            private final MaterialButton removeButton;
            private final MaterialButton addToCartButton;

            WishlistViewHolder(@NonNull View itemView) {
                super(itemView);
                productImage = itemView.findViewById(R.id.productImage);
                productName = itemView.findViewById(R.id.productName);
                productPrice = itemView.findViewById(R.id.productPrice);
                removeButton = itemView.findViewById(R.id.removeButton);
                addToCartButton = itemView.findViewById(R.id.addToCartButton);
            }

            void bind(Product product) {
                productName.setText(product.getName());
                productPrice.setText(String.format("MWK%.2f", product.getPrice()));

                // Load image using Glide
                Glide.with(itemView.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .centerCrop()
                    .into(productImage);

                itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onProductClick(product);
                    }
                });

                removeButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onRemoveClick(product);
                    }
                });

                addToCartButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onAddToCartClick(product);
                    }
                });
            }
        }
    }