package com.example.shop3;

    import android.content.Intent;
    import android.net.Uri;
    import android.os.Bundle;
    import android.widget.ArrayAdapter;
    import android.widget.AutoCompleteTextView;
    import android.widget.Toast;
    import androidx.activity.result.ActivityResultLauncher;
    import androidx.activity.result.contract.ActivityResultContracts;
    import androidx.appcompat.app.AppCompatActivity;
    import com.google.android.material.button.MaterialButton;
    import com.google.android.material.textfield.TextInputEditText;
    import com.google.android.material.textfield.TextInputLayout;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.storage.FirebaseStorage;
    import com.google.firebase.storage.StorageReference;
    import java.util.HashMap;
    import java.util.Map;
    import java.util.UUID;

    public class AdminActivity extends AppCompatActivity {
        private TextInputLayout nameLayout, descriptionLayout, priceLayout, categoryLayout;
        private TextInputEditText nameEditText, descriptionEditText, priceEditText;
        private AutoCompleteTextView categoryDropdown;
        private MaterialButton selectImageButton, uploadButton;
        private Uri imageUri;
        private DatabaseReference mDatabase;
        private FirebaseStorage storage;

        private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            result -> {
                if (result != null) {
                    imageUri = result;
                    selectImageButton.setText("Image Selected");
                }
            }
        );

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_admin);

            initFirebase();
            initViews();
            setupCategoryDropdown();
            setupListeners();
        }

        private void initFirebase() {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            storage = FirebaseStorage.getInstance();
        }

        private void initViews() {
            nameLayout = findViewById(R.id.nameLayout);
            descriptionLayout = findViewById(R.id.descriptionLayout);
            priceLayout = findViewById(R.id.priceLayout);
            categoryLayout = findViewById(R.id.categoryLayout);

            nameEditText = findViewById(R.id.nameEditText);
            descriptionEditText = findViewById(R.id.descriptionEditText);
            priceEditText = findViewById(R.id.priceEditText);
            categoryDropdown = findViewById(R.id.categoryDropdown);

            selectImageButton = findViewById(R.id.selectImageButton);
            uploadButton = findViewById(R.id.uploadButton);
        }

        private void setupCategoryDropdown() {
            String[] categories = new String[] {
                "Fashion",
                "Electronics",
                "Home & Living",
                "Sports & Outdoor",
                "Books & Stationery",
                "Health & Beauty",
                "Toys & Games",
                "Automotive",
                "Other"
            };

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_item,
                categories
            );

            categoryDropdown.setAdapter(adapter);
        }

        private void setupListeners() {
            selectImageButton.setOnClickListener(v ->
                imagePickerLauncher.launch("image/*"));

            uploadButton.setOnClickListener(v -> {
                if (validateInputs()) {
                    uploadProduct();
                }
            });
        }

        private boolean validateInputs() {
            boolean isValid = true;

            String name = nameEditText.getText().toString().trim();
            if (name.isEmpty()) {
                nameLayout.setError("Product name is required");
                isValid = false;
            } else {
                nameLayout.setError(null);
            }

            String description = descriptionEditText.getText().toString().trim();
            if (description.isEmpty()) {
                descriptionLayout.setError("Description is required");
                isValid = false;
            } else {
                descriptionLayout.setError(null);
            }

            String price = priceEditText.getText().toString().trim();
            if (price.isEmpty()) {
                priceLayout.setError("Price is required");
                isValid = false;
            } else {
                try {
                    double priceValue = Double.parseDouble(price);
                    if (priceValue <= 0) {
                        priceLayout.setError("Price must be greater than 0");
                        isValid = false;
                    } else {
                        priceLayout.setError(null);
                    }
                } catch (NumberFormatException e) {
                    priceLayout.setError("Invalid price format");
                    isValid = false;
                }
            }

            String category = categoryDropdown.getText().toString().trim();
            if (category.isEmpty()) {
                categoryLayout.setError("Category is required");
                isValid = false;
            } else {
                categoryLayout.setError(null);
            }

            if (imageUri == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            return isValid;
        }

        private void uploadProduct() {
            uploadButton.setEnabled(false);
            uploadButton.setText("Uploading...");

            String imageId = UUID.randomUUID().toString();
            StorageReference imageRef = storage.getReference().child("products/" + imageId);

            imageRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imageRef.getDownloadUrl();
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String imageUrl = task.getResult().toString();
                        saveProductToDatabase(
                            nameEditText.getText().toString().trim(),
                            descriptionEditText.getText().toString().trim(),
                            priceEditText.getText().toString().trim(),
                            categoryDropdown.getText().toString().trim(),
                            imageUrl
                        );
                    } else {
                        uploadButton.setEnabled(true);
                        uploadButton.setText("Upload");
                        Toast.makeText(AdminActivity.this,
                            "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
        }

        private void saveProductToDatabase(String name, String description,
                                         String price, String category, String imageUrl) {
            String productId = mDatabase.child("products").push().getKey();
            Map<String, Object> product = new HashMap<>();
            product.put("name", name);
            product.put("description", description);
            product.put("price", Double.parseDouble(price));
            product.put("category", category);
            product.put("imageUrl", imageUrl);
            product.put("timestamp", System.currentTimeMillis());

            mDatabase.child("products").child(productId)
                .setValue(product)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AdminActivity.this,
                        "Product uploaded successfully", Toast.LENGTH_SHORT).show();
                    // Navigate to MainActivity
                    Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    uploadButton.setEnabled(true);
                    uploadButton.setText("Upload");
                    Toast.makeText(AdminActivity.this,
                        "Failed to upload product", Toast.LENGTH_SHORT).show();
                });
        }
    }