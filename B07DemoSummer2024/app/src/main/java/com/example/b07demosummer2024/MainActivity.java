package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//       Example of using the Firebase Database
//        db = FirebaseDatabase.getInstance("https://b07-demo-summer-2024-default-rtdb.firebaseio.com/");
//        DatabaseReference myRef = db.getReference("testDemo");
//
////        myRef.setValue("B07 Demo!");
//        myRef.child("movies").setValue("B07 Demo!");

        // Find the search button
        Button buttonSearch = findViewById(R.id.buttonSearch);

        // Set a click listener for the search button
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
