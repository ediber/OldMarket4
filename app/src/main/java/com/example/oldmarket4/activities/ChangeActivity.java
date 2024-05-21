package com.example.oldmarket4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
    private String productId;

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

        boolean isMyUser = getIntent().getBooleanExtra("isMyUser", false); // Retrieve isMyUser
        productId = getIntent().getStringExtra("productId"); // Retrieve productId

        if(! isMyUser){ // if the original product is not mine the exchange products are mine
            fetchMyProducts();
        }
    }

    @Override
    protected void InitializeViews() {
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        productAdapter = new ChangeProductAdapter(productList, new ChangeProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String productId) {
                addProductToList(productId);
            }
        });
        recyclerViewProducts.setAdapter(productAdapter);
    }

    @Override
    protected void setListeners() {
        // No additional listeners needed
    }

    private void fetchMyProducts() {
            db.collection("products")
                    .whereEqualTo("userId", currentUser.getIdFs()) // Adjust according to your implementation
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            productList.clear();
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

    private void addProductToList(String productIdToChange) {

        String currentProductId = getIntent().getStringExtra("productId");
        Intent intent = new Intent(ChangeActivity.this, ChangeProductDescriptionActivity.class);
        intent.putExtra("currentProductId", currentProductId);
        intent.putExtra("productIdToChange", productIdToChange);
        startActivity(intent);
    }

}
