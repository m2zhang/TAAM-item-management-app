package com.example.b07demosummer2024;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.content.Intent;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance("https://b07-project-c1ef0-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = db.getReference("testDemo");

//      myRef.setValue("B07 Demo!");
        myRef.child("movies").setValue("B07 Demo!");

        Button sendDataButton = findViewById(R.id.sendDataButton);
        sendDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "DID THIS GET REACHED? Yes, yes it did");
                sendDataToFirebase(myRef);
            }
        });

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private void sendDataToFirebase(DatabaseReference myRef) {
        myRef.child("newData").setValue("Hello, Firebase!").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Firebase", "Data written successfully");
            } else {
                Log.e("Firebase", "Error writing data");
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}