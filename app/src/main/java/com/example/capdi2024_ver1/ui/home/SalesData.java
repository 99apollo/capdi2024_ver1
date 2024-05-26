package com.example.capdi2024_ver1.ui.home;

public class SalesData {
    private String name;
    private double price;
    private int salesPerDay;
    private int salesPerWeek;
    private int salesPerMonth;
    private String category;

    public SalesData(String name, double price, int salesPerDay, int salesPerWeek, int salesPerMonth, String category) {
        this.name = name;
        this.price = price;
        this.salesPerDay = salesPerDay;
        this.salesPerWeek = salesPerWeek;
        this.salesPerMonth = salesPerMonth;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getSalesPerDay() {
        return salesPerDay;
    }

    public int getSalesPerWeek() {
        return salesPerWeek;
    }

    public int getSalesPerMonth() {
        return salesPerMonth;
    }

    public String getCategory() {
        return category;
    }

}
