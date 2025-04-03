package com.example.shop3.fragment;

        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.fragment.app.Fragment;
        import androidx.lifecycle.ViewModelProvider;
        import androidx.navigation.Navigation;
        import androidx.recyclerview.widget.GridLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;
        import com.example.shop3.R;
        import com.example.shop3.Adapter.ProductAdapter;
        import com.example.shop3.model.Product;
        import com.google.android.material.progressindicator.CircularProgressIndicator;
        import com.google.android.material.search.SearchBar;
        import java.util.ArrayList;

        public class HomeFragment extends Fragment implements ProductAdapter.OnProductClickListener {
            private HomeViewModel viewModel;
            private RecyclerView productsRecyclerView;
            private ProductAdapter adapter;
            private SearchBar searchBar;
            private CircularProgressIndicator progressIndicator;

            @Override
            public void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
            }

            @Override
            public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.fragment_home, container, false);
                initViews(view);
                setupRecyclerView();
                observeViewModel();
                return view;
            }

            private void initViews(View view) {
                productsRecyclerView = view.findViewById(R.id.productsRecyclerView);
                searchBar = view.findViewById(R.id.searchBar);
                progressIndicator = view.findViewById(R.id.progressIndicator);
            }

            private void setupRecyclerView() {
                adapter = new ProductAdapter(new ArrayList<>());
                adapter.setOnProductClickListener(this);
                productsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                productsRecyclerView.setAdapter(adapter);
            }

            private void observeViewModel() {
                viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
                    adapter.setProducts(products);
                    adapter.notifyDataSetChanged();
                });

                viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
                    progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                });
            }

          @Override
            public void onProductClick(Product product) {
                Bundle args = new Bundle();
                args.putString("productId", product.getId());
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_navigation_home_to_productDetailFragment, args);
            }
        }