package com.example.capdi2024_ver1;

public class Purchase {
    private String itemId;
    private String itemName;
    private int price;
    private String date;

    public Purchase() {
        // Default constructor required for calls to DataSnapshot.getValue(Purchase.class)
    }

    public Purchase(String itemId, String itemName, int price, String date) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.date = date;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }
}
