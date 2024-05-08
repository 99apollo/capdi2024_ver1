package com.example.capdi2024_ver1.admin_ui.notifications;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.capdi2024_ver1.SharedViewModel;
import com.example.capdi2024_ver1.databinding.FragmentAdminNotificationsBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNotificationsFragment extends Fragment {

    private FragmentAdminNotificationsBinding binding;
    private SharedViewModel sharedViewModel;
    private String userId;  // 전달받은 userId를 저장할 필드
    private DatabaseReference itemListRef;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AdminNotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(AdminNotificationsViewModel.class);

        binding = FragmentAdminNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAdminNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        itemListRef = FirebaseDatabase.getInstance().getReference("item_list");
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getUserId().observe(getViewLifecycleOwner(), new Observer<String>() {  // `LiveData` 관찰
            @Override
            public void onChanged(String newUserId) {
                userId=newUserId;
                Log.d(TAG, "idin: " + userId);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}