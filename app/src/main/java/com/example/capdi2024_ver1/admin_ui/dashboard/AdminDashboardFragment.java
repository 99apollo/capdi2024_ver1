package com.example.capdi2024_ver1.admin_ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    private List<CartCheckItem> cartCheckItemList = new ArrayList<>();
    private CartCheckItemAdapter cartCheckItemAdapter;
    private DatabaseReference cartCheckListRef;
    private TableLayout tableLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        cartCheckListRef = FirebaseDatabase.getInstance().getReference("cart_check_list");

        // Get references to views
        RadioGroup radioGroup = binding.radioGroup;
        ChipGroup chipGroup = binding.chipGroup;
        tableLayout = binding.tableLayout;

        // Set visibility to GONE initially
        chipGroup.setVisibility(View.GONE);
        tableLayout.setVisibility(View.GONE);

        // Setup RecyclerView
        binding.adminDashboardRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        itemAdapter = new ItemAdapter(new ArrayList<>());
        cartCheckItemAdapter = new CartCheckItemAdapter(new ArrayList<>());
        binding.adminDashboardRecyclerView.setAdapter(itemAdapter);

        // Set listener for RadioGroup
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButton1) {
                chipGroup.setVisibility(View.VISIBLE); // Show chip group when option 1 is selected
                tableLayout.setVisibility(View.GONE);
                binding.adminDashboardRecyclerView.setAdapter(itemAdapter);
                fetchItemData(); // Option 1이 선택될 때 데이터 가져오기
            } else if (checkedId == R.id.radioButton2) {
                chipGroup.setVisibility(View.GONE); // Hide chip group when option 2 is selected
                tableLayout.setVisibility(View.VISIBLE);
                binding.adminDashboardRecyclerView.setAdapter(cartCheckItemAdapter); // Use cart check item adapter
                fetchCartCheckList(); // Option 2이 선택될 때 카트 체크 리스트 가져오기
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

    private List<ItemData> parseItemDataJson(String response) {
        List<ItemData> itemList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String item_value = jsonObject.getString("item_value");
                String categori = jsonObject.getString("categori");
                String name = jsonObject.getString("name");
                String manufacturer = jsonObject.getString("manufacturer");
                double price = jsonObject.getDouble("price");
                int amount = jsonObject.getInt("amount");
                itemList.add(new ItemData(item_value, categori, name, manufacturer, price, amount));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return itemList;
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

    private void fetchCartCheckList() {
        cartCheckListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cartCheckItemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String cartId = snapshot.getKey();
                    String userId = snapshot.getValue(String.class);

                    // Check if the userId already exists in the list
                    boolean isNewUserId = true;
                    for (CartCheckItem item : cartCheckItemList) {
                        if (item.getUserId().equals(userId)) {
                            isNewUserId = false;
                            break;
                        }
                    }

                    // Add only if it's a new userId
                    if (isNewUserId) {
                        cartCheckItemList.add(new CartCheckItem(cartId, userId));
                    }
                }
                cartCheckItemAdapter.updateItemList(cartCheckItemList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("AdminDashboard", "Error fetching cart check list: " + databaseError.getMessage());
                Toast.makeText(requireContext(), "Failed to fetch cart check list", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void addDataToTableLayout(List<CartCheckItem> cartCheckItemList) {
        if (tableLayout == null) {
            return;
        }

        tableLayout.removeAllViews(); // 이전 데이터를 지우기 위해 테이블 초기화

        // 헤더 행 추가
        TableRow headerRow = new TableRow(requireContext());
        TextView cartIdHeader = new TextView(requireContext());
        cartIdHeader.setText("Cart ID");
        cartIdHeader.setTypeface(null, android.graphics.Typeface.BOLD);
        headerRow.addView(cartIdHeader);

        TextView userIdHeader = new TextView(requireContext());
        userIdHeader.setText("User ID");
        userIdHeader.setTypeface(null, android.graphics.Typeface.BOLD);
        headerRow.addView(userIdHeader);

        tableLayout.addView(headerRow);

        // 데이터 행 추가
        for (CartCheckItem cartCheckItem : cartCheckItemList) {
            TableRow row = new TableRow(requireContext());

            TextView cartIdTextView = new TextView(requireContext());
            cartIdTextView.setText(cartCheckItem.getCartId());
            row.addView(cartIdTextView);

            TextView userIdTextView = new TextView(requireContext());
            userIdTextView.setText(cartCheckItem.getUserId());
            row.addView(userIdTextView);

            tableLayout.addView(row);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
