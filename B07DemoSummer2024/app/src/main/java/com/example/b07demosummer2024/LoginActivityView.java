package com.example.b07demosummer2024;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.concurrent.CompletableFuture;


public class LoginActivityView extends AppCompatActivity {
    LoginActivityPresenter presenter;
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private Button btnBack;
    private ProgressBar progressBar;
    private FirebaseDatabase db;
    private DatabaseReference itemsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        presenter = new LoginActivityPresenter(this, new LoginActivityModel());
        // init firebase
//        db = FirebaseDatabase.getInstance("https://b07-project-c1ef0-default-rtdb.firebaseio.com/");
//        itemsRef = db.getReference("AdminProfiles");

        // init ui elements
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnBack = findViewById(R.id.back);
        progressBar = findViewById(R.id.progressBar);

        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etUsername.getText().toString().trim();
                final String password = etPassword.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "Enter username!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                authenticateLogin().thenAccept(isAuthenticated->{
                    progressBar.setVisibility(View.GONE);
                    if (isAuthenticated) {
                        // Authentication successful, make it go to admin page
                        startActivity(new Intent(LoginActivityView.this, MainActivity.class));
                        finish();
                    } else {
                        // Authentication failed, show a toast or handle accordingly
                        Toast.makeText(LoginActivityView.this, "Incorrect Credentials.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }


    private CompletableFuture<Boolean> authenticateLogin() {
        return presenter.checkDB(etUsername.getText().toString(), etPassword.getText().toString());
    }


}