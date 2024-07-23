package com.example.b07demosummer2024;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    public ItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
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
        holder.textViewLotNumber.setText(item.getLotNumber());
        holder.textViewName.setText(item.getName());
        holder.textViewCategory.setText(item.getCategory());
        holder.textViewPeriod.setText(item.getPeriod());
        holder.textViewDescription.setText(item.getDescription());

        // Testing with some default pictures first

        holder.imageViewPicture.setImageResource(R.drawable.default_image); // Replace with your default image resource
        holder.videoViewVideo.setVisibility(View.GONE);

        // Load image
        /*
        new Thread(() -> {
            try {
                InputStream inputStream = new URL(item.getPicture()).openStream();
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                holder.itemView.post(() -> holder.imageViewPicture.setImageBitmap(bitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Load video
        holder.videoViewVideo.setVideoURI(Uri.parse(item.getVideo()));
        holder.videoViewVideo.seekTo(1);
        holder.videoViewVideo.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            holder.videoViewVideo.start();
        });
         */
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textViewLotNumber, textViewName, textViewCategory, textViewPeriod, textViewDescription;
        ImageView imageViewPicture;
        VideoView videoViewVideo;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewLotNumber = itemView.findViewById(R.id.textViewLotNum);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewPeriod = itemView.findViewById(R.id.textViewPeriod);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);

            imageViewPicture = itemView.findViewById(R.id.imageViewImage);
            videoViewVideo = itemView.findViewById(R.id.videoViewVideo);
        }
    }
}
