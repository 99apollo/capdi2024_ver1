package com.example.capdi2024_ver1.ui.dashboard;

public class CartItem {
    private String cartId;
    private String itemId;
    private String itemValue;
    private String count;

    public CartItem(String cartId, String itemId, String itemValue, String count) {
        this.cartId = cartId;
        this.itemId = itemId;
        this.itemValue = itemValue;
        this.count = count;
    }

    public String getCartId() {
        return cartId;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemValue() {
        return itemValue;
    }
    public String getCount(){return count;}
}
