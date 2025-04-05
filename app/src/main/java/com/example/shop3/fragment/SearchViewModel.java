package com.example.shop3.fragment;

                import androidx.lifecycle.LiveData;
                import androidx.lifecycle.MutableLiveData;
                import androidx.lifecycle.ViewModel;
                import com.example.shop3.model.Product;
                import com.example.shop3.repository.ProductRepository;
                import com.google.firebase.auth.FirebaseAuth;
                import com.google.firebase.database.DatabaseReference;
                import com.google.firebase.database.FirebaseDatabase;

                import java.util.HashMap;
                import java.util.List;
                import java.util.Map;

                public class SearchViewModel extends ViewModel {
                    private final ProductRepository productRepository;
                    private final MutableLiveData<List<Product>> products = new MutableLiveData<>();
                    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
                    private final MutableLiveData<String> error = new MutableLiveData<>();
                    private final DatabaseReference database;
                    private final String userId;

                    public SearchViewModel() {
                        productRepository = new ProductRepository();
                        userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
                        database = FirebaseDatabase.getInstance().getReference();
                    }

                    public void addToCart(Product product) {
                        if (userId == null) {
                            error.setValue("Please login to add items to cart");
                            return;
                        }

                        Map<String, Object> cartItem = new HashMap<>();
                        cartItem.put("product", product);
                        cartItem.put("quantity", 1);

                        database.child("users")
                                .child(userId)
                                .child("cart")
                                .push()
                                .setValue(cartItem)
                                .addOnSuccessListener(unused -> {
                                    // Handle success if needed
                                })
                                .addOnFailureListener(e -> error.setValue(e.getMessage()));
                    }

                    public void searchProducts(String query, String category, String price) {
                        isLoading.setValue(true);

                        // Handle potential number format exception for price
                        double priceValue = 0;
                        if (price != null && !price.isEmpty()) {
                            try {
                                priceValue = Double.parseDouble(price);
                            } catch (NumberFormatException e) {
                                error.postValue("Invalid price format");
                                isLoading.postValue(false);
                                return;
                            }
                        }

                        final String finalPrice = price;

                        productRepository.searchProducts(query, category, finalPrice, new ProductRepository.ProductListCallback() {
                            @Override
                            public void onSuccess(List<Product> productList) {
                                products.postValue(productList);
                                isLoading.postValue(false);
                            }

                            @Override
                            public void onError(Exception e) {
                                error.postValue(e.getMessage());
                                isLoading.postValue(false);
                            }
                        });
                    }

                    public LiveData<List<Product>> getProducts() {
                        return products;
                    }

                    public LiveData<Boolean> getIsLoading() {
                        return isLoading;
                    }

                    public LiveData<String> getError() {
                        return error;
                    }
                }