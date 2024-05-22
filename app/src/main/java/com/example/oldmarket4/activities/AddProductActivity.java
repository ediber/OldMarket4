package com.example.oldmarket4.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.oldmarket4.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AddProductActivity extends BaseActivity {
    private ImageView photo;
    private EditText nameView;
    private EditText desView;
    private EditText quaView;
    private View saveView;
    private EditText changView;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        InitializeViews();
        setListeners();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    protected void InitializeViews() {
        photo = findViewById(R.id.imageViewPhoto);
        nameView = findViewById(R.id.etProductName1);
        desView = findViewById(R.id.etProductDescripion);
        quaView = findViewById(R.id.etProductQuantity);
        saveView = findViewById(R.id.btnSaveProduct);
        changView = findViewById(R.id.etProductChange);
    }

    @Override
    protected void setListeners() {
        photo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
                builder.setTitle("Add Photo!");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 1);
                        } else if (options[item].equals("Choose from Gallery")) {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, 2);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
                return false;
            }
        });

        saveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameView.getText().toString();
                String des = desView.getText().toString();
                String qua = quaView.getText().toString();
                String change = changView.getText().toString();

                if (imageBitmap != null) {
                    uploadImageAndSaveProduct(name, des, qua, change, currentUser.getIdFs());
                } else {
                    Toast.makeText(AddProductActivity.this, "Please add a photo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadImageAndSaveProduct(String name, String description, String quantity, String change, String userId) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");

        UploadTask uploadTask = imagesRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                saveProductToFirestore(name, description, quantity, change, userId, imageUrl);
            }).addOnFailureListener(e -> {
                Log.w("Firebase", "Error getting download URL", e);
                Toast.makeText(AddProductActivity.this, "Error uploading image", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Log.w("Firebase", "Error uploading image", e);
            Toast.makeText(AddProductActivity.this, "Error uploading image", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveProductToFirestore(String name, String description, String quantity, String change, String userId, String imageUrl) {
        List<String> relatedProductIds = new ArrayList<>(); // Initialize an empty list
        List<String> phoneNumbersList = new ArrayList<>(); // Initialize an empty list for phone numbers
        String productId = UUID.randomUUID().toString().substring(0, 5); // Generate a random 5-character string

        Map<String, Object> product = new HashMap<>();
        product.put("productId", productId); // Add the productId to the Firestore document
        product.put("name", name);
        product.put("description", description);
        product.put("quantity", quantity);
        product.put("change", change);
        product.put("userId", userId);
        product.put("imageUrl", imageUrl);
        product.put("relatedProductIds", relatedProductIds); // Add the empty list to Firestore
        product.put("phoneNumbersList", phoneNumbersList); // Add the empty list to Firestore

        db.collection("products")
                .add(product)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firebase", "DocumentSnapshot added with ID: " + documentReference.getId());
                    Toast.makeText(AddProductActivity.this, "Product saved successfully!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(AddProductActivity.this, ShowProductsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Log.w("Firebase", "Error adding document", e);
                    Toast.makeText(AddProductActivity.this, "Error saving product", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1 && data != null && data.getExtras() != null) {
                // Handle camera result
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                photo.setImageBitmap(imageBitmap);
            } else if (requestCode == 2 && data != null) {
                // Handle gallery result
                Uri selectedImageUri = data.getData();
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    photo.setImageBitmap(imageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
