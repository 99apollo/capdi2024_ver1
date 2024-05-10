package com.example.capdi2024_ver1.admin_ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.capdi2024_ver1.R;
import com.example.capdi2024_ver1.databinding.FragmentAdminDashboardBinding;
import com.google.android.material.chip.ChipGroup;

public class AdminDashboardFragment extends Fragment {

    private FragmentAdminDashboardBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get references to views
        RadioGroup radioGroup = binding.radioGroup;
        ChipGroup chipGroup = binding.chipGroup;

        // Set visibility to GONE initially
        chipGroup.setVisibility(View.GONE);

        // Set listener for RadioGroup
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButton1) {
                chipGroup.setVisibility(View.VISIBLE); // Show chip group when option 1 is selected
            } else {
                chipGroup.setVisibility(View.GONE); // Hide when other options are selected
            }
        });

        // Set onClickListeners for each chip
        binding.chip1.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Chip 1 clicked", Toast.LENGTH_SHORT).show());
        binding.chip2.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Chip 2 clicked", Toast.LENGTH_SHORT).show());
        binding.chip3.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Chip 3 clicked", Toast.LENGTH_SHORT).show());
        binding.chip4.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Chip 4 clicked", Toast.LENGTH_SHORT).show());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
