package com.example.shop3.repository;

    import com.example.shop3.model.Product;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import java.util.ArrayList;
    import java.util.List;

    public class CartRepository {
        private final DatabaseReference cartRef;
        private final String userId;

        public CartRepository() {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            this.cartRef = database.getReference("carts");
            // TODO: Get actual user ID from authentication
            this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        public interface CartCallback {
            void onSuccess();
            void onError(Exception e);
        }

        public void addToCart(Product product, int quantity, CartCallback callback) {
            cartRef.child(userId).child(product.getId())
                .setValue(quantity)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e));
        }

        public void removeFromCart(Product product, CartCallback callback) {
            cartRef.child(userId).child(product.getId())
                .removeValue()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e));
        }

        public void getCart(CartItemsCallback callback) {
            cartRef.child(userId).get()
                .addOnSuccessListener(snapshot -> {
                    List<Product> products = new ArrayList<>();
                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                        Product product = productSnapshot.getValue(Product.class);
                        if (product != null) {
                            products.add(product);
                        }
                    }
                    callback.onSuccess(products);
                })
                .addOnFailureListener(e -> callback.onError(e));
        }

        public interface CartItemsCallback {
            void onSuccess(List<Product> products);
            void onError(Exception e);
        }
    }