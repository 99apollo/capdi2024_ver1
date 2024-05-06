package com.example.capdi2024_ver1;

import static android.widget.Toast.makeText;
import static java.sql.Types.NULL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capdi2024_ver1.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button signUpButton = findViewById(R.id.sign_up_button);
        Button loginbutton = findViewById(R.id.login_button);
        TextView idtext = findViewById(R.id.id_input_text);
        EditText passtext = findViewById(R.id.password_input_text);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("user");
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence idin = idtext.getText().toString();
                CharSequence pwin = passtext.getText().toString();
                Map<String, Object> user = new HashMap<>();
                user.put("id", idin);
                user.put("password", pwin);

                // Log를 사용하여 값이 올바르게 설정되었는지 확인
                Log.d(TAG, "idin: " + idin);
                Log.d(TAG, "pwin: " + pwin);

                if (idin.length() == 0 || pwin.length() == 0) {
                    makeText(getApplicationContext(), "입력해주세요.", Toast.LENGTH_LONG).show();
                } else {
                    myRef.child("user").setValue(user).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // 실패 시 처리
                            Log.e(TAG, "Error adding document", e);
                        }
                    });
                    Intent intent = new Intent(MainActivity.this, ClientMainPage.class);
                    startActivity(intent);
                }
            }
        });

        //회원가입
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}
