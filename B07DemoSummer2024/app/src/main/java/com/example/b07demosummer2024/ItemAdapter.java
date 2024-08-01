package com.example.b07demosummer2024;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import android.net.Uri;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.VideoView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<Item> itemList;
    private GeneralUserHomeFragment fragment;
    private int selectedPosition = -1; // No selection by default
    private OnItemSelectedListener onItemSelectedListener;

    public ItemAdapter(List<Item> itemList, OnItemSelectedListener onItemSelectedListener) {
        this.itemList = itemList;
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public ItemAdapter(List<Item> itemList, GeneralUserHomeFragment fragment) {
        this.itemList = itemList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.textViewLotNumber.setText(String.valueOf(item.getLotNumber()));
        holder.textViewName.setText(item.getName());
        holder.textViewCategory.setText(item.getCategory());
        holder.textViewPeriod.setText(item.getPeriod());
        holder.textViewDescription.setText(item.getDescription());

        holder.radioButtonSelect.setChecked(position == selectedPosition);
        holder.radioButtonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int previousSelectedPosition = selectedPosition;
                // Update the selected position
                selectedPosition = holder.getAdapterPosition();
                // Notify the adapter to refresh the list
                // Notify the adapter to refresh only the updated items
                notifyItemChanged(previousSelectedPosition);
                notifyItemChanged(selectedPosition);
                if (onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(item);
                }
            }
        });


        String pictureUrl = item.getPicture();
        // old condition: pictureUrl != null && !pictureUrl.isEmpty()
        if (pictureUrl.startsWith("https://firebasestorage")) {
            Log.i("Picture url apparently not empty", pictureUrl);
            Picasso.get()
                    .load(pictureUrl)
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image)
                    .into(holder.imageViewPicture);
        } else {
            holder.imageViewPicture.setVisibility(View.GONE);
        }


        String videoPath = item.getVideo();

        if (videoPath != null && videoPath.startsWith("https://firebasestorage") && !videoPath.isEmpty()) {
        holder.videoViewVideo.setVisibility(View.VISIBLE);

        // Reference to the video in Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference videoRef = storage.getReferenceFromUrl(videoPath);

        // Get download URL
        videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
            holder.videoViewVideo.setVideoURI(uri);
            holder.videoViewVideo.setOnPreparedListener(mp -> {
                mp.setLooping(true);
                holder.videoViewVideo.start();
            });
        }).addOnFailureListener(e -> {
            holder.videoViewVideo.setVisibility(View.GONE);
        });
        } else {
            // Handle the case where the URL is empty or null
            holder.videoViewVideo.setVisibility(View.GONE);
        }



        //holder.imageViewPicture.setImageResource(R.drawable.default_image);
        //holder.videoViewVideo.setVisibility(View.GONE);
        Log.i("setting images + vid", "finish one time");
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public Item getItem(int position) {
        return itemList.get(position);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textViewLotNumber, textViewName, textViewCategory, textViewPeriod, textViewDescription;
        ImageView imageViewPicture;
        VideoView videoViewVideo;
        RadioButton radioButtonSelect;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewLotNumber = itemView.findViewById(R.id.textViewLotNum);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewPeriod = itemView.findViewById(R.id.textViewPeriod);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);

            imageViewPicture = itemView.findViewById(R.id.imageViewImage);
            videoViewVideo = itemView.findViewById(R.id.videoViewVideo);
            radioButtonSelect = itemView.findViewById(R.id.radioButtonSelect);

        }
    }
}
