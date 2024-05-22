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

import java.util.List;

public class NotificationsFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private RecyclerView recyclerView;
    private PurchaseAdapter purchaseAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        TextView userIdTextView = root.findViewById(R.id.user_id_text_view);
        TextView userNameTextView = root.findViewById(R.id.user_name_text_view);
        TextView userEmailTextView = root.findViewById(R.id.user_email_text_view);

        sharedViewModel.getUserId().observe(getViewLifecycleOwner(), id -> userIdTextView.setText("User ID: " + id));

        sharedViewModel.getUserName().observe(getViewLifecycleOwner(), name -> userNameTextView.setText("User Name: " + name));

        sharedViewModel.getUserEmail().observe(getViewLifecycleOwner(), email -> userEmailTextView.setText("User Email: " + email));

        sharedViewModel.getPurchases().observe(getViewLifecycleOwner(), purchaseList -> {
            purchaseAdapter = new PurchaseAdapter(purchaseList);
            recyclerView.setAdapter(purchaseAdapter);
        });

        return root;
    }
}
