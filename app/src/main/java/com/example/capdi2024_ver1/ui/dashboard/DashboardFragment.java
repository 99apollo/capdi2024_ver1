package com.example.capdi2024_ver1.ui.dashboard;


import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        sharedViewModel.getUserId().observe(getViewLifecycleOwner(), new Observer<String>() {  // `LiveData` 관찰
            @Override
            public void onChanged(String newUserId) {
                userId=newUserId;

                Log.d(TAG, "idin: " + userId);
                checkAndRequestCartId(userId);
            }
        });


        return root;
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
            cartListRef.child(userId).child("cart_id").setValue(cartId);
            loadCartItems(cartId);
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
