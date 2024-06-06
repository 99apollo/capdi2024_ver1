package com.example.capdi2024_ver1;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.journeyapps.barcodescanner.CaptureActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

public class BarcodeScannerActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        // QR 코드 스캐너 옵션 설정
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();

        // 바코드 스캐너 인스턴스 생성
        BarcodeScanner scanner = BarcodeScanning.getClient(options);

        // 카메라로 사진 촬영
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // InputImage 생성
            InputImage image = InputImage.fromBitmap(imageBitmap, 0);

            // QR 코드 스캔
            BarcodeScanner scanner = BarcodeScanning.getClient();
            scanner.process(image)
                    .addOnSuccessListener(barcodes -> {
                        for (Barcode barcode : barcodes) {
                            String rawValue = barcode.getRawValue();
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("cart_id", rawValue);
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("BarcodeScannerActivity", "QR 코드 스캔 실패", e);
                        Toast.makeText(BarcodeScannerActivity.this, "QR 코드 스캔 실패", Toast.LENGTH_SHORT).show();
                        setResult(Activity.RESULT_CANCELED);
                        finish();
                    });
        } else {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }
}