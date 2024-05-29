package com.example.capdi2024_ver1.ui.notifications;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import com.example.capdi2024_ver1.Purchase;
import com.example.capdi2024_ver1.PurchaseAdapter;
import com.example.capdi2024_ver1.R;
import com.example.capdi2024_ver1.SharedViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private PurchaseAdapter purchaseAdapter;
    private List<Purchase> allPurchases = new ArrayList<>();
    private TextView totalAmountTextView;
    private CalendarView calendarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        final TextView userIdTextView = view.findViewById(R.id.user_id_text_view);
        final TextView userNameTextView = view.findViewById(R.id.user_name_text_view);
        final TextView userEmailTextView = view.findViewById(R.id.user_email_text_view);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        calendarView = view.findViewById(R.id.calendar_view);
        totalAmountTextView = view.findViewById(R.id.total_amount_text_view);

        // Initialize ViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Log initial values
        if (sharedViewModel.getUserId().getValue() != null) {
            Log.d(TAG, "Initial ViewModel ID: " + sharedViewModel.getUserId().getValue());
        } else {
            Log.d(TAG, "Initial ViewModel ID: null");
        }

        if (sharedViewModel.getUserName().getValue() != null) {
            Log.d(TAG, "Initial ViewModel Name: " + sharedViewModel.getUserName().getValue());
        } else {
            Log.d(TAG, "Initial ViewModel Name: null");
        }

        if (sharedViewModel.getUserEmail().getValue() != null) {
            Log.d(TAG, "Initial ViewModel Email: " + sharedViewModel.getUserEmail().getValue());
        } else {
            Log.d(TAG, "Initial ViewModel Email: null");
        }

        // Observe LiveData from ViewModel
        sharedViewModel.getUserId().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String userId) {
                userIdTextView.setText("User ID: " + userId);
                Log.d(TAG, "Observed User ID: " + userId);
            }
        });

        sharedViewModel.getUserName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String userName) {
                userNameTextView.setText("User Name: " + userName);
                Log.d(TAG, "Observed User Name: " + userName);
            }
        });

        sharedViewModel.getUserEmail().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String userEmail) {
                userEmailTextView.setText("User Email: " + userEmail);
                Log.d(TAG, "Observed User Email: " + userEmail);
            }
        });

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        purchaseAdapter = new PurchaseAdapter(new ArrayList<>());
        recyclerView.setAdapter(purchaseAdapter);

        sharedViewModel.getPurchases().observe(getViewLifecycleOwner(), new Observer<List<Purchase>>() {
            @Override
            public void onChanged(List<Purchase> purchases) {
                Log.d(TAG, "Observed Purchases: " + purchases.toString());
                allPurchases = purchases;
                // Show purchases for today's date
                String todayDate = getTodayDate();
                List<Purchase> purchasesForToday = getPurchasesForDate(todayDate);
                purchaseAdapter.setPurchases(purchasesForToday);
                int totalAmount = calculateTotalAmount(purchasesForToday);
                totalAmountTextView.setText("Total Amount: " + totalAmount);
            }
        });

        // Set up CalendarView listener
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            List<Purchase> purchasesForSelectedDate = getPurchasesForDate(selectedDate);
            purchaseAdapter.setPurchases(purchasesForSelectedDate);
            int totalAmount = calculateTotalAmount(purchasesForSelectedDate);
            totalAmountTextView.setText("Total Amount: " + totalAmount);
        });

        // Set CalendarView date to today
        setTodayAsDefaultDate();
    }

    private List<Purchase> getPurchasesForDate(String date) {
        List<Purchase> purchasesForDate = new ArrayList<>();
        for (Purchase purchase : allPurchases) {
            if (purchase.getDate().equals(date)) {
                purchasesForDate.add(purchase);
            }
        }
        return purchasesForDate;
    }

    private int calculateTotalAmount(List<Purchase> purchases) {
        int totalAmount = 0;
        for (Purchase purchase : purchases) {
            totalAmount += purchase.getPrice();
        }
        return totalAmount;
    }

    private void setTodayAsDefaultDate() {
        Calendar calendar = Calendar.getInstance();
        long today = calendar.getTimeInMillis();
        calendarView.setDate(today, false, true);
    }

    private String getTodayDate() {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(today);
    }
}
