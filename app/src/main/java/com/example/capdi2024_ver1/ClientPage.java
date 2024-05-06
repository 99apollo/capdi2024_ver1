package com.example.capdi2024_ver1;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

public class ClientPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_page);

        // HTTP 요청을 보낼 URL
        String url = "http://3.35.9.191/connect.php?username=app&password=app2024";

        // Volley 요청 큐 생성
        RequestQueue queue = Volley.newRequestQueue(this);

        // StringRequest 생성
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 응답 데이터를 TextView에 표시
                        displayDownloadedData(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // 에러 처리
            }
        });

        // 요청을 큐에 추가
        queue.add(stringRequest);
    }

    // 다운로드한 데이터를 TextView에 표시하는 메서드
    private void displayDownloadedData(String resultJson) {
        try {
            // JSONArray로 변환
            JSONArray jsonArray = new JSONArray(resultJson);

            // TextView에 데이터 표시
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String cartId = jsonObject.getString("cartid");
                String itemId = jsonObject.getString("item_id");
                String itemValue = jsonObject.getString("item_value");

                // 데이터를 TextView에 추가
                stringBuilder.append("cartId: ").append(cartId).append("\n")
                        .append("itemId: ").append(itemId).append("\n")
                        .append("itemValue: ").append(itemValue).append("\n\n");
            }

            // TextView를 찾아서 데이터를 설정
            TextView textView = findViewById(R.id.textView);
            textView.setText(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
