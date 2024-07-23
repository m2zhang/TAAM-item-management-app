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
    // haven't fix the button for uploading pictures
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

        //set up periodSpinner
        ArrayAdapter<CharSequence> periodAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.period_array, android.R.layout.simple_spinner_item);
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodSpinner.setAdapter(periodAdapter);

        //follow the requirements - visit the TAAM requirements
        //update the xml files for the dropdown menus - done
        //add category - done
        //traverse the lot number to check duplications - technical issues
        //add the image/videos function
        //submit button

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // need to return the screen
            }
        });

        selectImageVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //function for calling the image
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

        if (lotNumber.isEmpty() || name.isEmpty() || category.isEmpty() || period.isEmpty()
                || description.isEmpty() || imageVideo == null){
            //need to add some notification there
            return;
        }

        checkLotNumberExist(lotNumber);
        /*
         * Need to fix the case if lotNumber has been used already
         * Assume lotNumber is not repeated
         */
        imageVideoReference = storageReference.child(imageVideo.getLastPathSegment());
        uploadTask = imageVideoReference.putFile(imageVideo);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                uploadSucceed[0] = false;
            }
        });
        if (!uploadSucceed[0]) {
            // add a toast to tell that the upload failed, abort the process
            return;
        }

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
        imageVideo = null;
    }

    private void checkLotNumberExist(String lotNumber) {
        ValueEventListener dbListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean lotNumberExistStatus = false;
                for (DataSnapshot itemSnapshot : snapshot.getChildren()){
                    String itemLotNumber = snapshot.child("lotNumber").getValue(String.class);
                    if (lotNumber.equals(itemLotNumber)) {
                        Toast.makeText(getContext(), "This lot number has been used",
                                Toast.LENGTH_SHORT).show();
                        lotNumberExistStatus = true;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Database error", Toast.LENGTH_SHORT).show();
            }
        };
        itemsReference.addListenerForSingleValueEvent(dbListener);
    }

    private void selectMedia() {
        ActivityResultLauncher<PickVisualMediaRequest> pickImageVideo =
                registerForActivityResult(new PickVisualMedia(), uri -> {
                    if (uri != null){
                        //Log.d("Image/Video Picker", "Selected URI" + uri); for debugging
                        imageVideo = uri;
                        Toast.makeText(getContext(), "Image/Video Selected: " + uri,
                                Toast.LENGTH_SHORT).show();
                        //and then i need to save the uri to somewhere else

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
}
