package com.example.b07demosummer2024;

public class Item {
    private int LotNumber;
    private String Name;
    private String Category;
    private String Period;
    private String Description;
    private String imageVideoReference;

    public Item() {
        // Default constructor required for calls to DataSnapshot.getValue(Item.class)
    }

    // Just make whichever parts null if not included (e.g. for video and/or picture)
    public Item(int lotNumber, String name, String category, String period, String description, String picture, String video) {
        this.LotNumber = lotNumber;
        this.Name = name;
        this.Category = category;
        this.Period = period;
        this.Description = description;
        this.imageVideoReference = imageVideoReference;
    }

    // Getter and Setters
    public int getLotNumber() { return LotNumber; }
    public void setLotNumber(int lotNumber) { this.LotNumber = lotNumber; }

    public String getName() {return Name; }
    public void setName(String name) { this.Name = name; }

    public String getCategory() {return Category; }
    public void setCategory(String category) { this.Category = category; }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) { this.Description = description; }

    public String getPeriod() {return Period; }
    public void setPeriod(String period) { this.Period = period; }

    public String getURL() {
        return imageVideoReference;
    }
    public void setURL(String imageVideoReference ){
        this.imageVideoReference = imageVideoReference;}
}
