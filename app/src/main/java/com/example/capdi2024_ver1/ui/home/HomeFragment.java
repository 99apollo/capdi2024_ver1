package com.example.capdi2024_ver1.ui.home;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.capdi2024_ver1.R;
import com.example.capdi2024_ver1.admin_ui.dashboard.ItemData;
import com.example.capdi2024_ver1.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private List<ItemData> itemDataList = new ArrayList<>();
    private List<SalesData> salesDataList = new ArrayList<>();
    private RequestQueue requestQueue;
    private ProductAdapter productAdapter;
    private SalesDataAdapter salesDataAdapter;

    private String url = "http://3.35.9.191/test5.php";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize RecyclerView for products
        RecyclerView recyclerViewProducts = binding.recyclerViewProducts;
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        productAdapter = new ProductAdapter(itemDataList);
        recyclerViewProducts.setAdapter(productAdapter);

        // Initialize RecyclerView for sales data
        RecyclerView recyclerViewSales = binding.recyclerViewRecomand;
        recyclerViewSales.setLayoutManager(new LinearLayoutManager(getContext()));
        salesDataAdapter = new SalesDataAdapter(salesDataList);
        recyclerViewSales.setAdapter(salesDataAdapter);

        // Initialize buttons and set click listeners
        Button buttonCategory1 = binding.buttonCategory1;
        Button buttonCategory2 = binding.buttonCategory2;
        Button buttonCategory3 = binding.buttonCategory3;
        Button buttonCategory4 = binding.buttonCategory4;
        Button buttonCategory00 = binding.buttonCategory00;
        Button buttonCategory10 = binding.buttonCategory10;
        Button buttonCategory20 = binding.buttonCategory20;
        Button buttonCategory30 = binding.buttonCategory30;
        Button buttonCategory40 = binding.buttonCategory40;

        buttonCategory1.setOnClickListener(v -> fetchItemDataByCategory("신선식품"));
        buttonCategory2.setOnClickListener(v -> fetchItemDataByCategory("냉동식품"));
        buttonCategory3.setOnClickListener(v -> fetchItemDataByCategory("가공식품"));
        buttonCategory4.setOnClickListener(v -> fetchItemDataByCategory("기타"));
        buttonCategory00.setOnClickListener(v -> fetchSalesData(""));
        buttonCategory10.setOnClickListener(v -> fetchSalesData("신선식품"));
        buttonCategory20.setOnClickListener(v -> fetchSalesData("냉동식품"));
        buttonCategory30.setOnClickListener(v -> fetchSalesData("가공식품"));
        buttonCategory40.setOnClickListener(v -> fetchSalesData("기타"));

        // Initialize SearchView
        SearchView searchView = binding.searchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterItemData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterItemData(newText);
                return true;
            }
        });

        // Initialize Volley request queue
        requestQueue = Volley.newRequestQueue(requireContext());
        fetchTopSellingProduct(); // Add this line to fetch and log top selling product
        return root;
    }

    private void fetchTopSellingProduct() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            String topSellingProductName = "";
                            int maxSales = 0;

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String name = jsonObject.getString("name");
                                int salesPerMonth = jsonObject.getInt("salespermonth");

                                if (salesPerMonth > maxSales) {
                                    maxSales = salesPerMonth;
                                    topSellingProductName = name;
                                }
                            }

                            // Set the top selling product to the banner
                            if (binding != null) {
                                TextView recommendedBanner = binding.textRecommendedBanner;
                                recommendedBanner.setText("오늘 가장 많이 팔린 제품: " + topSellingProductName);
                                recommendedBanner.setVisibility(View.VISIBLE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error parsing JSON data", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching data", error);
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    private void fetchItemDataByCategory(String category) {
        String url = "http://3.35.9.191/home_search.php?category=" + category;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("HomeFragment", "Category Response: " + response);
                        itemDataList = parseItemDataJson(response);
                        updateRecyclerView(itemDataList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("HomeFragment", "Category Error: " + error.getMessage());
                        Toast.makeText(requireContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                });

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(requireContext());
        }
        requestQueue.add(stringRequest);
    }

    private void fetchSalesData(String category) {
        String url;
        if (category == null || category.isEmpty()) {
            url = "http://3.35.9.191/sales.php"; // 전체 데이터를 가져오는 URL
        } else {
            url = "http://3.35.9.191/sales.php?category=" + category; // 특정 카테고리의 데이터를 가져오는 URL
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("HomeFragment", "Server Response: " + response);
                        salesDataList = parseSalesDataJson(response);
                        updateRecyclerViewWithSalesData(salesDataList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("HomeFragment", "Server Error: " + error.getMessage());
                        Toast.makeText(requireContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                });

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(requireContext());
        }
        requestQueue.add(stringRequest);
    }

    private List<ItemData> parseItemDataJson(String response) {
        List<ItemData> itemList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String itemValue = jsonObject.getString("item_value");
                String category = jsonObject.getString("categori");
                String name = jsonObject.getString("name");
                String manufacturer = jsonObject.getString("manufacturer");
                double price = jsonObject.getDouble("price");
                int amount = jsonObject.getInt("amount");
                String location = jsonObject.getString("location");

                ItemData itemData = new ItemData(itemValue, category, name, manufacturer, price, amount, location);
                itemList.add(itemData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return itemList;
    }

    private List<SalesData> parseSalesDataJson(String response) {
        List<SalesData> salesList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                double price = jsonObject.getDouble("price");
                int salesPerDay = jsonObject.getInt("salesperday");
                int salesPerWeek = jsonObject.getInt("salesperweek");
                int salesPerMonth = jsonObject.getInt("salespermonth");
                String category = jsonObject.getString("categori");

                SalesData salesData = new SalesData(name, price, salesPerDay, salesPerWeek, salesPerMonth, category);
                salesList.add(salesData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return salesList;
    }

    private void updateRecyclerView(List<ItemData> itemList) {
        productAdapter.setItems(itemList);
    }

    private void updateRecyclerViewWithSalesData(List<SalesData> salesList) {
        salesDataAdapter.setSalesList(salesList);
    }

    private void filterItemData(String query) {
        List<ItemData> filteredList = new ArrayList<>();
        for (ItemData item : itemDataList) {
            if (item.getName().toLowerCase().contains(query.toLowerCase())
                    || item.getManufacturer().toLowerCase().contains(query.toLowerCase())
                    || item.getCategory().equalsIgnoreCase(query)) {
                filteredList.add(item);
            }
        }
        updateRecyclerView(filteredList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
