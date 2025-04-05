package com.example.shop3.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.shop3.model.Product;
import com.example.shop3.repository.CartRepository;
import com.example.shop3.repository.WishlistRepository;
import java.util.List;

public class WishlistViewModel extends ViewModel {
    private final MutableLiveData<List<Product>> wishlist = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    private final WishlistRepository wishlistRepository;
    private final CartRepository cartRepository;

    public WishlistViewModel(WishlistRepository wishlistRepository, CartRepository cartRepository) {
        this.wishlistRepository = wishlistRepository;
        this.cartRepository = cartRepository;
        loadWishlist();
    }

    public LiveData<List<Product>> getWishlist() {
        return wishlist;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    private void loadWishlist() {
        isLoading.setValue(true);
        wishlistRepository.getWishlist(new WishlistRepository.WishlistCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                wishlist.setValue(products);
                isLoading.setValue(false);
            }

            @Override
            public void onError(Exception e) {
                errorMessage.setValue("Failed to load wishlist: " + e.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    public void removeFromWishlist(Product product) {
        isLoading.setValue(true);
        wishlistRepository.removeFromWishlist(product, new WishlistRepository.WishlistCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                wishlist.setValue(products);
                successMessage.setValue("Product removed from wishlist");
                isLoading.setValue(false);
            }

            @Override
            public void onError(Exception e) {
                errorMessage.setValue("Failed to remove product: " + e.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    public void addToCart(Product product) {
        isLoading.setValue(true);
        cartRepository.addToCart(product, 1, new CartRepository.CartCallback() {
            @Override
            public void onSuccess() {
                successMessage.setValue("Product added to cart");
                removeFromWishlist(product);  // Remove the product from wishlist after adding it to the cart
                isLoading.setValue(false);
            }

            @Override
            public void onError(Exception e) {
                errorMessage.setValue("Failed to add to cart: " + e.getMessage());
                isLoading.setValue(false);
            }
        });
    }
}
