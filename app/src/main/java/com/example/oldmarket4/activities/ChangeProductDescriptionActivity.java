package com.example.oldmarket4.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.oldmarket4.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.List;

import model.Product;

// ChangeProductDescriptionActivity extends BaseActivity that extends AppCompatActivity(android)
public class ChangeProductDescriptionActivity extends BaseActivity {

    private FirebaseFirestore db;
    private TextView tvProductName, tvProductDescription, tvProductQuantity, tvProductChange;
    private ImageView ivProductImage;
    private Button btnSaveProduct;
    private String currentProductId, productIdToChange;
    private boolean isMyUser;
    private List<String> phoneNumbersList;
    private TextView tvPhoneNumbers;
    private int itemPosition;

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
        isMyUser = getIntent().getBooleanExtra("isMyUser", false);
        itemPosition = getIntent().getIntExtra("itemPosition", -1); // position of product selected

        // add basic data, put something to be invisibile depending on the user type
        InitializeViews();

        // fetch product details from Firestore
        fetchProductToChangeDetails();
        setListeners();
    }

    @Override
    protected void InitializeViews() {
        tvProductName = findViewById(R.id.tvProductName);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        tvProductQuantity = findViewById(R.id.tvProductQuantity);
        tvProductChange = findViewById(R.id.tvProductChange);
        ivProductImage = findViewById(R.id.ivProductImage);
        btnSaveProduct = findViewById(R.id.btnSaveProduct);
        tvPhoneNumbers = findViewById(R.id.tvPhoneNumbers);

        // make one of the text views invisible depending on the user type
        if (isMyUser) {
            btnSaveProduct.setVisibility(View.GONE);
        } else {
            tvPhoneNumbers.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setListeners() {
        btnSaveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(! isMyUser){
                    // if not my user, save need to work
                    saveProductDetails();
                }
            }
        });
    }

    private void fetchProductToChangeDetails() {
        if (productIdToChange != null) {
            // Fetch product details from Firestore, by id
            db.collection("products")
                    .whereEqualTo("productId", productIdToChange)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            Product product = task.getResult().getDocuments().get(0).toObject(Product.class);
                            if (product != null) {
                                // show details from Firestore
                                tvProductName.setText(product.getName());
                                tvProductDescription.setText(product.getDescription());
                                tvProductQuantity.setText("Quantity: " + product.getQuantity());
                                tvProductChange.setText("Change: " + product.getChange());

                                // show image from Firestore
                                // Glide is a library for loading images from URLs into ImageView
                                if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                                    Glide.with(this).load(product.getImageUrl()).into(ivProductImage);
                                }

                                if (isMyUser) {
                                    // if user is my, show phone numbers
                                    fetchMyProductAndShowPhoneNumber(itemPosition);
                                }
                            }
                        } else {
                            Log.d("Firestore", "Error getting product details: ", task.getException());
                        }
                    });
        }
    }

    private void fetchMyProductAndShowPhoneNumber(int itemPosition) {
        if (currentProductId != null && productIdToChange != null) {
            // Fetch product that is not myne details from Firestore, by id
            db.collection("products")
                    .whereEqualTo("productId", currentProductId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            Product currentProduct = task.getResult().getDocuments().get(0).toObject(Product.class);
                            if (currentProduct != null) {
                                DocumentReference docRef = task.getResult().getDocuments().get(0).getReference();
                                if (currentProduct.getPhoneNumbersList() != null) {
                                    // get phone numbers list from Firestore of the other product
                                    phoneNumbersList = new ArrayList<>(currentProduct.getPhoneNumbersList());
                                }
                                String phoneNumber = phoneNumbersList.get(itemPosition);
                                tvPhoneNumbers.setText(phoneNumber);
                            } else {
                                Log.d("Firestore", "No document found with the specified productId");
                                Toast.makeText(ChangeProductDescriptionActivity.this, "No product found with the specified ID.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }


    private void saveProductDetails() {
        // add current product id to relatedProductIds of other product
        // add phone number of my user to phoneNumbersList of other product
        if (currentProductId != null && productIdToChange != null) {
            // Fetch product that is not myne details from Firestore, by id
            db.collection("products")
                    .whereEqualTo("productId", currentProductId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            Product currentProduct = task.getResult().getDocuments().get(0).toObject(Product.class);
                            if (currentProduct != null) {
                                DocumentReference docRef = task.getResult().getDocuments().get(0).getReference();
                                if (currentProduct.getPhoneNumbersList() != null) {
                                    // get phone numbers list from Firestore of the other product
                                    phoneNumbersList = new ArrayList<>(currentProduct.getPhoneNumbersList());
                                }
                                // add current product id to relatedProductIds of other product
                                updateProductWithRelatedIdAndPhone(docRef);
                            } else {
                                Log.d("Firestore", "No document found with the specified productId");
                                Toast.makeText(ChangeProductDescriptionActivity.this, "No product found with the specified ID.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void updateProductWithRelatedIdAndPhone(DocumentReference docRef) {
        if (phoneNumbersList == null) {
            phoneNumbersList = new ArrayList<>();
        }
        // add my user phone number to phoneNumbersList of other product
        phoneNumbersList.add(currentUser.getPhone());
        // update new losts of relatedProductIds and phoneNumbersList of other product in Firestore
        docRef.update("relatedProductIds", FieldValue.arrayUnion(productIdToChange),
                        "phoneNumbersList", phoneNumbersList)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Product updated successfully");
                    Toast.makeText(ChangeProductDescriptionActivity.this, "Product saved successfully!", Toast.LENGTH_SHORT).show();
                    // finish activity, return to last activity
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error updating product", e);
                    Toast.makeText(ChangeProductDescriptionActivity.this, "Error saving product", Toast.LENGTH_SHORT).show();
                });
    }
}
