package com.example.oldmarket4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.example.oldmarket4.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import model.Product;

public class ShowProductDescriptionActivity extends BaseActivity {
    private FirebaseFirestore db;
    private TextView tvProductName, tvProductDescription, tvProductQuantity, tvProductChange, tvIsMyUser;
    private ImageView ivProductImage;
    private Button btnOfferChange;
    private boolean isMyUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        InitializeViews();

        // Get Product ID and isMyUser from Intent
        String productId = getIntent().getStringExtra("productId");
        isMyUser = getIntent().getBooleanExtra("isMyUser", false);

        // Display isMyUser in TextView
        tvIsMyUser.setText("Is My User: " + isMyUser);

        if (isMyUser) {
            btnOfferChange.setText("View Offers");
        } else {
            btnOfferChange.setText("Send Offer");
        }

        // Fetch product details from Firestore
        if (productId != null) {
            fetchMyProductDetails(productId);
        }

        setListeners();
    }

    @Override
    protected void InitializeViews() {
        // Initialize Views
        tvProductName = findViewById(R.id.tvProductName);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        tvProductQuantity = findViewById(R.id.tvProductQuantity);
        tvProductChange = findViewById(R.id.tvProductChange);
        ivProductImage = findViewById(R.id.ivProductImage);
        tvIsMyUser = findViewById(R.id.tvIsMyUser);
        btnOfferChange = findViewById(R.id.btnOfferChange);
    }

    @Override
    protected void setListeners() {
        btnOfferChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offerChange();
            }
        });
    }

    private void fetchMyProductDetails(String productId) {
        db.collection("products")
                .whereEqualTo("productId", productId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        Product product = task.getResult().getDocuments().get(0).toObject(Product.class);
                        if (product != null) {
                            // Set product details to views
                            tvProductName.setText(product.getName());
                            tvProductDescription.setText(product.getDescription());
                            tvProductQuantity.setText("Quantity: " + product.getQuantity());
                            tvProductChange.setText("Change: " + product.getChange());

                            // Load image using Glide
                            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                                Glide.with(this).load(product.getImageUrl()).into(ivProductImage);
                            }
                        }
                    } else {
                        Log.d("Firestore", "Error getting product details: ", task.getException());
                    }
                });
    }

    private void offerChange() {
        Intent intent = new Intent(ShowProductDescriptionActivity.this, ChangeProductsActivity.class);
        intent.putExtra("productId", getIntent().getStringExtra("productId")); // Pass current product ID
        intent.putExtra("isMyUser", isMyUser); // Pass isMyUser
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
