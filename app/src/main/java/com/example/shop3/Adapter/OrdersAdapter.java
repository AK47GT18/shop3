package com.example.shop3.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shop3.R;
import com.example.shop3.model.CartItem;
import com.example.shop3.model.Order;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {
    private List<Order> orders;
    private OnOrderDeleteListener deleteListener;

    public interface OnOrderDeleteListener {
        void onOrderDelete(Order order);
    }

    public OrdersAdapter(List<Order> orders) {
        this.orders = orders;
    }

    public void setOnOrderDeleteListener(OnOrderDeleteListener listener) {
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView orderDate;
        private final TextView orderTotal;
        private final TextView productName;
        private final MaterialButton deleteOrderButton;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderTotal = itemView.findViewById(R.id.orderTotal);
            productName = itemView.findViewById(R.id.productName);
            deleteOrderButton = itemView.findViewById(R.id.deleteOrderButton);

            deleteOrderButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && deleteListener != null) {
                    deleteListener.onOrderDelete(orders.get(position));
                }
            });
        }

        void bind(Order order) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            // Use Date constructor only if it's a timestamp
            long timestamp = order.getDate(); // assuming order.getDate() returns a long
            orderDate.setText(dateFormat.format(new Date(timestamp)));

            orderTotal.setText(String.format("MWK%.2f", order.getTotal()));

            StringBuilder productList = new StringBuilder();
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                for (CartItem item : order.getItems()) {
                    productList.append("• ")
                            .append(item.getProductName())
                            .append(" x")
                            .append(item.getQuantity())
                            .append("\n");
                }
            } else {
                productList.append("No products");
            }

            productName.setText(productList.toString().trim());
        }
    }
}
