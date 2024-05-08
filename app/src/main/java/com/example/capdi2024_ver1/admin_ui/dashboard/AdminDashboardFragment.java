package com.example.capdi2024_ver1.admin_ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.capdi2024_ver1.databinding.FragmentAdminDashboardBinding;


public class AdminDashboardFragment extends Fragment {

    private FragmentAdminDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AdminDashboardViewModel homeViewModel =
                new ViewModelProvider(this).get(AdminDashboardViewModel.class);

        binding = FragmentAdminDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAdminDashboard;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}