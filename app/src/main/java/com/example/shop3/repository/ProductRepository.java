package com.example.shop3.repository;

            import com.example.shop3.model.Product;
            import com.google.firebase.auth.FirebaseAuth;
            import com.google.firebase.database.DataSnapshot;
            import com.google.firebase.database.DatabaseError;
            import com.google.firebase.database.DatabaseReference;
            import com.google.firebase.database.FirebaseDatabase;
            import com.google.firebase.database.ValueEventListener;

            import java.util.ArrayList;
            import java.util.List;

            public class ProductRepository {
                private final DatabaseReference productsRef;
                private final DatabaseReference cartRef;
                private final DatabaseReference wishlistRef;
                private final String userId;

                public ProductRepository() {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    this.productsRef = database.getReference("products");
                    this.cartRef = database.getReference("cart");
                    this.wishlistRef = database.getReference("wishlist");
                    // Assuming you have Firebase Auth implemented
                    this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                }

                public interface ProductListCallback {
                    void onSuccess(List<Product> productList);
                    void onError(Exception e);
                }

                public void searchProducts(String query, String category, String price, ProductListCallback callback) {
                    productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List<Product> productList = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Product product = snapshot.getValue(Product.class);
                                if (product != null && matchesFilter(product, query, category, price)) {
                                    productList.add(product);
                                }
                            }
                            callback.onSuccess(productList);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            callback.onError(databaseError.toException());
                        }
                    });
                }

                private boolean matchesFilter(Product product, String query, String category, String price) {
                    boolean matchesQuery = query == null || query.isEmpty() || product.getName().toLowerCase().contains(query.toLowerCase());
                    boolean matchesCategory = category == null || category.isEmpty() || product.getCategory().equalsIgnoreCase(category);
                    boolean matchesPrice = price == null || price.isEmpty() || product.getPrice() <= Double.parseDouble(price);

                    return matchesQuery && matchesCategory && matchesPrice;
                }

                public interface SingleProductCallback {
                    void onSuccess(Product product);
                    void onError(Exception e);
                }

                public interface CartCallback {
                    void onSuccess();
                    void onError(Exception e);
                }

                public interface WishlistCallback {
                    void onSuccess();
                    void onError(Exception e);
                }

                public void getProductById(String productId, SingleProductCallback callback) {
                    productsRef.child(productId).get()
                        .addOnSuccessListener(snapshot -> {
                            Product product = snapshot.getValue(Product.class);
                            if (product != null) {
                                callback.onSuccess(product);
                            } else {
                                callback.onError(new Exception("Product not found"));
                            }
                        })
                        .addOnFailureListener(e -> callback.onError(e));
                }

                public void addToCart(Product product, CartCallback callback) {
                    String cartItemId = cartRef.child(userId).push().getKey();
                    if (cartItemId != null) {
                        cartRef.child(userId).child(cartItemId).setValue(product)
                            .addOnSuccessListener(unused -> callback.onSuccess())
                            .addOnFailureListener(callback::onError);
                    } else {
                        callback.onError(new Exception("Failed to generate cart item ID"));
                    }
                }

                public void addToWishlist(Product product, WishlistCallback callback) {
                    String wishlistItemId = wishlistRef.child(userId).push().getKey();
                    if (wishlistItemId != null) {
                        wishlistRef.child(userId).child(wishlistItemId).setValue(product)
                            .addOnSuccessListener(unused -> callback.onSuccess())
                            .addOnFailureListener(callback::onError);
                    } else {
                        callback.onError(new Exception("Failed to generate wishlist item ID"));
                    }
                }
            }