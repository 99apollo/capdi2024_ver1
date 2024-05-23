package com.example.capdi2024_ver1.admin_ui.home;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.capdi2024_ver1.R;
import com.example.capdi2024_ver1.admin_ui.home.AdminHomeViewModel;
import com.example.capdi2024_ver1.databinding.FragmentAdminHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdminHomeFragment extends Fragment {

    private FragmentAdminHomeBinding binding;
    private RequestQueue requestQueue;
    private String url = "http://3.35.9.191/test5.php"; // PHP 스크립트 URL

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AdminHomeViewModel homeViewModel =
                new ViewModelProvider(this).get(AdminHomeViewModel.class);

        binding = FragmentAdminHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        requestQueue = Volley.newRequestQueue(requireContext());
        fetchDataFromServer(); // 데이터를 가져와서 TextView에 설정하기 위해 fetchDataFromServer() 메서드 호출

        return root;
    }

    private void fetchDataFromServer() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            TableLayout tableLayout = requireView().findViewById(R.id.tableLayout);
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String name = jsonObject.getString("name");
                                String price = jsonObject.getString("price");
                                String salesPerDay = jsonObject.getString("salesperday");
                                String salesPerWeek = jsonObject.getString("salesperweek");
                                String salesPerMonth = jsonObject.getString("salespermonth");
                                String categori = jsonObject.getString("categori");

                                TableRow row = new TableRow(requireContext());

                                TextView nameTextView = new TextView(requireContext());
                                nameTextView.setText(name);
                                nameTextView.setGravity(Gravity.CENTER);
                                row.addView(nameTextView);

                                TextView priceTextView = new TextView(requireContext());
                                priceTextView.setText(price);
                                priceTextView.setGravity(Gravity.CENTER);
                                row.addView(priceTextView);

                                TextView salesPerDayTextView = new TextView(requireContext());
                                salesPerDayTextView.setText(salesPerDay);
                                salesPerDayTextView.setGravity(Gravity.CENTER);
                                row.addView(salesPerDayTextView);

                                TextView salesPerWeekTextView = new TextView(requireContext());
                                salesPerWeekTextView.setText(salesPerWeek);
                                salesPerWeekTextView.setGravity(Gravity.CENTER);
                                row.addView(salesPerWeekTextView);

                                TextView salesPerMonthTextView = new TextView(requireContext());
                                salesPerMonthTextView.setText(salesPerMonth);
                                salesPerMonthTextView.setGravity(Gravity.CENTER);
                                row.addView(salesPerMonthTextView);

                                TextView categoryTextView = new TextView(requireContext());
                                categoryTextView.setText(categori);
                                categoryTextView.setGravity(Gravity.CENTER);
                                row.addView(categoryTextView);

                                tableLayout.addView(row);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
