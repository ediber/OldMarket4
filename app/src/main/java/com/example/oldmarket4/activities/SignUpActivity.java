package com.example.oldmarket4.activities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.oldmarket4.R;

import model.User;
import viewmodel.UserViewModel;


public class SignUpActivity extends BaseActivity {
    Button btnSignUp;
    EditText etFName,etLName,etAdress,etPNumber,etPassword,etBYear,etEmail;

    private UserViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        InitializeViews();
        setObservers();
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
        //.......//

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
                Intent intent=new Intent(SignUpActivity.this,SignInActivity.class);
                startActivity(intent);
            }


        });

    }

    private  void setObservers(){
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }
}