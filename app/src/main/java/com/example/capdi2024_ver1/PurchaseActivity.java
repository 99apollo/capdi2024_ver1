package com.example.capdi2024_ver1;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PurchaseActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Firebase 데이터베이스 참조 가져오기
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
    }

    // 구매 내역 추가 메서드
    public void addPurchase(Context context, String userId, String itemName, int price, String date, int quantity) {
        String purchaseId = databaseReference.child(userId).child("purchases").push().getKey();

        if (purchaseId != null) {
            Purchase purchase = new Purchase(date, purchaseId, itemName, price, quantity);
            databaseReference.child(userId).child("purchases").child(purchaseId).setValue(purchase)
                    .addOnSuccessListener(aVoid -> {
                        // 구매 내역 추가 성공 시 처리
                        Toast.makeText(context, "Purchase added successfully", Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> {
                        // 구매 내역 추가 실패 시 처리
                        Toast.makeText(context, "Failed to add purchase", Toast.LENGTH_LONG).show();
                    });
        }
    }
}

// 예시: 사용자가 구매를 완료한 후
//PurchaseActivity purchaseActivity = new PurchaseActivity();
//purchaseActivity.addPurchase(this, userId, "itemName1", 100, "2023-05-15", 2);
