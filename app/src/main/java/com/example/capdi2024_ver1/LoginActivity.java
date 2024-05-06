package com.example.capdi2024_ver1;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity {

    public static void attemptLogin(Context context, String id, String password) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean loggedIn = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && user.getEmail().equals(id) && user.getPassword().equals(password)) {
                        loggedIn = true;
                        break;
                    }
                }

                if (loggedIn) {
                    // 로그인 성공 시 처리
                    Intent intent = new Intent(context, ClientMainPage.class);
                    context.startActivity(intent);
                } else {
                    // 로그인 실패 시 처리
                    Toast.makeText(context, "아이디 또는 비밀번호가 올바르지 않습니다.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 에러 처리
                Toast.makeText(context, "로그인 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        });
    }

}
