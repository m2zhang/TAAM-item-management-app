package com.example.b07demosummer2024;

import com.google.firebase.storage.StorageReference;

public class Item {
    private String lotNumber;
    private String name;
    private String category;
    private String period;
    private String description;
    private StorageReference imageVideo;

    public Item(String lotNumber, String name, String category, String period, String description, StorageReference imageVideo) {
        this.lotNumber = lotNumber;
        this.name = name;
        this.category = category;
        this.period = period;
        this.description = description;
        this.imageVideo = imageVideo;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getPeriod() {
        return period;
    }

    public String getDescription() {
        return description;
    }

    public StorageReference getImageVideo() {
        return imageVideo;
    }
}
