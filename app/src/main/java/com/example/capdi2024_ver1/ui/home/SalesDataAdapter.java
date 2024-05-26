package com.example.capdi2024_ver1.ui.home;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capdi2024_ver1.R;
import com.example.capdi2024_ver1.ui.home.SalesData;

import java.util.List;

public class SalesDataAdapter extends RecyclerView.Adapter<SalesDataAdapter.ViewHolder> {

    private List<SalesData> salesList;

    public SalesDataAdapter(List<SalesData> salesList) {
        this.salesList = salesList;
    }

    public void setSalesList(List<SalesData> salesList) {
        this.salesList = salesList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_ranking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SalesData salesData = salesList.get(position);
        holder.bind(salesData);
    }

    @Override
    public int getItemCount() {
        return salesList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textSalesRank;
        private ImageView itemImage;
        private TextView itemNameRank;
        private TextView itemPriceRank;
        private TextView itemLocationRank;
        private TextView salenumrank;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textSalesRank = itemView.findViewById(R.id.textSalesRank);
            itemImage = itemView.findViewById(R.id.item_Image);
            itemNameRank = itemView.findViewById(R.id.itemNamerank);
            itemPriceRank = itemView.findViewById(R.id.itemPricerank);
            salenumrank = itemView.findViewById(R.id.salenumrank);

        }

        void bind(SalesData salesData) {
            textSalesRank.setText(String.valueOf(getAdapterPosition() + 1));
            //temImage.setImageResource(salesData.getImageResource());
            // For example:
            // itemImage.setImageResource(salesData.getImageResource());
            itemNameRank.setText(salesData.getName());
            itemPriceRank.setText(String.valueOf(salesData.getPrice()));
            salenumrank.setText(String.valueOf(salesData.getSalesPerDay()));

            // Add logging
            Log.d("SalesDataAdapter", "Name: " + salesData.getName() + ", Price: " + salesData.getPrice() + ", SalesPerDay: " + salesData.getSalesPerDay());
        }
    }
}
