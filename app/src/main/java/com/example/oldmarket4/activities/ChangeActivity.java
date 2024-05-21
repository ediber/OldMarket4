package com.example.oldmarket4.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oldmarket4.R;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import model.Product;

public class ChangeActivity extends BaseActivity {

    private RecyclerView recyclerViewProducts;
    private ChangeProductAdapter productAdapter;
    private List<Product> productList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        InitializeViews();
        setListeners();

        db = FirebaseFirestore.getInstance();
        fetchProducts();
    }

    @Override
    protected void InitializeViews() {
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        productAdapter = new ChangeProductAdapter(productList, this::addProductToList);
        recyclerViewProducts.setAdapter(productAdapter);
    }

    @Override
    protected void setListeners() {
        // No additional listeners needed
    }

    private void fetchProducts() {
        db.collection("products")
                .whereEqualTo("userId", currentUser.getIdFs()) // Filter by user ID
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            product.setProductId(document.getId());
                            productList.add(product);
                        }
                        productAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void addProductToList(Product product) {
        String currentProductId = getIntent().getStringExtra("productId");
        if (currentProductId != null) {
            db.collection("products").document(currentProductId)
                    .update("relatedProductIds", FieldValue.arrayUnion(product.getProductId()))
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Product ID added successfully"))
                    .addOnFailureListener(e -> Log.d("Firestore", "Error adding product ID", e));
        }
    }
}
