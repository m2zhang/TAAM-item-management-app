package com.example.b07demosummer2024;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.concurrent.CompletableFuture;


public class LoginActivity extends AppCompatActivity {

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

        // init firebase
        db = FirebaseDatabase.getInstance("https://b07-project-c1ef0-default-rtdb.firebaseio.com/");
        itemsRef = db.getReference("AdminProfiles");

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

                authenticateLogin(username, password).thenAccept(isAuthenticated->{
                    progressBar.setVisibility(View.GONE);
                    if (isAuthenticated) {
                        // Authentication successful, make it go to admin page
                        startActivity(new Intent(LoginActivity.this, HomeFragment.class));
                        finish();
                    } else {
                        // Authentication failed, show a toast or handle accordingly
                        Toast.makeText(LoginActivity.this, "Incorrect Credentials.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }



    private CompletableFuture<Boolean> authenticateLogin(String username, String password) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Log.d("LoginActivity","Matching credentials from database.");
        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user;
                boolean auth = false;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    user = null;
                    try {
                        user = dataSnapshot.getValue(User.class);
                    } catch (DatabaseException e) {
                        Log.e("LoginActivity", "Dataconversion error: " + e.getMessage());
                    }

                    if (user != null && user.getUser().equals(username) && user.getPass().equals(password)) {
                        Log.d("LoginActivity", "Credentials authorised");
                        auth = true;
                        break;
                    }

                }
                future.complete(auth);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LoginActivity","Database error: " + error.getMessage());
                future.complete(false);
            }
        });
        return future;
    }


}