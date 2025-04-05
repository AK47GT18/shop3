package com.example.shop3;

    import androidx.annotation.NonNull;
    import androidx.lifecycle.ViewModel;
    import androidx.lifecycle.ViewModelProvider;

    import com.example.shop3.fragment.ProductDetailsViewModel;
    import com.example.shop3.repository.WishlistRepository;

    public class ProductDetailsViewModelFactory implements ViewModelProvider.Factory {
        private final WishlistRepository wishlistRepository;

        public ProductDetailsViewModelFactory(WishlistRepository wishlistRepository) {
            this.wishlistRepository = wishlistRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ProductDetailsViewModel.class)) {
                return (T) new ProductDetailsViewModel(wishlistRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }