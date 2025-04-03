// SearchViewModel.java
        package com.example.shop3.fragment;

        import androidx.lifecycle.LiveData;
        import androidx.lifecycle.MutableLiveData;
        import androidx.lifecycle.ViewModel;
        import com.example.shop3.model.Product;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.firestore.QuerySnapshot;
        import java.util.ArrayList;
        import java.util.List;

        public class SearchViewModel extends ViewModel {
            private final MutableLiveData<List<Product>> products = new MutableLiveData<>(new ArrayList<>());
            private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
            private final FirebaseFirestore db = FirebaseFirestore.getInstance();

            public LiveData<List<Product>> getProducts() {
                return products;
            }

            public LiveData<Boolean> getIsLoading() {
                return isLoading;
            }

            public void searchProducts(String query, String category, float minPrice, float maxPrice) {
                isLoading.setValue(true);

                db.collection("products")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Product> filteredList = filterProducts(
                            queryDocumentSnapshots,
                            query.toLowerCase(),
                            category,
                            minPrice,
                            maxPrice
                        );
                        products.setValue(filteredList);
                        isLoading.setValue(false);
                    })
                    .addOnFailureListener(e -> isLoading.setValue(false));
            }

            private List<Product> filterProducts(QuerySnapshot snapshots, String query,
                                              String category, float minPrice, float maxPrice) {
                List<Product> filteredList = new ArrayList<>();

                for (var document : snapshots) {
                    Product product = document.toObject(Product.class);
                    if (product != null) {
                        product.setId(document.getId());
                        boolean matchesSearch = product.getName().toLowerCase().contains(query) ||
                                             product.getDescription().toLowerCase().contains(query);
                        boolean matchesCategory = category.equals("All Categories") ||
                                               product.getCategory().equals(category);
                        boolean matchesPrice = product.getPrice() >= minPrice &&
                                             product.getPrice() <= maxPrice;

                        if (matchesSearch && matchesCategory && matchesPrice) {
                            filteredList.add(product);
                        }
                    }
                }
                return filteredList;
            }
        }