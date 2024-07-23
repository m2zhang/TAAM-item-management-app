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
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class AddActivityFragment extends Fragment {
    private EditText lotNumberEditText, nameEditText, descriptionEditText;
    private Spinner categorySpinner, periodSpinner;
    private Button submitButton, cancelButton, selectImageVideoButton;
    private Uri imageVideo;
    private FirebaseDatabase db;
    private DatabaseReference itemsReference;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_activity, container, false);

        db = FirebaseDatabase.getInstance("https://b07-project-c1ef0-default-rtdb.firebaseio.com/");
        itemsReference = FirebaseDatabase.getInstance().getReference("Items");
        storage = FirebaseStorage.getInstance("gs://b07-project-c1ef0.appspot.com");
        storageReference = storage.getReference();

        lotNumberEditText = view.findViewById(R.id.lotNumberEditText);
        nameEditText = view.findViewById(R.id.nameEditText);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        periodSpinner = view.findViewById(R.id.periodSpinner);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);

        submitButton = view.findViewById(R.id.submitButton);
        cancelButton = view.findViewById(R.id.cancelButton);
        selectImageVideoButton = view.findViewById(R.id.selectImageVideoButton);

        //set up categorySpinner
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.category_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setSelection(0);

        //set up periodSpinner
        ArrayAdapter<CharSequence> periodAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.period_array, android.R.layout.simple_spinner_item);
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodSpinner.setAdapter(periodAdapter);
        periodSpinner.setSelection(0);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });

        selectImageVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectMedia();
                resetInformation();
            }
        });

        return view;
    }

    private void addItem(){
        String lotNumber = lotNumberEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString().trim();
        String period = periodSpinner.getSelectedItem().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        StorageReference imageVideoReference;
        UploadTask uploadTask;

        final boolean[] uploadSucceed = {true};
        final boolean[] lotNumberExistStatus = {false};

        //check if all of the fields are filled in
        if (lotNumber.isEmpty() || name.isEmpty() || category.isEmpty() || period.isEmpty()
                || description.isEmpty() || imageVideo == null){
            Toast.makeText(getContext(), "Please fill in all information",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //check whether the lot number is reused
        if (checkLotNumberExist(lotNumber, lotNumberExistStatus)){
            Toast.makeText(getContext(), "This lot number already exist",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //upload image to firebase storage
        imageVideoReference = storageReference.child(imageVideo.getLastPathSegment());
        uploadTask = imageVideoReference.putFile(imageVideo);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                uploadSucceed[0] = false;
            }
        });
        if (!uploadSucceed[0]) {
            Toast.makeText(getContext(), "Image/Video upload failed. Process aborted",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //create a new item to store input information into firebase
        itemsReference = db.getReference("Items");
        String id = itemsReference.push().getKey();
        if (id == null){
            return;
        }
        Item item = new Item(lotNumber, name, category, period, description, imageVideoReference);

        itemsReference.child(id).setValue(item).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Collection added", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        Toast.makeText(getContext(), "Failed to add item", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
        );
    }

    private boolean checkLotNumberExist(String lotNumber, final boolean[] lotNumberExistStatus) {
        ValueEventListener dbListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()){
                    String itemLotNumber = snapshot.child("lotNumber").getValue(String.class);
                    if (lotNumber.equals(itemLotNumber)) {
                        Toast.makeText(getContext(), "This lot number has been used",
                                Toast.LENGTH_SHORT).show();
                        lotNumberExistStatus[0] = true;
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Database error", Toast.LENGTH_SHORT).show();
            }
        };
        lotNumberExistStatus[0] = false;
        itemsReference.addListenerForSingleValueEvent(dbListener);
        return lotNumberExistStatus[0];
    }

    private void selectMedia() {
        ActivityResultLauncher<PickVisualMediaRequest> pickImageVideo =
                registerForActivityResult(new PickVisualMedia(), uri -> {
                    if (uri != null){
                        //Log.d("Image/Video Picker", "Selected URI" + uri); for debugging
                        imageVideo = uri;
                        Toast.makeText(getContext(), "Image/Video Selected: " + uri,
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //Log.d("Image/Video Picker", "No Image/Video Selected"); for debugging
                        Toast.makeText(getContext(), "No Image/Video Selected",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        pickImageVideo.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(PickVisualMedia.ImageAndVideo.INSTANCE)
                .build());
    }

    private void resetInformation() {
        lotNumberEditText.setText("");
        nameEditText.setText("");
        categorySpinner.setSelection(0);
        periodSpinner.setSelection(0);
        imageVideo = null;
    }
}
