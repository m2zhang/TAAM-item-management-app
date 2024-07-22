package com.example.b07demosummer2024;

import android.app.Activity;

public class LoginActivity extends Activity {
    db = FirebaseDatabase.getInstance("https://b07-demo-summer-2024-default-rtdb.firebaseio.com/");
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.LoginActivity);

//       Example of using the Firebase Database
//        db = FirebaseDatabase.getInstance("https://b07-demo-summer-2024-default-rtdb.firebaseio.com/");
//        DatabaseReference myRef = db.getReference("testDemo");
//
////        myRef.setValue("B07 Demo!");
//        myRef.child("movies").setValue("B07 Demo!");

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
