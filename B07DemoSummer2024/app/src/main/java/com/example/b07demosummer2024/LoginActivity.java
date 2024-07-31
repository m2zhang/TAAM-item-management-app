package com.example.b07demosummer2024;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;



public class LoginActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private EditText etUsername, etPassword;
    private Button btnLogin;
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
        progressBar = findViewById(R.id.progressBar);

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

                if (authenticateLogin(username, password)){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "nice", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    //Toast.makeText(getApplicationContext(), "Incorrect Credentials", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
    }

    private boolean authenticateLogin(String username, String password) {
        Log.d("LoginActivity","Matching credentials from database.");
        boolean auth = false;
        User login = new User(username, password);
        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    user = null;
                    try {
                        user = dataSnapshot.getValue(User.class);
                    } catch (DatabaseException e) {
                        Log.e("LoginActivity", "Dataconversion error: " + e.getMessage());
                    }

                    if (user != null && user.getUser().equals(username) && user.getPass().equals(password)) {
                        Toast.makeText(getApplicationContext(), "working", Toast.LENGTH_SHORT).show();
                        Log.d("LoginActivity", "Credentials authorised");
                        auth = true;
                        return;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LoginActivity","Database error: " + error.getMessage());
            }
        });
        if(!auth) Toast.makeText(getApplicationContext(), "false", Toast.LENGTH_SHORT).show();
        return auth;
    }


}