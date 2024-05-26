package com.example.capdi2024_ver1;
public class Purchase {
    private String date;
    private String itemID;
    private String itemName;
    private int price;

    // 생성자, getter 및 setter

    public Purchase() {}

    public Purchase(String date, String itemID, String itemName, int price) {
        this.date = date;
        this.itemID = itemID;
        this.itemName = itemName;
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Purchase{" +
                "date='" + date + '\'' +
                ", itemID='" + itemID + '\'' +
                ", itemName='" + itemName + '\'' +
                ", price=" + price +
                '}';
    }
}
