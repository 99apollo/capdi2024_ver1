package com.example.capdi2024_ver1;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> userId = new MutableLiveData<>();
    private final MutableLiveData<String> userName = new MutableLiveData<>();
    private final MutableLiveData<String> userEmail = new MutableLiveData<>();
    private final MutableLiveData<List<Purchase>> purchases = new MutableLiveData<>();

    public void setUserId(String id) {
        userId.setValue(id);
    }

    public MutableLiveData<String> getUserId() {
        return userId;
    }

    public void setUserName(String name) {
        userName.setValue(name);
    }

    public MutableLiveData<String> getUserName() {
        return userName;
    }

    public void setUserEmail(String email) {
        userEmail.setValue(email);
    }

    public MutableLiveData<String> getUserEmail() {
        return userEmail;
    }

    public void setPurchases(List<Purchase> purchasesList) {
        purchases.setValue(purchasesList);
    }

    public MutableLiveData<List<Purchase>> getPurchases() {
        return purchases;
    }
}
