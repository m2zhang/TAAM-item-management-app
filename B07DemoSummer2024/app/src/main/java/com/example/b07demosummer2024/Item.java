package com.example.b07demosummer2024;

import com.google.firebase.storage.StorageReference;

public class Item {
    private Integer lotNumber;
    private String name;
    private String category;
    private String period;
    private String description;
    private String picture;
    private String video;


    public Item() {
        // Default constructor required for calls to DataSnapshot
    }

    public Item(Integer lotNumber, String name, String category, String period, String description,
                String picture, String video) {
        this.lotNumber = lotNumber;
        this.name = name;
        this.category = category;
        this.period = period;
        this.description = description;
        this.picture = picture;
        this.video = video;
    }

    public Integer getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(Integer lotNumber) { this.lotNumber = lotNumber; }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) { this.category = category; }


    public String getPeriod() {
        return period;
    }
    public void setPeriod(String period) { this.period = period; }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) { this.description = description; }


    public String getPicture() { return picture; }

    public void setPicture(String picture) { this.picture = picture; }

    public String getVideo() { return video; }

    public void setVideo(String video) { this.video = video; }

}
