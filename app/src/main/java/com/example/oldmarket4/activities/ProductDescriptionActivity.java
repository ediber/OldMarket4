package com.example.oldmarket4.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oldmarket4.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import model.Product;

public class ProductDescriptionActivity extends BaseActivity {
    private FirebaseFirestore db;
    private TextView tvProductName, tvProductDescription, tvProductQuantity, tvProductChange;
    private ImageView ivProductImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        InitializeViews();

        // Get Product ID from Intent
        String productId = getIntent().getStringExtra("productId");

        // Fetch product details from Firestore
        if (productId != null) {
            fetchMyProductDetails(productId);
        }
    }

    @Override
    protected void InitializeViews() {
        // Initialize Views
        tvProductName = findViewById(R.id.tvProductName);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        tvProductQuantity = findViewById(R.id.tvProductQuantity);
        tvProductChange = findViewById(R.id.tvProductChange);
        ivProductImage = findViewById(R.id.ivProductImage);
    }

    @Override
    protected void setListeners() {

    }

    private void fetchMyProductDetails(String productId) {
        DocumentReference docRef = db.collection("products").document(productId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Product product = task.getResult().toObject(Product.class);
                if (product != null) {
                    // Set product details to views
                    tvProductName.setText(product.getName());
                    tvProductDescription.setText(product.getDescription());
                    tvProductQuantity.setText("Quantity: " + product.getQuantity());
                    tvProductChange.setText("Change: " + product.getChange());

/*                    // Load image using Glide
                    if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                        Glide.with(this).load(product.getImageUrl()).into(ivProductImage);
                    }*/
                }
            } else {
                Log.d("Firestore", "Error getting product details: ", task.getException());
            }
        });
    }
}