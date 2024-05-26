package com.example.capdi2024_ver1;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capdi2024_ver1.R;

import java.util.List;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.PurchaseViewHolder> {

    private List<Purchase> purchaseList;

    public PurchaseAdapter(List<Purchase> purchaseList) {
        this.purchaseList = purchaseList;
    }

    @NonNull
    @Override
    public PurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_item, parent, false);
        return new PurchaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseViewHolder holder, int position) {
        Purchase purchase = purchaseList.get(position);
        holder.purchaseDateTextView.setText(purchase.getDate());
        holder.purchaseItemNameTextView.setText(purchase.getItemName());
        holder.purchasePriceTextView.setText(String.valueOf(purchase.getPrice()));
    }

    @Override
    public int getItemCount() {
        return purchaseList.size();
    }

    public static class PurchaseViewHolder extends RecyclerView.ViewHolder {
        TextView purchaseDateTextView;
        TextView purchaseItemNameTextView;
        TextView purchasePriceTextView;

        public PurchaseViewHolder(@NonNull View itemView) {
            super(itemView);
            purchaseDateTextView = itemView.findViewById(R.id.purchase_date_text_view);
            purchaseItemNameTextView = itemView.findViewById(R.id.purchase_item_name_text_view);
            purchasePriceTextView = itemView.findViewById(R.id.purchase_price_text_view);
        }
    }

    public void setPurchases(List<Purchase> purchases) {
        this.purchaseList = purchases;
        notifyDataSetChanged();
    }
}
