package com.example.capdi2024_ver1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.PurchaseViewHolder> {
    private List<Purchase> purchaseList;

    public static class PurchaseViewHolder extends RecyclerView.ViewHolder {
        public TextView itemNameTextView;
        public TextView priceTextView;
        public TextView dateTextView;

        public PurchaseViewHolder(View view) {
            super(view);
            itemNameTextView = view.findViewById(R.id.item_name);
            priceTextView = view.findViewById(R.id.price);
            dateTextView = view.findViewById(R.id.date);
        }
    }

    public PurchaseAdapter(List<Purchase> purchaseList) {
        this.purchaseList = purchaseList;
    }

    @NonNull
    @Override
    public PurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.purchase_item, parent, false);
        return new PurchaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseViewHolder holder, int position) {
        Purchase purchase = purchaseList.get(position);
        holder.itemNameTextView.setText(purchase.getItemName());
        holder.priceTextView.setText(String.valueOf(purchase.getPrice()));
        holder.dateTextView.setText(purchase.getDate());
    }

    @Override
    public int getItemCount() {
        return purchaseList.size();
    }
}
