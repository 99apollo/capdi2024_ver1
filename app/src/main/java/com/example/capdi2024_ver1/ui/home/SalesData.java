package com.example.capdi2024_ver1.ui.home;

public class SalesData {
    private String name;
    private String price;
    private String salesPerDay;
    private String salesPerWeek;
    private String salesPerMonth;
    private String category;
    private String itemValue;
    private String imageResource;

    public SalesData(String name, String price, String salesPerDay, String salesPerWeek, String salesPerMonth, String category, String itemValue, String imageResource) {
        this.name = name;
        this.price = price;
        this.salesPerDay = salesPerDay;
        this.salesPerWeek = salesPerWeek;
        this.salesPerMonth = salesPerMonth;
        this.category = category;
        this.itemValue = itemValue;
        this.imageResource = imageResource;
    }

    // Getter 메서드들
    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getSalesPerDay() {
        return salesPerDay;
    }

    public String getSalesPerWeek() {
        return salesPerWeek;
    }

    public String getSalesPerMonth() {
        return salesPerMonth;
    }

    public String getCategory() {
        return category;
    }

    public String getItemValue() {
        return itemValue;
    }

    public String getImageResource() {
        return imageResource;
    }
}
