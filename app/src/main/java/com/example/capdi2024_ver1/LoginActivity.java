package com.example.capdi2024_ver1;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private String username;

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
                        intent.putExtra("userId",user.getUsername());
                        if(user.getUsername().equals("root")){
                            rootcheck = true;
                        }
                        Log.d(TAG, "username : " + user.getUsername());
                        break;
                    }
                }
                if (loggedIn) {
                    // 로그인 성공 시 처리
                    if(rootcheck){
                        Intent intent2=new Intent(context, AdminMainPage.class);
                        context.startActivity(intent2);
                    }else {
                        context.startActivity(intent);
                    }
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
