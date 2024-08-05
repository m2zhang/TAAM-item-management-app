package com.example.b07demosummer2024;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CompletableFuture;

public class LoginActivityModel {
    FirebaseDatabase db;
    public LoginActivityModel(){
        db = FirebaseDatabase.getInstance("https://b07-project-c1ef0-default-rtdb.firebaseio.com/");
    }

    public CompletableFuture<Boolean> queryDB(LoginActivityPresenter presenter, String username, String password){
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Log.d("LoginActivity","Matching credentials from database.");
        DatabaseReference query = db.getReference("AdminProfiles");
        // move this func
        query.addListenerForSingleValueEvent(new ValueEventListener() {
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
