package com.example.oldmarket4.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.example.oldmarket4.R;

import android.view.View;

import android.widget.Button;

public class MainActivity extends BaseActivity {
    Button btnSignIn,btnSignUp;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitializeViews();
    }

    @Override
    protected void InitializeViews() {
        btnSignIn=findViewById(R.id.btnSignIn);
        btnSignUp=findViewById(R.id.btnSignUp);

        setListeners();
    }

    @Override
    protected void setListeners() {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SignInActivity.class);
                startActivity(intent);
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}