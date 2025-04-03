package com.example.shop3.repository;

    import com.example.shop3.model.Product;
    import com.google.firebase.firestore.FirebaseFirestore;
    import com.google.firebase.firestore.QuerySnapshot;
    import java.util.ArrayList;
    import java.util.List;

    public class WishlistRepository {
        private final FirebaseFirestore db;

        public interface WishlistCallback {
            void onSuccess(List<Product> products); // Single onSuccess method
            void onError(Exception e);
        }

        public WishlistRepository() {
            db = FirebaseFirestore.getInstance();
        }

        public void getWishlist(WishlistCallback callback) {
            db.collection("wishlist")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Product> products = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            products = querySnapshot.toObjects(Product.class);
                        }
                        callback.onSuccess(products);
                    } else {
                        callback.onError(task.getException());
                    }
                });
        }

        public void removeFromWishlist(Product product, WishlistCallback callback) {
            db.collection("wishlist")
                .whereEqualTo("id", product.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        task.getResult().getDocuments().forEach(document ->
                            db.collection("wishlist").document(document.getId()).delete());
                        getWishlist(callback); // This will return updated list after removal
                    } else {
                        callback.onError(task.getException());
                    }
                });
        }

        public void addToWishlist(Product product, WishlistCallback callback) {
            db.collection("wishlist")
                .add(product)
                .addOnSuccessListener(documentReference -> getWishlist(callback))
                .addOnFailureListener(callback::onError);
        }
    }