package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddActivityFragment extends Fragment {
    private EditText lotNumberEditText, nameEditText, categoryEditText, periodEditText;
    private EditText descriptionEditText;
    // haven't fix the button for uploading pictures
    private Button submitButton, cancelButton;
    private FirebaseDatabase db;
    private DatabaseReference itemsref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_activity, container, false);

        db = FirebaseDatabase.getInstance("https://b07-project-c1ef0-default-rtdb.firebaseio.com/");

        lotNumberEditText = view.findViewById(R.id.lotNumberEditText);
        nameEditText = view.findViewById(R.id.nameEditText);
        categoryEditText = view.findViewById(R.id.categoryEditText);
        periodEditText = view.findViewById(R.id.periodEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);

        submitButton = view.findViewById(R.id.submitButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        return view;


    }

    private void addItem(){
        String lotNumber = lotNumberEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String category = categoryEditText.getText().toString().trim();
        String period = periodEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (lotNumber.isEmpty() || name.isEmpty() || category.isEmpty() || period.isEmpty()
                || description.isEmpty()){
            //need to add some notification there
            return;
        }


    }
}
