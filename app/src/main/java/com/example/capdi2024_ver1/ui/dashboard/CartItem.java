package com.example.capdi2024_ver1.ui.dashboard;

public class CartItem {
    private String cartId;
    private String itemId;
    private String itemValue;

    public CartItem(String cartId, String itemId, String itemValue) {
        this.cartId = cartId;
        this.itemId = itemId;
        this.itemValue = itemValue;
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
}
