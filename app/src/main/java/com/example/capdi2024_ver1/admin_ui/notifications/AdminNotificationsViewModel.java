package com.example.capdi2024_ver1.admin_ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AdminNotificationsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AdminNotificationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}