package com.example.capdi2024_ver1.admin_ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.capdi2024_ver1.R;
import com.example.capdi2024_ver1.databinding.FragmentAdminDashboardBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardFragment extends Fragment {

    private FragmentAdminDashboardBinding binding;
    private RequestQueue requestQueue;
    private List<ItemData> itemDataList = new ArrayList<>();
    private ItemAdapter itemAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get references to views
        RadioGroup radioGroup = binding.radioGroup;
        ChipGroup chipGroup = binding.chipGroup;

        // Set visibility to GONE initially
        chipGroup.setVisibility(View.GONE);

        // Setup RecyclerView
        binding.adminDashboardRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        itemAdapter = new ItemAdapter(new ArrayList<>());
        binding.adminDashboardRecyclerView.setAdapter(itemAdapter);

        // Set listener for RadioGroup
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButton1) {
                chipGroup.setVisibility(View.VISIBLE); // Show chip group when option 1 is selected
                fetchItemData(); // Option 1이 선택될 때 데이터 가져오기
            } else {
                chipGroup.setVisibility(View.GONE); // Hide when other options are selected
            }
        });

        // Set onClickListeners for each chip
        binding.chip1.setOnClickListener(v -> filterItemsByCategory("신선식품"));
        binding.chip2.setOnClickListener(v -> filterItemsByCategory("냉동식품"));
        binding.chip3.setOnClickListener(v -> filterItemsByCategory("가공식품"));
        binding.chip4.setOnClickListener(v -> filterItemsByCategory("기타"));

        return root;
    }

    private void fetchItemData() {
        String url = "http://3.35.9.191/test4.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("AdminDashboard", "Response: " + response);
                        itemDataList = parseItemDataJson(response);
                        // Filter items for default category if needed
                        filterItemsByCategory("신선식품");  // 첫 번째 chip 선택 시 데이터를 표시하도록 설정
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("AdminDashboard", "Error: " + error.getMessage());
                        Toast.makeText(requireContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                });

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(requireContext());
        }
        requestQueue.add(stringRequest);
    }

    private List<ItemData> parseItemDataJson(String json) {
        List<ItemData> itemDataList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ItemData itemData = new ItemData();
                itemData.setItem_value(jsonObject.getString("item_value"));
                itemData.setCategori(jsonObject.getString("categori"));
                itemData.setName(jsonObject.getString("name"));
                itemData.setManufacturer(jsonObject.getString("manufacturer"));
                itemData.setPrice(jsonObject.getDouble("price"));
                itemData.setAmount(jsonObject.getInt("amount"));
                itemDataList.add(itemData);
            }
        } catch (JSONException e) {
            Log.e("AdminDashboard", "JSON Parsing error: " + e.getMessage());
        }
        return itemDataList;
    }

    private void filterItemsByCategory(String category) {
        List<ItemData> filteredList = new ArrayList<>();
        for (ItemData item : itemDataList) {
            if (item.getCategori().equals(category)) {
                filteredList.add(item);
            }
        }
        itemAdapter.updateItemList(filteredList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
