package com.example.oldmarket4.activities;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.oldmarket4.R;

import model.User;
import viewmodel.UserViewModel;


public class SignInActivity extends BaseActivity {
    private EditText etUserName;
    private EditText etSisma;
    private Button btnKnisa;
    private UserViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        InitializeViews();
        setObservers();
    }

    @Override
    protected void InitializeViews() {
        etUserName = findViewById(R.id.etUserName);
        etSisma = findViewById(R.id.etSisma);
        btnKnisa = findViewById(R.id.btnKnisa);

        setListeners();
    }


    private void setObservers() {
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        viewModel.getMuteUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    BaseActivity.currentUser = user;
                    Intent intent = new Intent(SignInActivity.this, ShowProductsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void setListeners() {


        btnKnisa.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            viewModel.SignIn(etUserName.getText().toString(), etSisma.getText().toString());
                                        }
                                    }
        );
    }
}




