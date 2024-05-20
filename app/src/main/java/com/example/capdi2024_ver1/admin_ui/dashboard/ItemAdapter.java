package com.example.capdi2024_ver1.admin_ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capdi2024_ver1.R;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<ItemData> itemList;

    public ItemAdapter(List<ItemData> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ItemData item = itemList.get(position);
        holder.nameTextView.setText(item.getName());
        holder.manufacturerTextView.setText(item.getManufacturer());
        holder.priceTextView.setText(String.valueOf(item.getPrice()));
        holder.amountTextView.setText(String.valueOf(item.getAmount()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, manufacturerTextView, priceTextView, amountTextView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            manufacturerTextView = itemView.findViewById(R.id.manufacturerTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
        }
    }


    public void updateItemList(List<ItemData> newList) {
        itemList = newList;
        notifyDataSetChanged();
    }
}
