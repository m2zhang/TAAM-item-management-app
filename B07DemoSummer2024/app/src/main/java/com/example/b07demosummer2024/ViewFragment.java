package com.example.b07demosummer2024;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Bundle;

import android.util.Log;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.ImageButton;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;
import com.squareup.picasso.Picasso;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import android.net.Uri;


public class ViewFragment extends Fragment {

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

    public static ViewFragment newInstance(int LotNumber, String Name, String Category,
                                           String Period, String Description, String Picture,
                                           String Video) {
        ViewFragment fragment = new ViewFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // prepare view
        View view = inflater.inflate(R.layout.fragment_dialog_view, container, false);
        setArguments();
        setAttributes(view);

        // close dialog button functionality
        ImageButton buttonClose = view.findViewById(R.id.imageButtonCloseDialog);
        buttonClose.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        return view;
    }

    private void setArguments() {
        if (getArguments() != null) {
            LotNumber = getArguments().getInt(ARG_LOT_NUMBER);
            Name = getArguments().getString(ARG_NAME);
            Category = getArguments().getString(ARG_CATEGORY);
            Period = getArguments().getString(ARG_PERIOD);
            Description = getArguments().getString(ARG_DESCRIPTION);
            Picture = getArguments().getString(ARG_PICTURE);
            Video = getArguments().getString(ARG_VIDEO);
        }
    }

    private void setAttributes(View view) {
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

        // display media details if needed, maximum one image or video permitteD
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

    }
}
