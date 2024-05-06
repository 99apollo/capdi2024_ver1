package com.example.capdi2024_ver1.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.example.capdi2024_ver1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private CartItemAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        requestQueue = Volley.newRequestQueue(requireContext());

        recyclerView = root.findViewById(R.id.cart_item_listview);  // RecyclerView ID
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CartItemAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        sendHttpRequest();  // 데이터 가져오기

        return root;
    }

    private void sendHttpRequest() {
        String url = "http://3.35.9.191/connect.php?username=app&password=app2024";  // 요청할 URL

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<CartItem> cartItems = parseJson(response);
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

    private List<CartItem> parseJson(String json) {
        List<CartItem> cartItems = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String cartId = jsonObject.getString("cartid");
                String itemId = jsonObject.getString("item_id");
                String itemValue = jsonObject.getString("item_value");

                CartItem cartItem = new CartItem(cartId, itemId, itemValue);
                cartItems.add(cartItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cartItems;
    }
}
