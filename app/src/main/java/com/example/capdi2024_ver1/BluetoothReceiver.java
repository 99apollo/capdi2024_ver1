package com.example.capdi2024_ver1;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BluetoothReceiver extends BroadcastReceiver {
    // 특정 블루투스 기기의 MAC 주소
    private static final String TARGET_DEVICE_MAC_ADDRESS = "64:03:7F:A8:E8:F7"; // 예시 MAC 주소

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Log.e(TAG,"On bluetooth Activity");

        if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
            // 연결된 블루투스 기기의 MAC 주소 가져오기
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String connectedDeviceMacAddress = device.getAddress();
            Log.e(TAG,"test bluetooth address"+connectedDeviceMacAddress);

            // 특정 기기와의 연결 여부 확인
            if (TARGET_DEVICE_MAC_ADDRESS.equals(connectedDeviceMacAddress)) {
                // 특정 기기와의 연결이 확인되면 지문인식 다이얼로그 띄우기
                Toast.makeText(context, "특정 블루투스 기기가 연결되었습니다.", Toast.LENGTH_SHORT).show();

                // 지문인식 다이얼로그를 띄우는 예시
                showFingerprintDialog(context);
            }
        }
    }

    // 지문인식 다이얼로그를 띄우는 메서드
    private void showFingerprintDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("지문 인증");
        builder.setMessage("블루투스 기기에 연결되었습니다. 지문을 스캔하여 계속하시겠습니까?");
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
            }
        });
        builder.show();
    }
}


