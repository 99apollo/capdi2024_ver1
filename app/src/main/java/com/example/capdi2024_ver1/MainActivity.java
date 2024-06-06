package com.example.capdi2024_ver1;

import static android.widget.Toast.makeText;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static java.sql.Types.NULL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<String> discoveredDevices = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 3;
    private static final int PERMISSION_REQUEST_CODE_S = 101;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button signUpButton = findViewById(R.id.sign_up_button);
        Button loginbutton = findViewById(R.id.login_button);
        TextView idtext = findViewById(R.id.id_input_text);
        EditText passtext = findViewById(R.id.password_input_text);

        // BluetoothAdapter 초기화
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // 장치가 Bluetooth을 지원하지 않는 경우 처리
            Toast.makeText(this, "이 장치는 Bluetooth을 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
            finish(); // 앱 종료
        }

        // 위치 권한을 확인하고 요청
        checkAndRequestLocationPermission();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("user");
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = idtext.getText().toString();
                String password = passtext.getText().toString();

                // LoginActivity를 통해 로그인 시도
                LoginActivity.attemptLogin(MainActivity.this, id, password);
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

    private void startBluetoothDiscovery() {
        // Bluetooth 기능이 활성화되어 있는지 확인
        if (!bluetoothAdapter.isEnabled()) {
            // Bluetooth이 비활성화된 경우, 사용자에게 활성화할 것인지 알리는 메시지를 표시
            Toast.makeText(this, "Bluetooth를 활성화해주세요", Toast.LENGTH_SHORT).show();

            // Bluetooth를 활성화하기 위한 시스템 설정 화면으로 이동
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
        } else {
            // Bluetooth 장치 검색 시작
            checkAndRequestBluetoothScanPermission();
            //checkAndRequestBluetoothConnectPermission();
        }
    }
    private void checkAndRequestBluetoothScanPermission() {
        Log.e(TAG, "chaseefasegseg");
        // BLUETOOTH_SCAN 권한이 허용되어 있는지 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_CONNECT)!=PackageManager.PERMISSION_GRANTED
        ) {
            // BLUETOOTH_SCAN 권한이 허용되지 않은 경우 BLUETOOTH_ADMIN 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSION);
        } else {
            // 권한이 허용된 경우 Bluetooth 장치 검색 시작
            startBluetoothDiscovery();
        }
    }

    private void checkAndRequestLocationPermission() {
        // 위치 권한이 허용되어 있는지 확인
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 위치 및 기타 권한이 허용되지 않은 경우 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE_S);
        } else {
            // 모든 권한이 이미 허용된 경우 여기에 추가 작업 수행
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            // Bluetooth 활성화 요청의 결과 확인
            if (resultCode == RESULT_OK) {
                // Bluetooth이 성공적으로 활성화된 경우, Bluetooth 장치 검색 시작

            } else {
                // 사용자가 Bluetooth 활성화를 거부한 경우
                Toast.makeText(this, "Bluetooth을 활성화하지 않았습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE_S) {
            // 권한 요청 결과 처리
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    // 하나라도 권한이 거부된 경우
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
            } else {
                // 권한이 거부된 경우 처리
                Toast.makeText(this, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
                // 필요한 권한을 사용자에게 설명하거나 다시 요청할 수 있습니다.
            }
        }
    }
}
