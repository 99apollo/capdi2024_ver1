package com.example.capdi2024_ver1;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<String> userId;
    private MutableLiveData<String> userName;
    private MutableLiveData<String> userEmail;
    private MutableLiveData<List<Purchase>> purchases;

    public SharedViewModel() {
        userId = new MutableLiveData<>();
        userName = new MutableLiveData<>();
        userEmail = new MutableLiveData<>();
        purchases = new MutableLiveData<>();

        Log.d(TAG, "ViewModel Constructor - ID: " + userId.getValue());
        Log.d(TAG, "ViewModel Constructor - Name: " + userName.getValue());
        Log.d(TAG, "ViewModel Constructor - Email: " + userEmail.getValue());
        Log.d(TAG, "ViewModel Constructor - Purchases: " + purchases.toString());
    }

    public SharedViewModel(String id, String name, String email, List<Purchase> purchases) {
        this();
        setUserId(id);
        setUserName(name);
        setUserEmail(email);
        this.purchases.setValue(purchases);

        Log.d(TAG, "ViewModel Constructor - ID: " + id);
        Log.d(TAG, "ViewModel Constructor - Name: " + name);
        Log.d(TAG, "ViewModel Constructor - Email: " + email);
        Log.d(TAG, "ViewModel Constructor - Purchases: " + purchases.toString());

    }

    public void setUserId(String id) {
        userId.setValue(id);
        Log.d(TAG, "setUserId - ID: " + this.userId.getValue());
        if (id != null) {
            fetchPurchases(id);
        }
    }

    public LiveData<String> getUserId() {
        return userId;
    }

    public void setUserName(String name) {

        userName.setValue(name);
        Log.d(TAG, "setUserName - Name: " + this.userName.getValue());
    }

    public LiveData<String> getUserName() {
        return userName;
    }

    public void setUserEmail(String email) {
        userEmail.setValue(email);
        Log.d(TAG, "setUserEmail - Email: " + this.userEmail.getValue());
    }

    public LiveData<String> getUserEmail() {
        return userEmail;
    }

    public void setPurchases(List<Purchase> purchases) {
        this.purchases.setValue(purchases);
        Log.d(TAG, "setPurchases - Purchases: " + this.purchases.getValue().toString());
    }
    public LiveData<List<Purchase>> getPurchases() {
        return purchases;
    }

    private void fetchPurchases(String userId) {
        DatabaseReference purchasesRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("purchases");
        purchasesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Purchase> purchaseList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Purchase purchase = snapshot.getValue(Purchase.class);
                    purchaseList.add(purchase);
                }
                purchases.setValue(purchaseList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
            }
        });
    }
}
