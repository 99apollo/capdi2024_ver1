package com.example.capdi2024_ver1;

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
    }

    public SharedViewModel(String id, String name, String email, List<Purchase> purchases) {
        this();
        setUserId(id);
        setUserName(name);
        setUserEmail(email);
        this.purchases.setValue(purchases);
    }

    public void setUserId(String id) {
        userId.setValue(id);
        fetchPurchases(id);
    }

    public LiveData<String> getUserId() {
        return userId;
    }

    public void setUserName(String name) {
        userName.setValue(name);
    }

    public LiveData<String> getUserName() {
        return userName;
    }

    public void setUserEmail(String email) {
        userEmail.setValue(email);
    }

    public LiveData<String> getUserEmail() {
        return userEmail;
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
