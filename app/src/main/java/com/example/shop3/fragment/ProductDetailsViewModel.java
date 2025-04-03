package com.example.shop3.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.shop3.model.Product;
import com.example.shop3.repository.CartRepository;
import com.example.shop3.repository.ProductRepository;
import com.example.shop3.repository.WishlistRepository;

import java.util.List;

public class ProductDetailsViewModel extends ViewModel {
    private final MutableLiveData<Product> product = new MutableLiveData<>();
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final WishlistRepository wishlistRepository;

    public ProductDetailsViewModel() {
        productRepository = new ProductRepository();
        cartRepository = new CartRepository();
        wishlistRepository = new WishlistRepository();
    }

    public void loadProduct(String productId) {
        // TODO: Replace with actual API call
        productRepository.getProduct(productId, new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(Product loadedProduct) {
                product.setValue(loadedProduct);
            }

            @Override
            public void onError(Exception e) {
                // Handle error
            }
        });
    }

    public LiveData<Product> getProduct() {
        return product;
    }

    public void addToCart(Product product) {
        cartRepository.addToCart(product, 1, new CartRepository.CartCallback() {
            @Override
            public void onSuccess() {
                // Handle success
            }

            @Override
            public void onError(Exception e) {
                // Handle error
            }
        });
    }

    public void addToWishlist(Product product) {
        wishlistRepository.addToWishlist(product, new WishlistRepository.WishlistCallback() {


            @Override
            public void onSuccess(List<Product> products) {

            }

            @Override
            public void onError(Exception e) {
                // Handle error
            }
        });
    }
}