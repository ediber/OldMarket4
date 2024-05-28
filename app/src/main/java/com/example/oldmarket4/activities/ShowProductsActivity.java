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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import model.Product;
import service.MusicService;

public class ShowProductsActivity extends BaseActivity {
    private FirebaseFirestore db;
    private RecyclerView rvProducts;
    private FloatingActionButton fabAdd;
    private Button btnFilterByUserId;
    private Button btnFilterNotByUserId;
    private Button btnStartMusic;
    private Button btnStopMusic;
    private Button btnDelete;
    private ShowProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private boolean isMyUser = false;
    private boolean isDeleteMode = false;
    private int selectedIndex = -1; // Field to store the index of the selected item

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
                                productList.add(product);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("Firestore", "Error getting documents: ", task.getException());
                        }
                    });
        }
    }

    private void deleteProductById(String productId) {
        db.collection("products")
                .whereEqualTo("productId", productId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("products").document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "DocumentSnapshot successfully deleted!");
                                        fetchDataByUserId(isMyUser);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("Firestore", "Error deleting document", e);
                                    });
                        }
                    } else {
                        Log.w("Firestore", "No matching document found for productId: " + productId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error finding document", e);
                });
    }


    @Override
    protected void InitializeViews() {
        rvProducts = findViewById(R.id.rvProducts);
        fabAdd = findViewById(R.id.fabAdd);
        btnFilterByUserId = findViewById(R.id.btnFilterByUserId);
        btnFilterNotByUserId = findViewById(R.id.btnFilterNotByUserId);
        btnStartMusic = findViewById(R.id.btnStartMusic);
        btnStopMusic = findViewById(R.id.btnStopMusic);
        btnDelete = findViewById(R.id.btnDelete); // New delete button

        adapter = new ShowProductAdapter(productList, new ShowProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Product product = productList.get(position);
                Intent intent = new Intent(ShowProductsActivity.this, ShowProductDescriptionActivity.class);
                intent.putExtra("productId", product.getProductId());
                intent.putExtra("isMyUser", isMyUser);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(int position) {
                if (isDeleteMode) {
                    // Clear selection from all items
                    for (Product product : productList) {
                        product.setSelected(false);
                    }
                    // Select the clicked item and store its index
                    Product product = productList.get(position);
                    product.setSelected(true);
                    selectedIndex = position;
                    // Notify adapter of the changes
                    adapter.notifyDataSetChanged();
                    btnDelete.setVisibility(View.VISIBLE);
                }
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
                Intent intent = new Intent(ShowProductsActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });

        btnFilterByUserId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMyUser = true;
                isDeleteMode = true; // Enable delete mode
                fetchDataByUserId(true);
            }
        });

        btnFilterNotByUserId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMyUser = false;
                isDeleteMode = false; // Disable delete mode
                fetchDataByUserId(false);
                btnDelete.setVisibility(View.INVISIBLE);
            }
        });

        btnStartMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(ShowProductsActivity.this, MusicService.class));
            }
        });

        btnStopMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(ShowProductsActivity.this, MusicService.class));
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle deletion of the selected item from Firebase Firestore
                if (selectedIndex != -1) {
                    Product productToDelete = productList.get(selectedIndex);
                    deleteProductById(productToDelete.getProductId());

                    // Clear the selection state of products
                    productList.get(selectedIndex).setSelected(false);
                    selectedIndex = -1;

                    // Hide the delete button
                    btnDelete.setVisibility(View.GONE);
                }
            }
        });
    }
}
