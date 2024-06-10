package com.example.capdi2024_ver1.ui.home;

import static android.content.ContentValues.TAG;

import android.content.Intent;
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

        Button buttonCategory1 = binding.buttonCategory1;
        Button buttonCategory2 = binding.buttonCategory2;
        Button buttonCategory3 = binding.buttonCategory3;
        Button buttonCategory4 = binding.buttonCategory4;
        Button buttonCategory00 = binding.buttonCategory00;
        Button buttonCategory10 = binding.buttonCategory10;
        Button buttonCategory20 = binding.buttonCategory20;
        Button buttonCategory30 = binding.buttonCategory30;
        Button buttonCategory40 = binding.buttonCategory40;


        buttonCategory1.setOnClickListener(v -> {
            fetchItemDataByCategory("신선식품");
            toggleRecyclerViewProductsVisibility();
        });

        buttonCategory2.setOnClickListener(v -> {
            fetchItemDataByCategory("유제품");
            toggleRecyclerViewProductsVisibility();
        });

        buttonCategory3.setOnClickListener(v -> {
            fetchItemDataByCategory("가공식품");
            toggleRecyclerViewProductsVisibility();
        });

        buttonCategory4.setOnClickListener(v -> {
            fetchItemDataByCategory("음료");
            toggleRecyclerViewProductsVisibility();
        });

        buttonCategory00.setOnClickListener(v -> {
            fetchSalesData("");
            toggleRecyclerViewSalesVisibility();
        });

        buttonCategory10.setOnClickListener(v -> {
            fetchSalesData("신선식품");
            toggleRecyclerViewSalesVisibility();
        });

        buttonCategory20.setOnClickListener(v -> {
            fetchSalesData("유제품");
            toggleRecyclerViewSalesVisibility();
        });

        buttonCategory30.setOnClickListener(v -> {
            fetchSalesData("가공식품");
            toggleRecyclerViewSalesVisibility();
        });

        buttonCategory40.setOnClickListener(v -> {
            fetchSalesData("음료");
            toggleRecyclerViewSalesVisibility();
        });


        SearchView searchView = binding.searchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("HomeFragment", "onQueryTextSubmit: " + query);
                if (!query.trim().isEmpty()) {
                    fetchItemDataByCategory("");
                    // Filter data based on the search query
                    List<ItemData> filteredList = filterItemData(query);
                    // Start SearchResultsActivity and pass the filtered data
                    Intent intent = new Intent(requireContext(), SearchResultsActivity.class);
                    intent.putParcelableArrayListExtra("search_results", new ArrayList<>(filteredList));
                    startActivity(intent);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("HomeFragment", "onQueryTextChange: " + newText);
                if (!newText.trim().isEmpty()) {
                    filterItemData(newText);
                } else {
                    // 텍스트가 비어있을 경우 전체 목록을 보여주거나 다른 처리를 합니다.
                    productAdapter.setItems(itemDataList);
                    productAdapter.notifyDataSetChanged();
                }
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
        String url;
        if (category == null || category.isEmpty()) {
            url = "http://3.35.9.191/home_search.php"; // 전체 데이터를 가져오는 URL
        } else {
            url ="http://3.35.9.191/home_search.php?category=" + category;// 특정 카테고리의 데이터를 가져오는 URL
        }

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

                // item_value의 8번째 문자를 가져와서 이미지 리소스 문자열을 설정합니다.
                String imageResource = "";
                if (!itemValue.isEmpty() && itemValue.length() >= 8) {
                    char identifier = itemValue.charAt(7);
                    imageResource = "u" + identifier;
                }

                ItemData itemData = new ItemData(itemValue, category, name, manufacturer, price, amount, location, imageResource);
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
                String price = String.valueOf(jsonObject.getDouble("price"));
                String salesPerDay = String.valueOf(jsonObject.getInt("salesperday"));
                String salesPerWeek = String.valueOf(jsonObject.getInt("salesperweek"));
                String salesPerMonth = String.valueOf(jsonObject.getInt("salespermonth"));
                String category = jsonObject.getString("categori");
                String itemValue = jsonObject.optString("item_value", "");

                // item_value의 8번째 문자를 가져와서 이미지 리소스 문자열을 설정합니다.
                String imageResource = "";
                if (!itemValue.isEmpty() && itemValue.length() >= 8) {
                    char identifier = itemValue.charAt(7);
                    imageResource = "u" + identifier;
                }

                SalesData salesData = new SalesData(name, price, salesPerDay, salesPerWeek, salesPerMonth, category, itemValue, imageResource);
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

    private void handleSearchQuery(String query) {
        // 검색 결과를 처리하는 로직을 여기에 작성합니다.
        List<ItemData> filteredList = filterItemData(query);
        // 다음 화면으로 이동하는 코드를 여기에 추가합니다. (예: SearchResultsActivity를 시작하는 코드)
        Intent intent = new Intent(requireContext(), SearchResultsActivity.class);
        intent.putParcelableArrayListExtra("search_results", new ArrayList<>(filteredList));
        startActivity(intent);
    }

    private List<ItemData> filterItemData(String query) {
        List<ItemData> filteredList = new ArrayList<>();
        for (ItemData item : itemDataList) {
            if (item.getName().toLowerCase().contains(query.toLowerCase())
                    || item.getManufacturer().toLowerCase().contains(query.toLowerCase())
                    || item.getCategory().equalsIgnoreCase(query)) {
                filteredList.add(item);
                Log.d("filterItemData", "Filtered item added: " + item.getName());
            }
        }
        return filteredList;

    }

    private void toggleRecyclerViewProductsVisibility() {
        RecyclerView recyclerViewProducts = binding.recyclerViewProducts;

        if (recyclerViewProducts.getVisibility() == View.VISIBLE) {
            recyclerViewProducts.setVisibility(View.GONE);
        } else {
            recyclerViewProducts.setVisibility(View.VISIBLE);
        }
    }

    private void toggleRecyclerViewSalesVisibility() {
        RecyclerView recyclerViewSales = binding.recyclerViewRecomand;

        if (recyclerViewSales.getVisibility() == View.VISIBLE) {
            recyclerViewSales.setVisibility(View.GONE);
        } else {
            recyclerViewSales.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
