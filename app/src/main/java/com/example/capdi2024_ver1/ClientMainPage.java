package com.example.capdi2024_ver1;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.capdi2024_ver1.databinding.ActivityClientMainPageBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ClientMainPage extends AppCompatActivity {

    private ActivityClientMainPageBinding binding;
    private SharedViewModel sharedViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityClientMainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up the Toolbar as ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up Navigation with AppBarConfiguration
        BottomNavigationView navView = binding.navView;
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_client_main_page);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        Button test = findViewById(R.id.disconnect_button);
        // Intent로부터 데이터 받기
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);  // `SharedViewModel` 가져오기
        sharedViewModel.setUserId(userId);

        // `Activity`에서 `NavController`를 사용하여 목적지 변경을 감지

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            Button disconnectButton = findViewById(R.id.disconnect_button);
            if (destination.getId() == R.id.navigation_dashboard) {  // 특정 `Fragment`에서
                disconnectButton.setVisibility(View.VISIBLE);  // `Button`을 보이게 설정
            } else {  // 다른 `Fragment`에서는
                disconnectButton.setVisibility(View.GONE);  // `Button`을 숨김
            }
        });

        // 이 데이터를 Fragment에 전달하는 방법 중 하나는 NavController를 통해 Bundle로 전달하는 것입니다.
        Bundle bundle = new Bundle();
        bundle.putString("userid", userId);
        navController.navigate(R.id.navigation_dashboard, bundle);  // navigation_dashboard에 데이터를 전달
        navController.navigate(R.id.navigation_notifications, bundle);  // navigation_dashboard에 데이터를 전달
        navController.navigate(R.id.navigation_home, bundle);  // navigation_dashboard에 데이터를 전달
    }

}
