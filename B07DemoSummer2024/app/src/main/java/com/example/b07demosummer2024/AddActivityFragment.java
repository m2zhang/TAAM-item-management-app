package com.example.b07demosummer2024;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.String;

public class AddActivityFragment extends Fragment {
    private EditText lotNumberEditText, nameEditText, descriptionEditText;
    private Spinner categorySpinner, periodSpinner;
    private Button submitButton, cancelButton, selectImageVideoButton;
    private Uri imageVideo;
    private ActivityResultLauncher<PickVisualMediaRequest> pickImageVideo;
    private FirebaseDatabase db;
    private DatabaseReference itemsReference;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_activity, container, false);

        pickImageVideo = registerForActivityResult(new PickVisualMedia(), uri -> {
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

        db = FirebaseDatabase.getInstance("https://b07-project-c1ef0-default-rtdb.firebaseio.com/");
        itemsReference = db.getReference("Items");
        storage = FirebaseStorage.getInstance("gs://b07-project-c1ef0.appspot.com");
        storageReference = storage.getReference();

        lotNumberEditText = view.findViewById(R.id.lotNumberEditText);
        nameEditText = view.findViewById(R.id.nameEditText);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        periodSpinner = view.findViewById(R.id.periodSpinner);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);

        submitButton = view.findViewById(R.id.submitButton2);
        cancelButton = view.findViewById(R.id.cancelButton);
        selectImageVideoButton = view.findViewById(R.id.selectImageVideoButton);

        //set up categorySpinner
        String[] categoryArray = getResources().getStringArray(R.array.category_array);
        ArrayAdapter<CharSequence> categoryAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, new ArrayList<>(Arrays.asList(categoryArray)));
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setSelection(0);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1){
                    addNewItem(categoryAdapter, "Category",
                            ((addNewItemCheckerStatus, newItem) -> {
                        if (addNewItemCheckerStatus){
                            categoryAdapter.add(newItem);
                            categoryAdapter.notifyDataSetChanged();
                            categorySpinner.setSelection(categoryAdapter.getPosition(newItem));
                        }
                        else {
                            categorySpinner.setSelection(0);
                        }
                    }));

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //set up periodSpinner
        String[] periodArray = getResources().getStringArray(R.array.period_array);
        ArrayAdapter<CharSequence> periodAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, new ArrayList<>(Arrays.asList(periodArray)));
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodSpinner.setAdapter(periodAdapter);
        periodSpinner.setSelection(0);
        periodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1){
                    addNewItem(periodAdapter, "Period",
                            ((addNewItemCheckerStatus, newItem) -> {
                                if (addNewItemCheckerStatus){
                                    periodAdapter.add(newItem);
                                    periodAdapter.notifyDataSetChanged();
                                    periodSpinner.setSelection(periodAdapter.getPosition(newItem));
                                }
                                else {
                                    periodSpinner.setSelection(0);
                                }
                            }));

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

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
            }
        });
        return view;
    }

    //for the spinners
    private void addNewItem(ArrayAdapter<CharSequence> adapter, String itemText,
                            final addNewItemChecker isNewCategoryAddedChecker){
        final EditText newChoice = new EditText(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Add a new " + itemText + ":");
        builder.setView(newChoice);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String choice = newChoice.getText().toString().trim();
                for (int index = 0; index < adapter.getCount(); index++){
                    if (adapter.getItem(index).toString().equalsIgnoreCase(choice)){
                        Toast.makeText(getContext(), itemText + " already exists",
                                Toast.LENGTH_SHORT).show();
                        isNewCategoryAddedChecker.result(false, null);
                        dialogInterface.dismiss();
                        return;
                    }
                }
                isNewCategoryAddedChecker.result(true, choice);
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isNewCategoryAddedChecker.result(false, null);
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void addItem(){
        String lotNumberUnparsed = lotNumberEditText.getText().toString().trim();
        Integer lotNumber;
        String name = nameEditText.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString().trim();
        String period = periodSpinner.getSelectedItem().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        final String IMAGE = "image";
        final String VIDEO = "video";

        //check if all of the fields are filled in
        //lot number cannot be a number
        if (lotNumberUnparsed.isEmpty() || name.isEmpty() || category.isEmpty() || period.isEmpty()
                || description.isEmpty() ){
            Toast.makeText(getContext(), "Please fill in all information",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (!lotNumberUnparsed.matches("\\d+")){
            Toast.makeText(getContext(), "Please enter a valid lot number with digits only ",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            lotNumber = Integer.parseInt(lotNumberUnparsed);
        }
        //check whether the lot number is reused
        checkLotNumberExist(lotNumber, (lotNumberExistStatus) -> {
            if (lotNumberExistStatus){
                Toast.makeText(getContext(), "Lot number already exist",
                        Toast.LENGTH_SHORT).show();
            }
            else {
                if (imageVideo == null) {
                    // No media selected, add item directly to database
                    addItemToDatabase(lotNumber, name, category, period, description, "", "");
                    resetInformation();
                }
                else {
                    //check if the image/video uploaded successfully to storage
                    addItemToStorage((uploadSucceedStatus, imageVideoReference) -> {
                        if (uploadSucceedStatus){
                            //add data to database
                        /*
                                addItemToDatabase(lotNumberUnparsed, name, category, period, description,
                                        imageVideoReference);
                                resetInformation();

                         */
                            checkMediaType(imageVideoReference, (mediaTypeCheckerStatus, mediaType) -> {
                                if (mediaTypeCheckerStatus){
                                    if (mediaType.equals(IMAGE)){
                                        addItemToDatabase(lotNumber, name, category, period, description,
                                                imageVideoReference, "");
                                        resetInformation();
                                    }
                                    else if(mediaType.equals(VIDEO)) {
                                        addItemToDatabase(lotNumber, name, category, period, description,
                                                "", imageVideoReference);
                                        resetInformation();
                                    }
                                    else {
                                        Toast.makeText(getContext(), "Unknown Error, Should not happen." +
                                                "Process aborted", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(getContext(), "Image/Video type detection failed. " +
                                            "Process aborted", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            Toast.makeText(getContext(), "Image/Video upload failed. " +
                                    "Process aborted", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }

    private void checkLotNumberExist(Integer lotNumber,
                                     final StatusResultChecker isLotNumberExistChecker){
        ValueEventListener dbListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean lotNumberExists = false;
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Integer itemLotNumber = itemSnapshot.child("lotNumber").getValue(Integer.class);
                    if (Objects.equals(lotNumber, itemLotNumber)) {
                        /*
                        Toast.makeText(getContext(), "This lot number has been used",
                                Toast.LENGTH_SHORT).show();
                        */
                        lotNumberExists = true;
                        break;
                    }
                }
                isLotNumberExistChecker.result(lotNumberExists);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Database error", Toast.LENGTH_SHORT).show();
            }
        };
        itemsReference.addListenerForSingleValueEvent(dbListener);
    }

    private void addItemToStorage(final StorageResultChecker isUploadSuccessfulChecker){
        final StorageReference imageVideoReference;
        UploadTask uploadTask;
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSSZ",
                Locale.CANADA).format(new Date());
        String random = UUID.randomUUID().toString();
        imageVideoReference = storageReference.child(imageVideo.getLastPathSegment()
                + "-" + random + "-" + timeStamp);
        uploadTask = imageVideoReference.putFile(imageVideo);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageVideoReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri){
                        isUploadSuccessfulChecker.result(true, uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isUploadSuccessfulChecker.result(false, null);
                    }
                });
            }
        });
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isUploadSuccessfulChecker.result(false, null);
            }
        });
    }

    private void checkMediaType(String url, final MediaTypeChecker isMediaTypeCheckedChecker){
        StorageReference mediaReference = storage.getReferenceFromUrl(url);
        mediaReference.getMetadata().addOnSuccessListener(metadata -> {
            String mediaType = metadata.getContentType();
            if (mediaType != null){
                if (mediaType.startsWith("image")){
                    isMediaTypeCheckedChecker.result(true, "image");
                }
                else if (mediaType.startsWith("video")){
                    isMediaTypeCheckedChecker.result(true, "video");
                }
                else {
                    Toast.makeText(getContext(), "Media format is not image or video",
                            Toast.LENGTH_SHORT).show();
                    isMediaTypeCheckedChecker.result(false, null);
                }
            }
            else {
                Toast.makeText(getContext(), "Unable to determine media format",
                        Toast.LENGTH_SHORT).show();
                isMediaTypeCheckedChecker.result(false, null);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error: Unable to retrieve media metadata",
                        Toast.LENGTH_SHORT).show();
                isMediaTypeCheckedChecker.result(false, null);
            }
        });
    }

    private void addItemToDatabase(Integer lotNumber, String name, String category, String period,
                                   String description, String picture, String video){
        //itemsReference = db.getReference("Items");
        String id = itemsReference.push().getKey();
        if (id == null){
            return;
        }
        //create a new item to store input information into firebase
        Item item = new Item(lotNumber, name, category,
                period, description, picture, video);
        itemsReference.child(id).setValue(item).addOnCompleteListener(task -> {
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

    private void selectMedia() {
        pickImageVideo.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(PickVisualMedia.ImageAndVideo.INSTANCE)
                .build());
    }

    private void resetInformation() {
        lotNumberEditText.setText("");
        nameEditText.setText("");
        categorySpinner.setSelection(0);
        periodSpinner.setSelection(0);
        descriptionEditText.setText("");
        imageVideo = null;
    }
}

interface StatusResultChecker {
    void result(boolean lotNumberExistStatus);
}

interface StorageResultChecker {
    void result(boolean uploadSucceedStatus, String imageVideoReference);
}

interface MediaTypeChecker {
    void result(boolean mediaTypeCheckerStatus, String mediaType);
}

interface addNewItemChecker {
    void result(boolean addNewItemCheckerStatus, String newItem);
}
