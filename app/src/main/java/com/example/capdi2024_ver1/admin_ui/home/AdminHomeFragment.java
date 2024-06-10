package com.example.capdi2024_ver1.admin_ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.capdi2024_ver1.databinding.FragmentAdminHomeBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminHomeFragment extends Fragment {

    private FragmentAdminHomeBinding binding;
    private RequestQueue requestQueue;
    private String url = "http://3.35.9.191/test5.php"; // PHP 스크립트 URL
    private BarChart barChart;
    private ChipGroup chipGroupXAxis;
    private ChipGroup chipGroupPeriod;

    // 전역 변수로 ArrayList를 선언합니다.
    // 카테고리와 프로덕트에 대한 데이터를 구분하여 저장합니다.
    private ArrayList<BarEntry> categorySalesPerDayEntries;
    private ArrayList<BarEntry> categorySalesPerWeekEntries;
    private ArrayList<BarEntry> categorySalesPerMonthEntries;
    private ArrayList<BarEntry> productSalesPerDayEntries;
    private ArrayList<BarEntry> productSalesPerWeekEntries;
    private ArrayList<BarEntry> productSalesPerMonthEntries;

    // 클래스 멤버 변수 선언
    private HashMap<String, ArrayList<Float>> categorySalesMap;
    private HashMap<String, ArrayList<Float>> productSalesMap;

    // 카테고리와 프로덕트 라벨을 관리하는 리스트 선언
    private ArrayList<String> categoryLabels;
    private ArrayList<String> productLabels;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AdminHomeViewModel homeViewModel =
                new ViewModelProvider(this).get(AdminHomeViewModel.class);

        binding = FragmentAdminHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        barChart = root.findViewById(R.id.barChart);
        barChart.setDrawGridBackground(false);
        chipGroupXAxis = root.findViewById(R.id.chipGroupXAxis);
        chipGroupPeriod = root.findViewById(R.id.chipGroupPeriod);
        requestQueue = Volley.newRequestQueue(requireContext());

        chipGroupXAxis.setSingleSelection(true);
        chipGroupPeriod.setSingleSelection(true);

        // ChipGroup의 OnCheckedChangeListener 설정
        chipGroupXAxis.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (checkedId == -1) {
                    // X축 Chip이 선택 해제되었을 때
                    barChart.clear();
                    barChart.invalidate();
                } else {
                    Log.d("ChipGroupXAxis", "onCheckedChanged: Chip selected");
                    updateChart();
                }
            }
        });

        chipGroupPeriod.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (checkedId == -1) {
                    // 기간 Chip이 선택 해제되었을 때
                    barChart.clear();
                    barChart.invalidate();
                } else {
                    Log.d("ChipGroupPeriod", "onCheckedChanged: Chip selected");
                    updateChart();
                }
            }
        });

        fetchDataFromServer(); // 초기 데이터 가져오기

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
                            // 데이터 가져와서 저장
                            categorySalesPerDayEntries = new ArrayList<>();
                            categorySalesPerWeekEntries = new ArrayList<>();
                            categorySalesPerMonthEntries = new ArrayList<>();
                            productSalesPerDayEntries = new ArrayList<>();
                            productSalesPerWeekEntries = new ArrayList<>();
                            productSalesPerMonthEntries = new ArrayList<>();

                            categorySalesMap = new HashMap<>();
                            productSalesMap = new HashMap<>();
                            categoryLabels = new ArrayList<>();
                            productLabels = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String name = jsonObject.getString("name");
                                String categori = jsonObject.getString("categori");
                                float salesPerDay = Float.parseFloat(jsonObject.getString("salesperday"));
                                float salesPerWeek = Float.parseFloat(jsonObject.getString("salesperweek"));
                                float salesPerMonth = Float.parseFloat(jsonObject.getString("salespermonth"));

                                // 카테고리 데이터 처리
                                if (categorySalesMap.containsKey(categori)) {
                                    ArrayList<Float> salesList = categorySalesMap.get(categori);
                                    salesList.set(0, salesList.get(0) + salesPerDay);
                                    salesList.set(1, salesList.get(1) + salesPerWeek);
                                    salesList.set(2, salesList.get(2) + salesPerMonth);
                                } else {
                                    ArrayList<Float> salesList = new ArrayList<>();
                                    salesList.add(salesPerDay);
                                    salesList.add(salesPerWeek);
                                    salesList.add(salesPerMonth);
                                    categorySalesMap.put(categori, salesList);
                                    categoryLabels.add(categori); // 카테고리 라벨 추가 (중복 없이)
                                }

                                // 프로덕트 데이터 처리
                                if (productSalesMap.containsKey(name)) {
                                    ArrayList<Float> salesList = productSalesMap.get(name);
                                    salesList.set(0, salesList.get(0) + salesPerDay);
                                    salesList.set(1, salesList.get(1) + salesPerWeek);
                                    salesList.set(2, salesList.get(2) + salesPerMonth);
                                } else {
                                    ArrayList<Float> salesList = new ArrayList<>();
                                    salesList.add(salesPerDay);
                                    salesList.add(salesPerWeek);
                                    salesList.add(salesPerMonth);
                                    productSalesMap.put(name, salesList);
                                    productLabels.add(name); // 프로덕트 라벨 추가 (중복 없이)
                                }
                            }

                            // 카테고리의 데이터 추가
                            int categoryIndex = 0;
                            for (String label : categoryLabels) {
                                ArrayList<Float> salesList = categorySalesMap.get(label);
                                categorySalesPerDayEntries.add(new BarEntry(categoryIndex, salesList.get(0)));
                                categorySalesPerWeekEntries.add(new BarEntry(categoryIndex, salesList.get(1)));
                                categorySalesPerMonthEntries.add(new BarEntry(categoryIndex, salesList.get(2)));
                                Log.d("CategoryData", "Category: " + label + ", SalesPerDay: " + salesList.get(0) + ", SalesPerWeek: " + salesList.get(1) + ", SalesPerMonth: " + salesList.get(2));
                                categoryIndex++;
                            }

                            // 프로덕트의 데이터 추가
                            int productIndex = 0;
                            for (String label : productLabels) {
                                ArrayList<Float> salesList = productSalesMap.get(label);
                                productSalesPerDayEntries.add(new BarEntry(productIndex, salesList.get(0)));
                                productSalesPerWeekEntries.add(new BarEntry(productIndex, salesList.get(1)));
                                productSalesPerMonthEntries.add(new BarEntry(productIndex, salesList.get(2)));
                                productIndex++;
                            }

                            // categorySalesMap 및 productSalesMap을 로그로 출력
                            Log.d("SalesData", "Category Sales Map: " + categorySalesMap);
                            Log.d("SalesData", "Product Sales Map: " + productSalesMap);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(requireContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    private void updateChart() {
        Chip selectedXAxisChip = getView().findViewById(chipGroupXAxis.getCheckedChipId());
        Chip selectedChipPeriod = getView().findViewById(chipGroupPeriod.getCheckedChipId());

        if (selectedXAxisChip != null && selectedChipPeriod != null) {
            String selectedXAxis = selectedXAxisChip.getText().toString();
            String selectedPeriod = selectedChipPeriod.getText().toString();

            // 선택된 축(X Axis)와 기간(Period)에 따라 필터링된 데이터를 가져옵니다.
            ArrayList<BarEntry> filteredEntries = getFilteredSalesData(selectedXAxis, selectedPeriod);

            BarDataSet barDataSet = new BarDataSet(filteredEntries, "Sales Data");

            // 그래프 색상을 D04E1B로 설정합니다.
            barDataSet.setColor(Color.parseColor("#FFCC80"));

            BarData barData = new BarData(barDataSet);
            barChart.setData(barData);

            XAxis xAxis = barChart.getXAxis();

            // 라벨 리스트를 선택된 X 축에 따라 설정
            ArrayList<String> selectedLabels = selectedXAxis.equals("Category") ? categoryLabels : productLabels;

            // X 축 라벨을 설정하는 ValueFormatter
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    int index = Math.round(value); // value를 반올림하여 인덱스로 변환
                    if (index >= 0 && index < selectedLabels.size()) {
                        return selectedLabels.get(index);
                    }
                    return "";
                }
            });

            // X 축 라벨의 최대 수를 설정
            xAxis.setLabelCount(selectedLabels.size());

            // X 축 그리드 라인 제거
            xAxis.setDrawGridLines(false);

            // Y 축 그리드 라인 제거
            YAxis yAxisLeft = barChart.getAxisLeft();
            YAxis yAxisRight = barChart.getAxisRight();
            yAxisLeft.setDrawGridLines(false);
            yAxisRight.setDrawGridLines(false);

            barChart.invalidate(); // 차트 갱신
        } else {
            // 선택된 Chip이 없을 경우, 차트를 숨깁니다.
            barChart.clear();  // 차트의 데이터 지우기
            barChart.invalidate();  // 차트를 갱신하여 화면에서 제거
        }
    }



    private ArrayList<BarEntry> getFilteredSalesData(String xAxis, String period) {
        ArrayList<BarEntry> filteredEntries = new ArrayList<>();

        // 선택된 데이터 셋을 가져옵니다.
        ArrayList<BarEntry> selectedEntries;
        if (xAxis.equals("Category")) {
            switch (period) {
                case "SalesPerDay":
                    selectedEntries = categorySalesPerDayEntries;
                    break;
                case "SalesPerWeek":
                    selectedEntries = categorySalesPerWeekEntries;
                    break;
                case "SalesPerMonth":
                    selectedEntries = categorySalesPerMonthEntries;
                    break;
                default:
                    selectedEntries = new ArrayList<>();
                    break;
            }
        } else { // Product
            switch (period) {
                case "SalesPerDay":
                    selectedEntries = productSalesPerDayEntries;
                    break;
                case "SalesPerWeek":
                    selectedEntries = productSalesPerWeekEntries;
                    break;
                case "SalesPerMonth":
                    selectedEntries = productSalesPerMonthEntries;
                    break;
                default:
                    selectedEntries = new ArrayList<>();
                    break;
            }
        }

        // 선택된 축의 데이터만 필터링합니다.
        for (BarEntry entry : selectedEntries) {
            filteredEntries.add(new BarEntry(entry.getX(), entry.getY()));
        }

        // 신선식품에 대한 SalesPerWeek 데이터를 로그로 출력
        Log.d("SalesPerWeek", "Filtered Entries: " + filteredEntries.toString());

        return filteredEntries;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
