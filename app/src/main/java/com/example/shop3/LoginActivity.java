package com.example.shop3;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.Toast;
    import androidx.appcompat.app.AppCompatActivity;
    import com.google.android.material.button.MaterialButton;
    import com.google.android.material.tabs.TabLayout;
    import com.google.android.material.textfield.TextInputEditText;
    import com.google.android.material.textfield.TextInputLayout;
    import com.google.firebase.auth.FirebaseAuth;
    import com.example.shop3.helpers.ValidationHelper;

    public class LoginActivity extends AppCompatActivity {
        private TextInputLayout emailLayout, passwordLayout;
        private TextInputEditText emailEditText, passwordEditText;
        private MaterialButton loginButton;
        private View forgotPasswordText, signUpText;
        private TabLayout loginTabs;
        private FirebaseAuth mAuth;

        private static final String ADMIN_USERNAME = "admin";
        private static final String ADMIN_PASSWORD = "Arthony@47K";
        private boolean isAdminLogin = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() != null) {
                // User is already logged in, move to MainActivity
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
                return;
            }

            initViews();
            setupListeners();
        }

        private void initViews() {
            emailLayout = findViewById(R.id.emailLayout);
            passwordLayout = findViewById(R.id.passwordLayout);
            emailEditText = findViewById(R.id.emailEditText);
            passwordEditText = findViewById(R.id.passwordEditText);
            loginButton = findViewById(R.id.loginButton);
            forgotPasswordText = findViewById(R.id.forgotPasswordText);
            signUpText = findViewById(R.id.signUpText);
            loginTabs = findViewById(R.id.loginTabs);
        }

        private void setupListeners() {
            loginTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    isAdminLogin = tab.getPosition() == 1;
                    emailLayout.setHint(isAdminLogin ? "Username" : "Email");
                    signUpText.setVisibility(isAdminLogin ? View.GONE : View.VISIBLE);
                    forgotPasswordText.setVisibility(isAdminLogin ? View.GONE : View.VISIBLE);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {}

                @Override
                public void onTabReselected(TabLayout.Tab tab) {}
            });

            loginButton.setOnClickListener(v -> attemptLogin());

            forgotPasswordText.setOnClickListener(v ->
                Toast.makeText(LoginActivity.this, "Password reset coming soon", Toast.LENGTH_SHORT).show());

            signUpText.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
        }

        private void attemptLogin() {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Reset errors
            emailLayout.setError(null);
            passwordLayout.setError(null);

            if (isAdminLogin) {
                if (email.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
                    startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Invalid admin credentials", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // Validate user login
            if (!ValidationHelper.isValidEmail(email)) {
                emailLayout.setError("Please enter a valid email address");
                return;
            }

            if (password.isEmpty()) {
                passwordLayout.setError("Password cannot be empty");
                return;
            }

            // Show progress
            loginButton.setEnabled(false);
            loginButton.setText("Logging in...");

            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    loginButton.setEnabled(true);
                    loginButton.setText("Login");

                    if (task.isSuccessful()) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this,
                            "Authentication failed: " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }