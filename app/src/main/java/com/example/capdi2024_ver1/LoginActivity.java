package com.example.capdi2024_ver1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
    }

    public static void attemptLogin(Context context, String id, String password) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent intent = new Intent(context, ClientMainPage.class);
                boolean loggedIn = false;
                boolean rootcheck = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && user.getEmail().equals(id) && user.getPassword().equals(password)) {
                        loggedIn = true;
                        intent.putExtra("userId", user.getId());
                        if (user.getUsername().equals("root")) {
                            rootcheck = true;
                        }

                        // ViewModel에 사용자 정보 설정
                        SharedViewModel sharedViewModel = new ViewModelProvider((AppCompatActivity) context).get(SharedViewModel.class);
                        sharedViewModel.setUserId(user.getId());
                        sharedViewModel.setUserName(user.getUsername());
                        sharedViewModel.setUserEmail(user.getEmail());

                        // Map을 List로 변환하여 ViewModel에 설정
                        Map<String, Purchase> purchaseMap = user.getPurchases();
                        List<Purchase> purchaseList = new ArrayList<>(purchaseMap.values());
                        sharedViewModel.setPurchases(purchaseList);

                        Log.d(TAG, "username : " + user.getUsername());
                        break;
                    }
                }
                if (loggedIn) {
                    if (rootcheck) {
                        Intent intent2 = new Intent(context, AdminMainPage.class);
                        context.startActivity(intent2);
                    } else {
                        context.startActivity(intent);
                    }
                } else {
                    Toast.makeText(context, "아이디 또는 비밀번호가 올바르지 않습니다.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "로그인 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        });
    }



}
