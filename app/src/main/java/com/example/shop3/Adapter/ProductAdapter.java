package com.example.shop3.Adapter;

        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;
        import com.example.shop3.R;
        import com.example.shop3.model.Product;
        import java.util.List;

        public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
            private List<Product> products;
            private OnProductClickListener listener;

            public interface OnProductClickListener {
                void onProductClick(Product product);
            }

            public ProductAdapter(List<Product> products) {
                this.products = products;
            }

            public void setOnProductClickListener(OnProductClickListener listener) {
                this.listener = listener;
            }

            public void setProducts(List<Product> products) {
                this.products = products;
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product, parent, false);
                return new ProductViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
                Product product = products.get(position);
                // Bind product data to views
                holder.itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onProductClick(product);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return products.size();
            }

            static class ProductViewHolder extends RecyclerView.ViewHolder {
                // Define your views here

                public ProductViewHolder(@NonNull View itemView) {
                    super(itemView);
                    // Initialize your views here
                }
            }
        }