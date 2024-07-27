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

import java.io.Serializable;

public class RemoveItemFragment extends Fragment {
    private FirebaseDatabase db;
    private DatabaseReference itemsRef;

    private static final String ARG_LOT_NUMBER = "ARG_LOT_NUMBER";
    private int LotNumber;

    public static RemoveItemFragment newInstance(int LotNumber) {
        RemoveItemFragment fragment = new RemoveItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LOT_NUMBER, LotNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_remove_dialog, container, false);

        if (getArguments() != null) {
            LotNumber = getArguments().getInt(ARG_LOT_NUMBER);
        }

        TextView textViewName2 = view.findViewById(R.id.textViewName2);
        TextView textViewLotNum2 = view.findViewById(R.id.textViewLotNum2);
        TextView textViewPeriod2 = view.findViewById(R.id.textViewPeriod2);
        TextView textCategory2 = view.findViewById(R.id.textCategory2);
        TextView textViewDescription2 = view.findViewById(R.id.textViewDescription2);
        ImageView imageViewPicture = view.findViewById(R.id.imageView3);
        VideoView videoViewVideo = view.findViewById(R.id.videoView);

        // Display item details if needed
        // displayItemDetails(lotNumber, textViewName2, textViewLotNum2, textViewPeriod2, textCategory2, textViewDescription2, imageViewPicture, videoViewVideo);

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

    private void removeItemFromDatabase(int LotNumber) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("Items").orderByChild("LotNumber").equalTo(LotNumber);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
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



