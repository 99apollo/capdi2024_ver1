package com.example.capdi2024_ver1;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.capdi2024_ver1.R;
import com.example.capdi2024_ver1.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextEmail, editTextPassword;
    private Button buttonSignUp, buttonVerifyEmail, buttongoback;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextId);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonVerifyEmail = findViewById(R.id.buttonVerifyEmail); // 추가: 이메일 인증 버튼
        // Firebase 데이터베이스 레퍼런스 가져오기
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // 파이어베이스 인증 객체 얻기
        auth = FirebaseAuth.getInstance();

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });

        // 이메일 인증 버튼 클릭 시 동작
        buttonVerifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailCheck();
            }
        });
    }

    private void emailCheck() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (user.isEmailVerified()) {
                        // 이메일 인증이 완료된 경우
                        String userId = user.getUid();
                        String username = editTextUsername.getText().toString().trim();
                        String email = editTextEmail.getText().toString().trim();
                        String password = editTextPassword.getText().toString().trim();
                        User newUser = new User(userId, username, email, password);

                        // 회원 정보 Firebase 데이터베이스에 저장
                        databaseReference.child(userId).setValue(newUser);
                        Toast.makeText(SignUpActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();

                        // MainActivity로 이동
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // 현재 액티비티 종료
                    } else {
                        // 이메일 인증이 완료되지 않은 경우
                        Toast.makeText(SignUpActivity.this, "이메일 인증이 완료되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 사용자 정보 업데이트 실패
                    Log.e(TAG, "Failed to reload user: " + task.getException());
                }
            });
        } else {
            // 사용자가 로그인되어 있지 않은 경우
            Toast.makeText(SignUpActivity.this, "로그인되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }


    private void signUpUser() {
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Firebase Authentication을 사용하여 회원가입 시도
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        if (user != null) {
                            // 이메일 인증 보내기
                            user.sendEmailVerification()
                                    .addOnCompleteListener(emailTask -> {
                                        if (emailTask.isSuccessful()) {
                                            Toast.makeText(SignUpActivity.this, "이메일로 인증 링크가 전송되었습니다.", Toast.LENGTH_LONG).show();
                                            // 이메일 인증 버튼 활성화
                                            buttonVerifyEmail.setEnabled(true);
                                            buttonVerifyEmail.setVisibility(View.VISIBLE);
                                        } else {
                                            Toast.makeText(SignUpActivity.this, "이메일 인증 링크 전송에 실패했습니다.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    } else {
                        // 회원가입 실패 시
                        Toast.makeText(SignUpActivity.this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
