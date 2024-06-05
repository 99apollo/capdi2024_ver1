package com.example.capdi2024_ver1.admin_ui.dashboard;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemData implements Parcelable {
    private String item_value;
    private String categori;
    private String name;
    private String manufacturer;
    private double price;
    private int amount;

    private String location;

    private String imageResource;

    // Constructor
    public ItemData(String item_value, String categori, String name, String manufacturer, double price, int amount) {
        this.item_value = item_value;
        this.categori = categori;
        this.name = name;
        this.manufacturer = manufacturer;
        this.price = price;
        this.amount = amount;
    }

    // Constructor with location
    public ItemData(String itemValue, String category, String name, String manufacturer, double price, int amount, String location, String imageResource) {
        this.item_value = itemValue;
        this.categori = category;
        this.name = name;
        this.manufacturer = manufacturer;
        this.price = price;
        this.amount = amount;
        this.location = location;
        this.imageResource = imageResource;
    }


    // Parcelable 인터페이스 구현
    protected ItemData(Parcel in) {
        item_value = in.readString();
        categori = in.readString();
        name = in.readString();
        manufacturer = in.readString();
        price = in.readDouble();
        amount = in.readInt();
        location = in.readString();
        imageResource = in.readString();
    }

    public static final Parcelable.Creator<ItemData> CREATOR = new Parcelable.Creator<ItemData>() {
        @Override
        public ItemData createFromParcel(Parcel in) {
            return new ItemData(in);
        }

        @Override
        public ItemData[] newArray(int size) {
            return new ItemData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(item_value);
        dest.writeString(categori);
        dest.writeString(name);
        dest.writeString(manufacturer);
        dest.writeDouble(price);
        dest.writeInt(amount);
        dest.writeString(location);
        dest.writeString(imageResource);
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

    public String getLocation() {
        return location;
    }

    public String getCategory() {
        return categori;
    }

    public String getImageResource() { return imageResource; } // 추가된 getter
    public void setImageResource(String imageResource) { this.imageResource = imageResource; } // 추가된 setter

}
