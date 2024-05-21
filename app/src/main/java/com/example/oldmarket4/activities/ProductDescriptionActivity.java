package com.example.oldmarket4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.example.oldmarket4.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import model.Product;

public class ProductDescriptionActivity extends BaseActivity {
    private FirebaseFirestore db;
    private TextView tvProductName, tvProductDescription, tvProductQuantity, tvProductChange, tvIsMyUser, tvPhoneNumber;
    private ImageView ivProductImage;
    private EditText etPhoneNumber;
    private Button btnAddPhoneNumber, btnOfferChange;
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

        // Show or hide the button, EditText, and phone number TextView based on isMyUser
        if (isMyUser) {
            btnAddPhoneNumber.setVisibility(View.GONE);
            etPhoneNumber.setVisibility(View.GONE);
            tvPhoneNumber.setVisibility(View.VISIBLE);
        } else {
            btnAddPhoneNumber.setVisibility(View.VISIBLE);
            etPhoneNumber.setVisibility(View.VISIBLE);
            tvPhoneNumber.setVisibility(View.GONE);
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
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnAddPhoneNumber = findViewById(R.id.btnAddPhoneNumber);
        btnOfferChange = findViewById(R.id.btnOfferChange);
    }

    @Override
    protected void setListeners() {
        btnAddPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhoneNumberToProduct();
            }
        });

        btnOfferChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offerChange();
            }
        });
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

                    if (isMyUser && product.getPhoneNumber() != null) {
                        tvPhoneNumber.setText("Phone Number: " + product.getPhoneNumber());
                    }

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

    private void addPhoneNumberToProduct() {
        String phoneNumber = etPhoneNumber.getText().toString();
        String productId = getIntent().getStringExtra("productId");
        if (productId != null && !phoneNumber.isEmpty()) {
            DocumentReference docRef = db.collection("products").document(productId);
            docRef.update("phoneNumber", phoneNumber)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Phone number added successfully"))
                    .addOnFailureListener(e -> Log.d("Firestore", "Error adding phone number", e));
        }
    }

    private void offerChange() {
        Intent intent = new Intent(ProductDescriptionActivity.this, ChangeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
