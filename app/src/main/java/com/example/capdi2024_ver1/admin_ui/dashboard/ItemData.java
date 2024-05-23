package com.example.capdi2024_ver1.admin_ui.dashboard;

public class ItemData {
    private String item_value;
    private String categori;
    private String name;
    private String manufacturer;
    private double price;
    private int amount;

    // Constructor
    public ItemData(String item_value, String categori, String name, String manufacturer, double price, int amount) {
        this.item_value = item_value;
        this.categori = categori;
        this.name = name;
        this.manufacturer = manufacturer;
        this.price = price;
        this.amount = amount;
    }

    // Getters and setters
    public String getItem_value() { return item_value; }
    public void setItem_value(String item_value) { this.item_value = item_value; }

    public String getCategori() { return categori; }
    public void setCategori(String categori) { this.categori = categori; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
}
