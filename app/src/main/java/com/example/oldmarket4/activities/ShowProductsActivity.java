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
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

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
    private List<Product> myProductList = new ArrayList<>();
    private List<Product> otherProductList = new ArrayList<>();

    private boolean isMyUser = false;
    private boolean isDeleteMode = false;
    private int selectedIndex = -1; // Field to store the index of the selected item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

     //   FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        InitializeViews();
    }

    private void fetchDataByUserId(boolean isSameUserId) {
        String currentUserId = currentUser.getIdFs();
        if (isSameUserId) {  // my products
            db.collection("products")
                    .whereEqualTo("userId", currentUserId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            myProductList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = document.toObject(Product.class);
                                myProductList.add(product);
                            }
                            adapter.setProducts(myProductList);
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("Firestore", "Error getting documents: ", task.getException());
                        }
                    });
        } else { // other products
            db.collection("products")
                    .whereNotEqualTo("userId", currentUserId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            otherProductList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = document.toObject(Product.class);
                                otherProductList.add(product);
                            }
                            adapter.setProducts(otherProductList);
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("Firestore", "Error getting documents: ", task.getException());
                        }
                    });
        }
    }



    private void fetchDataByDifferentUserIdForDeletion(){
        String currentUserId = currentUser.getIdFs();
        // productlist with products that for current user
        Product productToDelete = myProductList.get(selectedIndex);

        // here the productlist will be updated with products that are not for current user,
        // from the list of products here we will delete current product id and current user phone number if needed
        // New method to fetch data of products that are not for the current user because they are not mine
        // and delete my product from other products if they are connected to it
        db.collection("products")
                .whereNotEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        otherProductList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            otherProductList.add(product);
                        }

                        deleteProductById(productToDelete.getProductId());
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void deleteProductById(String productId) {
        // Delete my product from the database
        db.collection("products")
                .whereEqualTo("productId", productId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        WriteBatch batch = db.batch();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            batch.delete(document.getReference());
                        }
                        batch.commit()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "DocumentSnapshot successfully deleted!");
                                    Product deletedProduct = adapter.getProducts().get(selectedIndex);
                                    selectedIndex = -1;
                                    // here we finished deleting the product from the list of products
                                    // and now we will delete the product from the other products list
                                    updateRelatedProducts(deletedProduct.getProductId());
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("Firestore", "Error deleting document", e);
                                });
                    } else {
                        Log.w("Firestore", "No matching document found for productId: " + productId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error finding document", e);
                });
    }

    private void updateRelatedProducts(String productId) {
        for (Product product : otherProductList) {
            // go over all products, go to the list inside this product,
            // and check if the product in his list has my productId
            List<String> relatedIds = product.getRelatedProductIds();
            List<String> phoneNumbers = product.getPhoneNumbersList();
            if (relatedIds != null && relatedIds.contains(productId)) {
                // index of product to delete from the list
                int index = relatedIds.indexOf(productId);
                relatedIds.remove(index);
                if (phoneNumbers != null && phoneNumbers.size() > index) {
                    // delete the phone number from the list with the same index
                    phoneNumbers.remove(index);
                }
                // update firebase with the new list of related products,
                // after deleting product from the list
                db.collection("products")
                        .whereEqualTo("productId", product.getProductId())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    document.getReference().update("relatedProductIds", relatedIds, "phoneNumbersList", phoneNumbers)
                                            .addOnCompleteListener(updateTask -> {
                                                if (updateTask.isSuccessful()) {
                                                    Log.d("Firestore", "Related product IDs and phone numbers updated successfully");
                                                } else {
                                                    Log.w("Firestore", "Error updating document", updateTask.getException());
                                                }
                                            });
                                }
                            } else {
                                Log.w("Firestore", "No matching document found for productId: " + product.getProductId());
                            }
                        });
            }
        }
        // Fetch the updated list of products after all updates are done
        fetchDataByUserId(isMyUser);
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

        //שמכניס את כל הערכים לתוך ה recyclerview
        // adapter get list of products and a listener for item click
        adapter = new ShowProductAdapter(myProductList, new ShowProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Product product = adapter.getProducts().get(position);
                // going to another activity
                Intent intent = new Intent(ShowProductsActivity.this, ShowProductDescriptionActivity.class);
                // sending data to another activity
                // so activity will know what product to show and if its for current user or not
                intent.putExtra("productId", product.getProductId());
                intent.putExtra("isMyUser", isMyUser);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(int position) {
                // isDeleteMode is true if the product is mine
                if (isDeleteMode) {
                    // Clear selection from all items
                    for (Product product : adapter.getProducts()) {
                        product.setSelected(false);
                    }
                    // Select the clicked item and store its index
                    Product product = adapter.getProducts().get(position);
                    product.setSelected(true);
                    selectedIndex = position;
                    // Notify adapter of the changes so it will be updated in the UI
                    adapter.notifyDataSetChanged();
                    // Show the delete button
                    btnDelete.setVisibility(View.VISIBLE);
                }
            }
        });

        // בין האדפטר ל recyclerviewמחבר
        // connects between adapter and recyclerview
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

        // New delete button listener, here the delete of product is done
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle deletion of the selected item from Firebase Firestore
                // index of product to delete
                if (selectedIndex != -1) {
                    fetchDataByDifferentUserIdForDeletion();

                    // Clear the selection state of products
                    myProductList.get(selectedIndex).setSelected(false);

                    // Hide the delete button
                    btnDelete.setVisibility(View.GONE);
                }
            }
        });
    }
}
