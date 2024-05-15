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

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.oldmarket4.R;

import java.util.HashMap;
import java.util.Map;
import com.google.firebase.firestore.FirebaseFirestore;



public class AddProductActivity extends BaseActivity {
    private ImageView photo;
    private EditText nameView;
    private EditText desView;
    private EditText  QuaWiew;
    private  View saveView;
    private EditText changView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        InitializeViews();
        setListeners();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void InitializeViews() {
        photo = findViewById(R.id.imageViewPhoto);
        nameView = findViewById(R.id.etProductName1);
        desView  = findViewById(R.id.etProductDescripion);
        QuaWiew  = findViewById(R.id.etProductQuantity);
        saveView  = findViewById(R.id.btnSaveProduct);
        changView  = findViewById(R.id.etProductQuantity);
    }

    @Override
    protected void setListeners() {
        photo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
                AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
                builder.setTitle("Add Photo!");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo"))
                        {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 1);

                        }
                        else if (options[item].equals("Choose from Gallery"))
                        {
                            Intent intent = new   Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, 2);
                        }
                        else if (options[item].equals("Cancel")) {
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
                String qua = QuaWiew.getText().toString();
                String change = changView.getText().toString();
                saveProductToFirestore(name, des, qua,change);
            }
        });
    }

    private void saveProductToFirestore(String name, String description, String quantity,String change) {
        Map<String, Object> product = new HashMap<>();
        product.put("name", name);
        product.put("description", description);
        product.put("quantity", quantity);
        product.put("change", change);
        // Add a new document with a generated ID
        db.collection("products")
                .add(product)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firebase", "DocumentSnapshot added with ID: " + documentReference.getId());
                    Toast.makeText(AddProductActivity.this, "Product saved successfully!", Toast.LENGTH_SHORT).show();
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
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                photo.setImageBitmap(imageBitmap);
            } else if (requestCode == 2 && data != null) {
                // Handle gallery result
                Uri selectedImageUri = data.getData();
                photo.setImageURI(selectedImageUri);  // Set the ImageView to display the selected image
            }
        }
    }
}