package com.example.oldmarket4.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.oldmarket4.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import model.User;
import viewmodel.UserViewModel;

public class SignUpActivity extends BaseActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "SignUpActivity";
    Button btnSignUp, btnGetLocation;
    EditText etFName, etLName, etAdress, etPNumber, etPassword, etBYear, etEmail;

    private UserViewModel viewModel;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        InitializeViews();
        setObservers();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void InitializeViews() {
        btnSignUp = findViewById(R.id.btnSignUp);
        etLName = findViewById(R.id.etLName);
        etFName = findViewById(R.id.etFName);
        etAdress = findViewById(R.id.etAdress);
        etBYear = findViewById(R.id.etBYear);
        etEmail = findViewById(R.id.etEmail);
        etPNumber = findViewById(R.id.etPNumber);
        etPassword = findViewById(R.id.etPassword);
        btnGetLocation = findViewById(R.id.btnGetLocation);

        setListeners();
    }

    @Override
    protected void setListeners() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                user.setFamily(etLName.getText().toString());
                user.setName(etFName.getText().toString());
                user.setAddress(etAdress.getText().toString());
                user.setBorn(Long.parseLong(etBYear.getText().toString()));
                user.setEmail(etEmail.getText().toString());
                user.setPhone(etPNumber.getText().toString());
                user.setPassword(etPassword.getText().toString());
                //........//

                viewModel.add(user);
            }
        });

        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    Log.d(TAG, "Permission already granted, getting location...");
                    getLastKnownLocation();
                }
            }
        });
    }

    private void getLastKnownLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<android.location.Location>() {
                @Override
                public void onSuccess(android.location.Location location) {
                    if (location != null) {
                        Log.d(TAG, "Location obtained: " + location.toString());
                        Geocoder geocoder = new Geocoder(SignUpActivity.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);
                                etAdress.setText(address.getAddressLine(0));
                            } else {
                                Log.d(TAG, "Geocoder returned empty address list.");
                                Toast.makeText(SignUpActivity.this, "Unable to get address from location", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Geocoder failed", e);
                        }
                    } else {
                        Log.d(TAG, "Location is null");
                        Toast.makeText(SignUpActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(e -> Log.e(TAG, "Failed to get location", e));
        } else {
            Log.d(TAG, "Location permission not granted.");
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission granted, getting location...");
                getLastKnownLocation();
            } else {
                Log.d(TAG, "Permission denied.");
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setObservers() {
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        viewModel.getSuccessOperation().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }
}
