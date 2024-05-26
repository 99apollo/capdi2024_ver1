package com.example.capdi2024_ver1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
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
                boolean loggedIn = false;
                boolean rootcheck = false;
                String userId = null;
                String username = null;
                String email = null;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && user.getEmail().equals(id) && user.getPassword().equals(password)) {
                        loggedIn = true;
                        userId = user.getId();
                        username = user.getUsername();
                        email = user.getEmail();

                        if (user.getUsername().equals("root")) {
                            rootcheck = true;
                        }

                        // Map을 List로 변환하여 ViewModel에 설정
                        Map<String, Purchase> purchaseMap = user.getPurchases();
                        List<Purchase> purchaseList = new ArrayList<>(purchaseMap.values());

                        // ViewModel에 사용자 정보 설정
                        SharedViewModel sharedViewModel = new ViewModelProvider((AppCompatActivity) context).get(SharedViewModel.class);
                        sharedViewModel.setUserId(userId);
                        sharedViewModel.setUserName(username);
                        sharedViewModel.setUserEmail(email);
                        sharedViewModel.setPurchases(purchaseList);

                        Log.d(TAG, "ViewModel ID: " + sharedViewModel.getUserId());
                        Log.d(TAG, "ViewModel Name: " + sharedViewModel.getUserName().getValue());
                        Log.d(TAG, "ViewModel Email: " + sharedViewModel.getUserEmail().getValue());

                        break;
                    }
                }
                if (loggedIn) {
                    Intent intent;
                    if (rootcheck) {
                        intent = new Intent(context, AdminMainPage.class);
                    } else {
                        intent = new Intent(context, ClientMainPage.class);
                        // 인텐트에 사용자 데이터 추가
                        intent.putExtra("userId", userId);
                        intent.putExtra("userName", username);
                        intent.putExtra("userEmail", email);
                    }

                    context.startActivity(intent);
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

    private static class SharedViewModelFactory implements ViewModelProvider.Factory {
        private final String id;
        private final String name;
        private final String email;
        private final List<Purchase> purchases;

        public SharedViewModelFactory(String id, String name, String email, List<Purchase> purchases) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.purchases = purchases;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass.isAssignableFrom(SharedViewModel.class)) {
                return (T) new SharedViewModel(id, name, email, purchases);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
