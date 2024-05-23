package com.example.capdi2024_ver1.ui.notifications;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.capdi2024_ver1.Purchase;
import com.example.capdi2024_ver1.PurchaseAdapter;
import com.example.capdi2024_ver1.R;
import com.example.capdi2024_ver1.SharedViewModel;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private PurchaseAdapter purchaseAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        final TextView userIdTextView = root.findViewById(R.id.user_id_text_view);
        final TextView userNameTextView = root.findViewById(R.id.user_name_text_view);
        final TextView userEmailTextView = root.findViewById(R.id.user_email_text_view);
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        sharedViewModel.getUserId().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String userId) {
                userIdTextView.setText("User ID: " + userId);
            }
        });

        sharedViewModel.getUserName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String userName) {
                userNameTextView.setText("User Name: " + userName);
            }
        });

        sharedViewModel.getUserEmail().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String userEmail) {
                userEmailTextView.setText("User Email: " + userEmail);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        purchaseAdapter = new PurchaseAdapter(new ArrayList<>());
        recyclerView.setAdapter(purchaseAdapter);

        sharedViewModel.getPurchases().observe(getViewLifecycleOwner(), new Observer<List<Purchase>>() {
            @Override
            public void onChanged(List<Purchase> purchases) {
                purchaseAdapter.setPurchases(purchases);
            }
        });

        return root;
    }
}
