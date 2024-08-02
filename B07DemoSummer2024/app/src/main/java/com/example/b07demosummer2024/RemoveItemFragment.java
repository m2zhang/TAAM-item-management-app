package com.example.b07demosummer2024;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

public class RemoveItemFragment extends Fragment {
    private static final String ARG_LOT_NUMBER = "LotNumber";
    private static final String ARG_NAME = "Name";
    private static final String ARG_CATEGORY = "Category";
    private static final String ARG_PERIOD = "Period";
    private static final String ARG_DESCRIPTION = "Description";
    private static final String ARG_PICTURE = "Picture";
    private static final String ARG_VIDEO = "Video";

    private int LotNumber;
    private String Name;
    private String Category;
    private String Period;
    private String Description;
    private String Picture;
    private String Video;

    public static RemoveItemFragment newInstance(Integer LotNumber, String Name, String Category, String Period, String Description, String Picture, String Video) {
        RemoveItemFragment fragment = new RemoveItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LOT_NUMBER, LotNumber);
        args.putString(ARG_NAME, Name);
        args.putString(ARG_CATEGORY, Category);
        args.putString(ARG_PERIOD, Period);
        args.putString(ARG_DESCRIPTION, Description);
        args.putString(ARG_PICTURE, Picture);
        args.putString(ARG_VIDEO, Video);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_remove_dialog, container, false);

        if (getArguments() != null) {
            LotNumber = getArguments().getInt(ARG_LOT_NUMBER);
            Name = getArguments().getString(ARG_NAME);
            Category = getArguments().getString(ARG_CATEGORY);
            Period = getArguments().getString(ARG_PERIOD);
            Description = getArguments().getString(ARG_DESCRIPTION);
            Picture = getArguments().getString(ARG_PICTURE);
            Video = getArguments().getString(ARG_VIDEO);        }

        TextView textViewName2 = view.findViewById(R.id.textViewName2);
        TextView textViewLotNum2 = view.findViewById(R.id.textViewLotNum2);
        TextView textViewPeriod2 = view.findViewById(R.id.textViewPeriod2);
        TextView textCategory2 = view.findViewById(R.id.textCategory2);
        TextView textViewDescription2 = view.findViewById(R.id.textViewDescription2);
        ImageView imageViewPicture = view.findViewById(R.id.imageView3);
        VideoView videoViewVideo = view.findViewById(R.id.videoView);

        // Display item details if needed
        textViewName2.setText(Name);
        textViewLotNum2.setText(String.valueOf(LotNumber));
        textViewPeriod2.setText(Period);
        textCategory2.setText(Category);
        textViewDescription2.setText(Description);

        // displayItemDetails(lotNumber, textViewName2, textViewLotNum2, textViewPeriod2, textCategory2, textViewDescription2, imageViewPicture, videoViewVideo);

        // display media details if needed
        if (Picture.startsWith("https://firebasestorage")) {
            Picasso.get()
                    .load(Picture)
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image)
                    .into(imageViewPicture);
        } else {
            imageViewPicture.setVisibility(View.GONE);
        }

        if (Video != null && Video.startsWith("https://firebasestorage") && !Video.isEmpty()) {
            videoViewVideo.setVisibility(View.VISIBLE);
            // Reference to the video in Firebase Storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference videoRef = storage.getReferenceFromUrl(Video);

            // Get download URL
            videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                videoViewVideo.setVideoURI(uri);
                videoViewVideo.setOnPreparedListener(mp -> {
                    mp.setLooping(true);
                    videoViewVideo.start();
                });
            }).addOnFailureListener(e -> {
                videoViewVideo.setVisibility(View.GONE);
            });
        } else {
            videoViewVideo.setVisibility(View.GONE);
        }


        Button buttonYes = view.findViewById(R.id.buttonYes);
        Button buttonCancel = view.findViewById(R.id.buttonCancel);

        buttonYes.setOnClickListener(v -> {
            removeItemFromDatabase(LotNumber);
            getParentFragmentManager().popBackStack();
        });

        buttonCancel.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        return view;
    }

    private void removeItemFromDatabase(Integer LotNumber) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("Items").orderByChild("lotNumber").equalTo(LotNumber);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // There should be only one child
                    DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("RemoveItemFragment", "onCancelled", databaseError.toException());
            }
        });
    }
}
