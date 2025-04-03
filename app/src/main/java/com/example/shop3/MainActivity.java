package com.example.shop3;

        import android.os.Bundle;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.navigation.NavController;
        import androidx.navigation.fragment.NavHostFragment;
        import androidx.navigation.ui.NavigationUI;
        import com.google.android.material.bottomnavigation.BottomNavigationView;
        import com.google.android.material.search.SearchView;
        import com.google.firebase.FirebaseApp;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.android.material.badge.BadgeDrawable;

        public class MainActivity extends AppCompatActivity {
            private NavController navController;
            private BottomNavigationView bottomNavigation;
            private SearchView searchView;
            private FirebaseAuth mAuth;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);

                initViews();
                setupNavigation();
                setupSearchView();
               // mAuth = FirebaseAuth.getInstance();
            }

            private void initViews() {
                bottomNavigation = findViewById(R.id.bottom_navigation);
                searchView = findViewById(R.id.searchView);
            }

            private void setupSearchView() {
                searchView.setOnClickListener(v -> {
                    if (navController != null) {
                        navController.navigate(R.id.navigation_search);
                    }
                });
            }

            private void setupNavigation() {
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
                if (navHostFragment != null) {
                    navController = navHostFragment.getNavController();
                    NavigationUI.setupWithNavController(bottomNavigation, navController);
                    setupCartBadge();
                }
            }

            private void setupCartBadge() {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if (auth.getCurrentUser() != null) {
                    BadgeDrawable badge = bottomNavigation.getOrCreateBadge(R.id.navigation_cart);
                    badge.setVisible(false);

                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(auth.getCurrentUser().getUid())
                        .collection("cart")
                        .addSnapshotListener((value, error) -> {
                            if (error == null && value != null) {
                                int count = value.size();
                                badge.setVisible(count > 0);
                                badge.setNumber(count);
                            }
                        });
                }
            }

            @Override
            public boolean onSupportNavigateUp() {
                return navController != null && navController.navigateUp() || super.onSupportNavigateUp();
            }
        }