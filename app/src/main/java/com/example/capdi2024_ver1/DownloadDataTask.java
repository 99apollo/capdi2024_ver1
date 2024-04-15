package com.example.capdi2024_ver1;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DownloadDataTask {

    private static final Executor executor = Executors.newSingleThreadExecutor();

    public static void downloadData(String url, OnDataDownloadedListener listener) {
        executor.execute(() -> {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String resultJson = null;
            try {
                URL dataUrl = new URL(url);
                urlConnection = (HttpURLConnection) dataUrl.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                if (buffer.length() == 0) {
                    return;
                }
                resultJson = buffer.toString();
            } catch (IOException e) {
                Log.e("DownloadDataTask", "Error ", e);
                return;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("DownloadDataTask", "Error closing stream", e);
                    }
                }
            }
            if (resultJson != null) {
                try {
                    JSONArray jsonArray = new JSONArray(resultJson);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String cartId = jsonObject.getString("cartid");
                        String itemId = jsonObject.getString("item_id");
                        String itemValue = jsonObject.getString("item_value");
                        // 여기서 데이터를 사용하거나 저장하면 됩니다.
                    }
                    if (listener != null) {
                        listener.onDataDownloaded(resultJson);
                    }
                } catch (JSONException e) {
                    Log.e("DownloadDataTask", "Error parsing JSON", e);
                }
            }
        });
    }

    public interface OnDataDownloadedListener {
        void onDataDownloaded(String resultJson);
    }
}
