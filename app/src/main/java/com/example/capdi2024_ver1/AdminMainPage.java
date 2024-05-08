package com.example.capdi2024_ver1;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.capdi2024_ver1.databinding.ActivityAdminMainPageBinding;

public class AdminMainPage extends AppCompatActivity {

    private ActivityAdminMainPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminMainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
// Set up the Toolbar as ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_admin_home, R.id.navigation_admin_dashboard, R.id.navigation_admin_notifications)
                .build();
        // `Toolbar` 설정 후에 `NavController`와 연동
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_admin_main_page);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

}