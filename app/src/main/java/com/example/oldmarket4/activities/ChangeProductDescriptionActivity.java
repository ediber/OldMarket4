package com.example.oldmarket4.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.oldmarket4.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import model.Product;

public class ChangeProductDescriptionActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView tvProductName, tvProductDescription, tvProductQuantity, tvProductChange;
    private ImageView ivProductImage;
    private Button btnSaveProduct;
    private String currentProductId, productIdToChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_product_description);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        currentProductId = getIntent().getStringExtra("currentProductId");
        productIdToChange = getIntent().getStringExtra("productIdToChange");

        InitializeViews();
        fetchProductDetails();
        setListeners();
    }

    private void InitializeViews() {
        tvProductName = findViewById(R.id.tvProductName);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        tvProductQuantity = findViewById(R.id.tvProductQuantity);
        tvProductChange = findViewById(R.id.tvProductChange);
        ivProductImage = findViewById(R.id.ivProductImage);
        btnSaveProduct = findViewById(R.id.btnSaveProduct);
    }

    private void setListeners() {
        btnSaveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProductDetails();
            }
        });
    }

    private void fetchProductDetails() {
        if (productIdToChange != null) {
            DocumentReference docRef = db.collection("products").document(productIdToChange);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    Product product = task.getResult().toObject(Product.class);
                    if (product != null) {
                        tvProductName.setText(product.getName());
                        tvProductDescription.setText(product.getDescription());
                        tvProductQuantity.setText("Quantity: " + product.getQuantity());
                        tvProductChange.setText("Change: " + product.getChange());

                        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                            Glide.with(this).load(product.getImageUrl()).into(ivProductImage);
                        }
                    }
                } else {
                    Log.d("Firestore", "Error getting product details: ", task.getException());
                }
            });
        }
    }

    private void saveProductDetails() {
        if (currentProductId != null && productIdToChange != null) {
            DocumentReference docRef = db.collection("products").document(currentProductId);

            // Assuming you have edit text fields or other inputs for these values
            String newDescription = tvProductDescription.getText().toString(); // Replace with your input field
            String newQuantity = tvProductQuantity.getText().toString().replace("Quantity: ", ""); // Replace with your input field
            String newChange = tvProductChange.getText().toString().replace("Change: ", ""); // Replace with your input field

            docRef.update("description", newDescription,
                            "quantity", newQuantity,
                            "change", newChange,
                            "relatedProductIds", FieldValue.arrayUnion(productIdToChange))
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "Product updated successfully");
                        Toast.makeText(ChangeProductDescriptionActivity.this, "Product saved successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firestore", "Error updating product", e);
                        Toast.makeText(ChangeProductDescriptionActivity.this, "Error saving product", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
