package com.example.shop3;

    import android.content.Intent;
    import android.net.Uri;
    import android.os.Bundle;
    import android.widget.ArrayAdapter;
    import android.widget.Toast;
    import androidx.activity.result.ActivityResultLauncher;
    import androidx.activity.result.contract.ActivityResultContracts;
    import androidx.appcompat.app.AppCompatActivity;
    import com.google.android.material.button.MaterialButton;
    import com.google.android.material.textfield.TextInputEditText;
    import com.google.android.material.textfield.TextInputLayout;
    import com.google.firebase.firestore.FirebaseFirestore;
    import com.google.firebase.storage.FirebaseStorage;
    import com.google.firebase.storage.StorageReference;
    import java.util.HashMap;
    import java.util.Map;
    import java.util.UUID;

    public class AdminActivity extends AppCompatActivity {
        private TextInputLayout nameLayout, descriptionLayout, priceLayout, categoryLayout;
        private TextInputEditText nameEditText, descriptionEditText, priceEditText, categoryEditText;
        private MaterialButton selectImageButton, uploadButton;
        private Uri imageUri;
        private FirebaseFirestore db;
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
            setupListeners();
        }

        private void initFirebase() {
            db = FirebaseFirestore.getInstance();
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
            categoryEditText = findViewById(R.id.categoryEditText);

            selectImageButton = findViewById(R.id.selectImageButton);
            uploadButton = findViewById(R.id.uploadButton);
        }

        private void setupListeners() {
            selectImageButton.setOnClickListener(v ->
                imagePickerLauncher.launch("image/*"));

            uploadButton.setOnClickListener(v -> uploadProduct());
        }

        private void uploadProduct() {
            String name = nameEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            String price = priceEditText.getText().toString().trim();
            String category = categoryEditText.getText().toString().trim();

            if (name.isEmpty() || description.isEmpty() || price.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (imageUri == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
                return;
            }

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
                        saveProductToFirestore(name, description, price, category, imageUrl);
                    } else {
                        uploadButton.setEnabled(true);
                        uploadButton.setText("Upload");
                        Toast.makeText(AdminActivity.this,
                            "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
        }

        private void saveProductToFirestore(String name, String description,
                                          String price, String category, String imageUrl) {
            Map<String, Object> product = new HashMap<>();
            product.put("name", name);
            product.put("description", description);
            product.put("price", Double.parseDouble(price));
            product.put("category", category);
            product.put("imageUrl", imageUrl);
            product.put("timestamp", System.currentTimeMillis());

            db.collection("products")
                .add(product)
                .addOnSuccessListener(documentReference -> {
                    uploadButton.setEnabled(true);
                    uploadButton.setText("Upload");
                    Toast.makeText(AdminActivity.this,
                        "Product uploaded successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    uploadButton.setEnabled(true);
                    uploadButton.setText("Upload");
                    Toast.makeText(AdminActivity.this,
                        "Failed to upload product", Toast.LENGTH_SHORT).show();
                });
        }

        private void clearFields() {
            nameEditText.setText("");
            descriptionEditText.setText("");
            priceEditText.setText("");
            categoryEditText.setText("");
            imageUri = null;
            selectImageButton.setText("Select Image");
        }
    }