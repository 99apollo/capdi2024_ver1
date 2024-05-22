package com.example.capdi2024_ver1.admin_ui.dashboard;

public class CartCheckItem {
    private String cartId;
    private String userId;

    public CartCheckItem(String cartId, String userId) {
        this.cartId = cartId;
        this.userId = userId;
    }

    public String getCartId() {
        return cartId;
    }

    public String getUserId() {
        return userId;
    }
}

