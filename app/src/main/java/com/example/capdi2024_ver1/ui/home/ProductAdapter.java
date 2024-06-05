package com.example.capdi2024_ver1.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.capdi2024_ver1.R;
import com.example.capdi2024_ver1.admin_ui.dashboard.ItemData;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<ItemData> itemList;

    public ProductAdapter(List<ItemData> itemList) {
        this.itemList = itemList;
    }

    public void updateItemList(List<ItemData> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    public void setItems(List<ItemData> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemData item = itemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView itemNameTextView;
        private TextView itemPriceTextView;
        private TextView itemLocationTextView;
        private ImageView itemImageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            itemPriceTextView = itemView.findViewById(R.id.itemPriceTextView);
            itemLocationTextView = itemView.findViewById(R.id.itemLocationTextView);
            itemImageView = itemView.findViewById(R.id.item_Image);
        }

        void bind(ItemData item) {
            itemNameTextView.setText(item.getName());
            itemPriceTextView.setText(String.valueOf(item.getPrice()));
            itemLocationTextView.setText(item.getLocation());

            // 이미지 리소스를 설정합니다.
            int resId = itemView.getContext().getResources().getIdentifier(item.getImageResource(), "drawable", itemView.getContext().getPackageName());
            if (resId != 0) {
                itemImageView.setImageResource(resId);
            } else {
                // 리소스가 없을 경우의 기본 이미지를 설정합니다.
                itemImageView.setImageResource(R.drawable.default_image);
            }
        }
    }
}
