package com.example.capdi2024_ver1;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.MutableLiveData;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> userId = new MutableLiveData<>();

    public void setUserId(String id) {
        userId.setValue(id);
    }

    public MutableLiveData<String> getUserId() {
        return userId;
    }
}
