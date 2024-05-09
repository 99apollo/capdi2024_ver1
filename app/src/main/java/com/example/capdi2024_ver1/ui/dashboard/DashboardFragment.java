package com.example.capdi2024_ver1.ui.dashboard;


import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        recyclerView = root.findViewById(R.id.cart_item_listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cartListRef = FirebaseDatabase.getInstance().getReference("cart_list");
        cartListCheckRef = FirebaseDatabase.getInstance().getReference("cart_check_list");
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        Button disconnet_Button = requireActivity().findViewById(R.id.disconnect_button);
        disconnet_Button.setVisibility(View.VISIBLE);

        sharedViewModel.getUserId().observe(getViewLifecycleOwner(), new Observer<String>() {  // `LiveData` 관찰
            @Override
            public void onChanged(String newUserId) {
                userId=newUserId;
                checkCartID(userId,disconnet_Button);
                Log.d(TAG, "idin: " + userId);
                checkAndRequestCartId(userId);
            }
        });


        disconnet_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(disconnet_Button.getText().equals("disconnet")){
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
                }else{
                    checkAndRequestCartId1(userId);
                }

            }
        });
        return root;
    }
    private void checkCartID(String userId,Button button) {
        Log.d(TAG, "id in requset: " + userId);
        cartListRef.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    // 사용자 cart_id가 존재할 때
                    button.setText("disconnet");

                } else {
                    button.setText("connet");
                }
            } else {
                // Firebase에서 오류가 발생했을 때 처리
            }
        });
    }
    private void releaseCart(String cartId){
        // `RequestQueue`가 `null`인지 확인하고, `null`일 경우 초기화
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(requireContext());
        }
        String url = "http://3.35.9.191/connect.php?username=app&password=app2024";  // 요청할 URL

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<CartItem> cartItems = parseJson(response,cartId);
                        Log.e(TAG, "data : " + cartItems);
                        if(cartItems.isEmpty()) {
                            cartListRef.child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    cartListCheckRef.child(cartId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(requireActivity(), "연결된 카트가 해제되었습니다.", Toast.LENGTH_LONG).show();
                                            Button disconnet = requireActivity().findViewById(R.id.disconnect_button);
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

                        }else{
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
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Enter Cart ID");

        final EditText input = new EditText(getContext());
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String cartId = input.getText().toString();
            cartListCheckRef.child(cartId).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        // cart 사용자 존재시
                        Toast.makeText(requireActivity(), "해당 카트는 사용자가 존재합니다.", Toast.LENGTH_LONG).show();

                    } else {
                        // cart 사용자 없을시
                        cartListRef.child(userId).child("cart_id").setValue(cartId);
                        cartListCheckRef.child(cartId).setValue(userId);
                        Button button1 = requireActivity().findViewById(R.id.disconnect_button);
                        button1.setText("disconnet");
                        loadCartItems(cartId);

                    }
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }

    private void loadCartItems(String cartId) {
        // cart_id를 사용하여 데이터를 가져옵니다.
        // 기존의 Volley를 사용한 코드와 통합
        sendHttpRequest(cartId);
    }

    private void sendHttpRequest(String cartID) {
        // `RequestQueue`가 `null`인지 확인하고, `null`일 경우 초기화
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(requireContext());
        }
        String url = "http://3.35.9.191/connect.php?username=app&password=app2024";  // 요청할 URL

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<CartItem> cartItems = parseJson(response,cartID);
                        adapter = new CartItemAdapter(cartItems);  // Adapter 업데이트
                        recyclerView.setAdapter(adapter);  // RecyclerView에 Adapter 설정
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // 에러 처리
            }
        });

        requestQueue.add(stringRequest);
    }

    private List<CartItem> parseJson(String json,String inCartId) {
        List<CartItem> cartItems = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String cartId = jsonObject.getString("cartid");
                String itemId = jsonObject.getString("item_id");
                String itemValue = jsonObject.getString("item_value");
                if(cartId.equals(inCartId)){
                    CartItem cartItem = new CartItem(cartId, itemId, itemValue);
                    cartItems.add(cartItem);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cartItems;
    }
}
