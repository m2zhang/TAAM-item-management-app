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

    public String getPicture() { return picture; }

    public String getVideo() { return video; }
}
