package com.example.capdi2024_ver1;

import static android.widget.Toast.makeText;
import static java.sql.Types.NULL;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button loginbutton = findViewById(R.id.login_button);
        TextView idtext= findViewById(R.id.id_input_text);
        EditText passtext= findViewById(R.id.password_input_text);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence idin = idtext.getText();
                CharSequence pwin = passtext.getText();
                if (idin.length() == 0 || pwin.length() == 0) {
                    makeText(getApplicationContext(), "입력해주세요.", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, ClientPage.class);
                    startActivity(intent);
                }
            }
        });

    }
}