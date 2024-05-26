package com.example.capdi2024_ver1;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String id;
    private String username;
    private String email;
    private String password;
    private Map<String, Purchase> purchases;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        this.purchases = new HashMap<>();
    }

    public User(String id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.purchases = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Map<String, Purchase> getPurchases() {
        return purchases;
    }

    public void addPurchase(String purchaseId, Purchase purchase) {
        this.purchases.put(purchaseId, purchase);
    }
}
