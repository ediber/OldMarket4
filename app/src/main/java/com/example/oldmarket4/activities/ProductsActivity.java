package com.example.oldmarket4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oldmarket4.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import model.Product;

public class ProductsActivity extends BaseActivity {
    private FirebaseFirestore db;
    private RecyclerView rvProducts;
    private FloatingActionButton fabAdd;
    private Button btnFilterByUserId;
    private Button btnFilterNotByUserId;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        InitializeViews();

        db = FirebaseFirestore.getInstance();
    }

    private void fetchDataByUserId(boolean isSameUserId) {
        String currentUserId = currentUser.getIdFs();
        if (isSameUserId) {
            db.collection("products")
                    .whereEqualTo("userId", currentUserId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            productList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = document.toObject(Product.class);
                                product.setProductId(document.getId());
                                productList.add(product);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("Firestore", "Error getting documents: ", task.getException());
                        }
                    });
        } else {
            db.collection("products")
                    .whereNotEqualTo("userId", currentUserId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            productList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = document.toObject(Product.class);
                                product.setProductId(document.getId());
                                productList.add(product);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("Firestore", "Error getting documents: ", task.getException());
                        }
                    });
        }
    }

    @Override
    protected void InitializeViews() {
        rvProducts = findViewById(R.id.rvProducts);
        fabAdd = findViewById(R.id.fabAdd);
        btnFilterByUserId = findViewById(R.id.btnFilterByUserId);
        btnFilterNotByUserId = findViewById(R.id.btnFilterNotByUserId);

        // Setting up the RecyclerView and Adapter
        adapter = new ProductAdapter(productList, new ProductAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                // Handle the long click event
                Product product = productList.get(position);
                // Start ProductDescriptionActivity with the product ID
                Intent intent = new Intent(ProductsActivity.this, ProductDescriptionActivity.class);
                intent.putExtra("productId", product.getProductId());
                startActivity(intent);
            }
        });
        rvProducts.setAdapter(adapter);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setHasFixedSize(true);

        setListeners();
    }

    @Override
    protected void setListeners() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductsActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });

        btnFilterByUserId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchDataByUserId(true);
            }
        });

        btnFilterNotByUserId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchDataByUserId(false);
            }
        });
    }
}
