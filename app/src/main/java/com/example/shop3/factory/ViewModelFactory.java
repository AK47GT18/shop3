package com.example.shop3.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.shop3.repository.CartRepository;
import com.example.shop3.repository.WishlistRepository;
import com.example.shop3.fragment.WishlistViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final WishlistRepository wishlistRepository;
    private final CartRepository cartRepository;

    public ViewModelFactory(WishlistRepository wishlistRepository, CartRepository cartRepository) {
        this.wishlistRepository = wishlistRepository;
        this.cartRepository = cartRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(WishlistViewModel.class)) {
            return (T) new WishlistViewModel(wishlistRepository, cartRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}