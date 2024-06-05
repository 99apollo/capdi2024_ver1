package com.example.capdi2024_ver1.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.capdi2024_ver1.R;
import com.example.capdi2024_ver1.admin_ui.dashboard.ItemData;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<ItemData> searchResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        recyclerView = findViewById(R.id.recycler_view_search_results);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get search results from intent
        if (getIntent() != null && getIntent().hasExtra("search_results")) {
            searchResults = getIntent().getParcelableArrayListExtra("search_results");
            Log.d("SearchResultsActivity", "Search results received: " + searchResults.toString());

            // Set the adapter with the search results
            productAdapter = new ProductAdapter(searchResults);
            recyclerView.setAdapter(productAdapter);
        } else {
            // Show a message if no search results are found
            Toast.makeText(this, "No search results found", Toast.LENGTH_SHORT).show();
        }
    }
}
