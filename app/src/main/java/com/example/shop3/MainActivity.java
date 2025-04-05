package com.example.shop3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.shop3.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.search.SearchView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.android.material.badge.BadgeDrawable;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private BottomNavigationView bottomNavigation;
    private SearchView searchView;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        initViews();
        setupNavigation();
        setupSearchView();
    }

    private void initViews() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
        searchView = findViewById(R.id.searchView);
    }

    private void setupSearchView() {
        if (searchView != null) {
            searchView.setOnClickListener(v -> {
                if (navController != null) {
                    navController.navigate(R.id.navigation_search);
                }
            });
        }
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
        if (bottomNavigation == null || mAuth.getCurrentUser() == null) {
            return;
        }

        BadgeDrawable badge = bottomNavigation.getOrCreateBadge(R.id.navigation_cart);
        badge.setVisible(false);

        DatabaseReference cartRef = mDatabase
            .child("users")
            .child(mAuth.getCurrentUser().getUid())
            .child("cart");

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (bottomNavigation != null) {
                    int count = (int) dataSnapshot.getChildrenCount();
                    badge.setVisible(count > 0);
                    badge.setNumber(count);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log the error
                Log.e("MainActivity", "Error getting cart items", databaseError.toException());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController != null && navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}