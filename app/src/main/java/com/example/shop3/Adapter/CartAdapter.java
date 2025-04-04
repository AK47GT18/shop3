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
import com.example.shop3.model.CartItem;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems = new ArrayList<>();
    private final OnCartItemClickListener listener;

    public interface OnCartItemClickListener {
        void onRemoveFromCartClick(CartItem cartItem);
        void onQuantityChanged(CartItem cartItem, int quantity);
    }

    public CartAdapter(OnCartItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(cartItems.get(position));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView productName;
        private final TextView productPrice;
        private final TextView quantityText;
        private final MaterialButton removeButton;
        private final MaterialButton decreaseButton;
        private final MaterialButton increaseButton;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            quantityText = itemView.findViewById(R.id.quantityText);
            removeButton = itemView.findViewById(R.id.removeButton);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
            increaseButton = itemView.findViewById(R.id.increaseButton);
        }

        void bind(CartItem cartItem) {
            productName.setText(cartItem.getProduct().getName());
            productPrice.setText(String.format("$%.2f", cartItem.getProduct().getPrice()));
            quantityText.setText(String.valueOf(cartItem.getQuantity()));

            Glide.with(itemView.getContext())
                .load(cartItem.getProduct().getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(productImage);

            removeButton.setOnClickListener(v -> listener.onRemoveFromCartClick(cartItem));

            decreaseButton.setOnClickListener(v -> {
                int newQuantity = cartItem.getQuantity() - 1;
                if (newQuantity >= 0) {
                    listener.onQuantityChanged(cartItem, newQuantity);
                }
            });

            increaseButton.setOnClickListener(v -> {
                int newQuantity = cartItem.getQuantity() + 1;
                listener.onQuantityChanged(cartItem, newQuantity);
            });
        }
    }
}