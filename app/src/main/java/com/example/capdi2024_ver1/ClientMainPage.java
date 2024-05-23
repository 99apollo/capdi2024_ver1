package com.example.capdi2024_ver1;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.capdi2024_ver1.databinding.ActivityClientMainPageBinding;
import com.example.capdi2024_ver1.ui.dashboard.CartItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClientMainPage extends AppCompatActivity {

    private ActivityClientMainPageBinding binding;
    private boolean test = false;
    private SharedViewModel sharedViewModel;
    private Handler handler;
    private String cart_ID;
    private Runnable bluetoothDiscoveryRunnable;
    private DatabaseReference cartListRef;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private ArrayList<String> discoveredDevices = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 3;
    private static final int PERMISSION_REQUEST_CODE_S = 101;
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityClientMainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up the Toolbar as ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up Navigation with AppBarConfiguration
        BottomNavigationView navView = binding.navView;
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_client_main_page);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // Intent로부터 데이터 받기
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);  // `SharedViewModel` 가져오기
        sharedViewModel.setUserId(userId);

        cartListRef = FirebaseDatabase.getInstance().getReference("cart_list");
        // `Activity`에서 `NavController`를 사용하여 목적지 변경을 감지

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            Button disconnectButton = findViewById(R.id.disconnect_button);
            Log.d(TAG, "id in request: " + userId);
            cartListRef.child(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        // 사용자 cart_id가 존재할 때
                        disconnectButton.setText("disconnect");
                    } else {
                        disconnectButton.setText("connect");
                    }
                } else {
                    // Firebase에서 오류가 발생했을 때 처리
                }
            });
            if (destination.getId() == R.id.navigation_dashboard) {  // 특정 `Fragment`에서
                disconnectButton.setVisibility(View.VISIBLE);  // `Button`을 보이게 설정
            } else {  // 다른 `Fragment`에서는
                disconnectButton.setVisibility(View.GONE);  // `Button`을 숨김
            }
        });

        // 이 데이터를 Fragment에 전달하는 방법 중 하나는 NavController를 통해 Bundle로 전달하는 것입니다.
        Bundle bundle = new Bundle();
        bundle.putString("userid", userId);
        navController.navigate(R.id.navigation_dashboard, bundle);  // navigation_dashboard에 데이터를 전달
        navController.navigate(R.id.navigation_notifications, bundle);  // navigation_dashboard에 데이터를 전달
        navController.navigate(R.id.navigation_home, bundle);  // navigation_dashboard에 데이터를 전달

        // BluetoothAdapter 초기화
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            // 장치가 Bluetooth을 지원하지 않는 경우 처리
            Toast.makeText(this, "이 장치는 Bluetooth을 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
            finish(); // 앱 종료
        }

        // BluetoothLeScanner 초기화
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        // 위치 권한을 확인하고 요청
        checkAndRequestLocationPermission();
    }

    private void checkAndRequestLocationPermission() {
        // 위치 권한이 허용되어 있는지 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // 위치 권한이 허용되지 않은 경우 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_REQUEST_CODE_S);
        } else {
            Log.e(TAG, "12312412412412421");
            // 권한이 허용된 경우 Bluetooth 장치 검색 시작
            startBluetoothDiscovery();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 액티비티가 종료될 때 Bluetooth 탐지 중지
        stopBluetoothDiscovery();
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
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // BLUETOOTH_SCAN 권한이 허용되지 않은 경우 BLUETOOTH_ADMIN 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSION);
        } else {
            // BLUETOOTH_SCAN 권한이 허용된 경우 Bluetooth 장치 검색 시작
            // 핸들러를 사용하여 일정한 시간 간격으로 블루투스 탐지를 수행
            startBluetoothDiscoveryWithInterval();
        }
    }

    // 주기적으로 BLE 스캔을 수행하는 메서드
    private void startBluetoothDiscoveryWithInterval() {
        handler = new Handler();
        bluetoothDiscoveryRunnable = new Runnable() {
            @Override
            public void run() {
                doBluetoothDiscovery();
                handler.postDelayed(this, 3000); // 4초마다 실행 (2초 스캔 + 2초 대기)
            }
        };
        handler.post(bluetoothDiscoveryRunnable);
    }

    // BLE 스캔을 중지하는 메서드
    private void stopBluetoothDiscovery() {
        if (bluetoothLeScanner != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothLeScanner.stopScan(leScanCallback);
        }
        if (handler != null && bluetoothDiscoveryRunnable != null) {
            handler.removeCallbacks(bluetoothDiscoveryRunnable);
        }
    }

    // BLE 스캔을 시작하는 메서드
    private void doBluetoothDiscovery() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return;
        }

        // 기존의 스캔 작업이 있으면 중지
        stopBluetoothDiscovery();

        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (bluetoothLeScanner != null) {
            // 2초 동안 스캔
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (ActivityCompat.checkSelfPermission(ClientMainPage.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    bluetoothLeScanner.stopScan(leScanCallback);
                }
            }, 2000);
            bluetoothLeScanner.startScan(leScanCallback);
        }
    }

    // BLE 스캔 콜백 정의
    private final ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            if (ActivityCompat.checkSelfPermission(ClientMainPage.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            String deviceName = device.getName();
            String deviceAddress = device.getAddress();
            Log.e(TAG, "name: " + deviceName + " address: " + deviceAddress);
            if ("98:DA:60:02:B8:83".equals(deviceAddress)) {
                showFingerprintDialog(ClientMainPage.this);
                stopBluetoothDiscovery();
            }
            if (!discoveredDevices.contains(deviceName + "\n" + deviceAddress)) {
                discoveredDevices.add(deviceName + "\n" + deviceAddress);

            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (ScanResult result : results) {
                onScanResult(ScanSettings.CALLBACK_TYPE_ALL_MATCHES, result);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e(TAG, "BLE Scan Failed with code: " + errorCode);
        }
    };

    private void showFingerprintDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("결제");
        builder.setMessage("결제 하시겠습니까?");
        stopBluetoothDiscovery();
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 지문인식 로직을 여기에 구현
                // 예를 들어 지문인식 API를 호출하거나 해당 기능을 수행하는 코드를 작성합니다.

            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 취소 버튼 클릭 시 동작 (생략 가능)
                startBluetoothDiscoveryWithInterval();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                // 다른 행동시
                startBluetoothDiscoveryWithInterval();
            }
        });
        builder.show();
    }
    private void removeItemAndCart(){

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
                // 권한이 모두 허용된 경우 Bluetooth 장치 검색 시작
                startBluetoothDiscovery();
            } else {
                // 권한이 거부된 경우 처리
                Toast.makeText(this, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
                // 필요한 권한을 사용자에게 설명하거나 다시 요청할 수 있습니다.
            }
        }
    }
}
