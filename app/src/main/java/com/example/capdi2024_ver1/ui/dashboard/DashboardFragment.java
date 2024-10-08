package com.example.capdi2024_ver1.ui.dashboard;


import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.capdi2024_ver1.BarcodeScannerActivity;
import com.example.capdi2024_ver1.R;
import com.example.capdi2024_ver1.SharedViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import androidx.lifecycle.ViewModelProvider;

public class DashboardFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartItemAdapter adapter;
    private DatabaseReference cartListRef;
    private DatabaseReference cartListCheckRef;
    private RequestQueue requestQueue;
    private String userId;  // 전달받은 userId를 저장할 필드
    private SharedViewModel sharedViewModel;
    private Handler handler;
    private Runnable fetchRunnable;
    private List<CartItem> currentCartItems = new ArrayList<>(); // 기존 데이터 저장
    private ActivityResultLauncher<Intent> qrCodeLauncher; // QR 코드 스캔 결과를 받을 ActivityResultLauncher

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // 초기화
        handler = new Handler(Looper.getMainLooper());
        recyclerView = root.findViewById(R.id.cart_item_listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cartListRef = FirebaseDatabase.getInstance().getReference("cart_list");
        cartListCheckRef = FirebaseDatabase.getInstance().getReference("cart_check_list");
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        Button disconnet_Button = requireActivity().findViewById(R.id.disconnect_button);
        disconnet_Button.setVisibility(View.VISIBLE);

        // QR 코드 스캔 결과를 받을 ActivityResultLauncher 초기화
        qrCodeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        handleQrResult(result.getData());
                    } else {
                        Toast.makeText(requireActivity(), "Cancelled", Toast.LENGTH_LONG).show();
                    }
                });

        // ViewModel ID 로그
        if (sharedViewModel.getUserId().getValue() != null) {
            Log.d(MotionEffect.TAG, "Dashboard ViewModel ID: " + sharedViewModel.getUserId().getValue());
        } else {
            Log.d(MotionEffect.TAG, "Dashboard ViewModel ID: null");
        }

        if (sharedViewModel.getUserName().getValue() != null) {
            Log.d(MotionEffect.TAG, "Dashboard ViewModel Name: " + sharedViewModel.getUserName().getValue());
        } else {
            Log.d(MotionEffect.TAG, "Dashboard ViewModel Name: null");
        }

        if (sharedViewModel.getUserEmail().getValue() != null) {
            Log.d(MotionEffect.TAG, "Dashboard ViewModel Email: " + sharedViewModel.getUserEmail().getValue());
        } else {
            Log.d(MotionEffect.TAG, "Dashboard ViewModel Email: null");
        }

        sharedViewModel.getUserId().observe(getViewLifecycleOwner(), new Observer<String>() {  // `LiveData` 관찰
            @Override
            public void onChanged(String newUserId) {
                userId = newUserId;
                checkCartID(userId, disconnet_Button);
                Log.d(TAG, "idin: " + userId);
                checkAndRequestCartId(userId);
            }
        });

        disconnet_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (disconnet_Button.getText().equals("disconnect")) {
                    cartListRef.child(userId).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DataSnapshot dataSnapshot = task.getResult();
                            if (dataSnapshot.exists()) {
                                // 사용자 cart_id가 존재할 때
                                String cartId = dataSnapshot.child("cart_id").getValue(String.class);
                                releaseCart(cartId);
                            } else {
                                Toast.makeText(requireActivity(), "연결된 카트가 없습니다.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // Firebase에서 오류가 발생했을 때 처리
                        }
                    });
                } else {
                    checkAndRequestCartId1(userId);
                }
            }
        });

        return root;
    }
    private void startPeriodicFetching(String cartID) {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper()); // `Handler` 초기화
        }

        stopPeriodicFetching(); // 이미 실행 중인 주기적 작업 중단

        fetchRunnable = new Runnable() {
            @Override
            public void run() {
                sendHttpRequest(cartID); // HTTP 요청 수행
                handler.postDelayed(this, 1000); // 10초 후에 다시 실행
                Log.e(TAG, "test" + cartID);
            }
        };

        if (handler != null) { // `Handler`가 유효한지 확인
            handler.post(fetchRunnable); // 최초 실행
        }
    }

    private void stopPeriodicFetching() {
        if (fetchRunnable != null && handler != null) {
            handler.removeCallbacks(fetchRunnable); // 주기적 작업 중단
        }
    }

    private void checkCartID(String userId, Button button) {
        Log.d(TAG, "id in requset: " + userId);
        cartListRef.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    // 사용자 cart_id가 존재할 때
                    button.setText("disconnect");

                } else {
                    button.setText("connect");
                }
            } else {
                // Firebase에서 오류가 발생했을 때 처리
            }
        });
    }

    private void releaseCart(String cartId) {
        // `RequestQueue`가 `null`인지 확인하고, `null`일 경우 초기화
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(requireContext());
        }
        String url = "http://3.35.9.191/test2.php?username=app&password=app2024&cart_id=" + cartId;  // 요청할 URL

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<CartItem> cartItems = parseJson(response, cartId);
                        Log.e(TAG, "data : " + cartItems);
                        if (cartItems.isEmpty()) {
                            cartListRef.child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    cartListCheckRef.child(cartId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(requireActivity(), "연결된 카트가 해제되었습니다.", Toast.LENGTH_LONG).show();
                                            Button disconnet = requireActivity().findViewById(R.id.disconnect_button);
                                            stopPeriodicFetching();

                                            disconnet.setText("connect");
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(requireActivity(), "연동된 카트 해제 실패", Toast.LENGTH_LONG).show();
                                    loadCartItems(cartId);
                                }
                            });

                        } else {
                            Toast.makeText(requireActivity(), "카트 안을 비워 주세요", Toast.LENGTH_LONG).show();
                            loadCartItems(cartId);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // 에러 처리
            }
        });

        requestQueue.add(stringRequest);
    }

    private void checkAndRequestCartId(String userId) {
        Log.d(TAG, "id in requset: " + userId);
        cartListRef.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    // 사용자 cart_id가 존재할 때
                    String cartId = dataSnapshot.child("cart_id").getValue(String.class);
                    loadCartItems(cartId);
                } else {
                    // cart_id가 존재하지 않을 때 입력 창을 띄웁니다.
                    //requestCartId(userId);
                }
            } else {
                // Firebase에서 오류가 발생했을 때 처리
            }
        });
    }

    private void checkAndRequestCartId1(String userId) {
        Log.d(TAG, "id in requset: " + userId);
        cartListRef.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    // 사용자 cart_id가 존재할 때
                    String cartId = dataSnapshot.child("cart_id").getValue(String.class);
                    loadCartItems(cartId);
                } else {
                    // cart_id가 존재하지 않을 때 입력 창을 띄웁니다.
                    requestCartId(userId);
                }
            } else {
                // Firebase에서 오류가 발생했을 때 처리
            }
        });
    }


    private void requestCartId(String userId) {
        Intent intent = new Intent(requireActivity(), BarcodeScannerActivity.class);
        qrCodeLauncher.launch(intent);
    }

    private void handleQrResult(Intent data) {
        String cartId = data.getStringExtra("cart_id");
        cartListCheckRef.child(cartId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    Toast.makeText(requireActivity(), "해당 카트는 사용자가 존재합니다.", Toast.LENGTH_LONG).show();
                } else {
                    cartListRef.child(userId).child("cart_id").setValue(cartId);
                    cartListCheckRef.child(cartId).setValue(userId);
                    Toast.makeText(requireActivity(), "카트 " + cartId + "번 연결", Toast.LENGTH_LONG).show();
                    Button button1 = requireActivity().findViewById(R.id.disconnect_button);
                    button1.setText("disconnect");
                    loadCartItems(cartId);
                }
            }
        });
    }

    private void loadCartItems(String cartId) {
        // cart_id를 사용하여 데이터를 가져옵니다.
        // 기존의 Volley를 사용한 코드와 통합
        startPeriodicFetching(cartId);
    }

    private void sendHttpRequest(String cartID) {
        // Fragment가 Activity에 연결된 상태인지 확인
        if (!isAdded()) {
            Log.w(TAG, "Fragment not attached to context. Aborting request.");
            return;
        }

        // `RequestQueue`가 `null`인지 확인하고, `null`일 경우 초기화
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(requireContext());
        }

        // 요청할 URL
        String url = "http://3.35.9.191/test2.php?username=app&password=app2024&cart_id=" + cartID;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<CartItem> newCartItems = parseJson(response, cartID);
                        updateCartItems(newCartItems);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 에러 처리
                    }
                });

        requestQueue.add(stringRequest);
    }

    private List<CartItem> parseJson(String json, String inCartId) {
        List<CartItem> cartItems = new ArrayList<>();
        if (!isAdded()) {
            Log.w(TAG, "Fragment not attached to context. Aborting request.");
            return cartItems;
        }

        int temp = 0;
        int total_cost = 0;
        try {
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String cartId = jsonObject.getString("cart_id");
                String itemId = jsonObject.getString("name");
                String itemValue = jsonObject.getString("item_value");
                String count = jsonObject.getString("count");
                String price = jsonObject.getString("price");
                Log.e(TAG, "name confirm : " + itemId);
                if (cartId.equals(inCartId)) {
                    if (price != null && !price.isEmpty()) {
                        int priceInt = Integer.parseInt(price);
                        int countInt=Integer.parseInt(count);
                        CartItem cartItem = new CartItem(cartId, itemId, itemValue, count, String.valueOf(priceInt));
                        cartItems.add(cartItem);
                        total_cost += priceInt*countInt;
                        temp+=countInt;
                    }
                }
            }

            if (isAdded()) { // Fragment가 Activity에 연결되었는지 확인
                TextView listCount = requireActivity().findViewById(R.id.list_count_input);
                TextView totalCostInput = requireActivity().findViewById(R.id.cost_input);
                totalCostInput.setText(String.valueOf(total_cost));
                listCount.setText(String.valueOf(temp)); // 문자열로 변환
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return cartItems;
    }

    private void updateCartItems(List<CartItem> newCartItems) {
        List<CartItem> itemsToAdd = new ArrayList<>();
        List<CartItem> itemsToRemove = new ArrayList<>();

        if (newCartItems.isEmpty()) {
            TextView listCount = requireActivity().findViewById(R.id.list_count_input);
            TextView totalCostInput = requireActivity().findViewById(R.id.cost_input);
            totalCostInput.setText("0");
            listCount.setText("0");
        }

        // 기존 데이터를 새 데이터와 비교하여 없는 경우에만 제거
        for (CartItem currentItem : currentCartItems) {
            boolean exists = false;
            for (CartItem newItem : newCartItems) {
                if (currentItem.getItemId().equals(newItem.getItemId())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                itemsToRemove.add(currentItem);
            }
        }

        // 새 데이터를 기존 데이터와 비교하여 없는 경우에만 추가하고, 있는 경우 count 업데이트
        for (CartItem newItem : newCartItems) {
            boolean exists = false;
            for (CartItem currentItem : currentCartItems) {
                if (currentItem.getItemId().equals(newItem.getItemId())) {
                    exists = true;
                    // count가 다를 경우 업데이트
                    if (currentItem.getCount() != newItem.getCount()) {
                        currentItem.setCount(newItem.getCount());
                    }
                    break;
                }
            }
            if (!exists) {
                itemsToAdd.add(newItem);
            }
        }

        // 기존 리스트에서 제거할 아이템들을 삭제
        currentCartItems.removeAll(itemsToRemove);

        // 새로운 아이템이 있을 경우 추가
        currentCartItems.addAll(itemsToAdd);

        // 어댑터 갱신
        if (adapter == null) {
            adapter = new CartItemAdapter(currentCartItems);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        stopPeriodicFetching(); // `Fragment`가 일시정지 상태로 갈 때 주기적 작업 중단
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPeriodicFetching(); // `Fragment`가 파괴될 때 주기적 작업 중단
    }
}
