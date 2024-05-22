package com.example.capdi2024_ver1.admin_ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capdi2024_ver1.R;

import java.util.List;

public class CartCheckItemAdapter extends RecyclerView.Adapter<CartCheckItemAdapter.ViewHolder> {
    private List<CartCheckItem> cartCheckItems;

    public CartCheckItemAdapter(List<CartCheckItem> cartCheckItems) {
        this.cartCheckItems = cartCheckItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_check_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartCheckItem cartCheckItem = cartCheckItems.get(position);
        holder.cartIdTextView.setText(cartCheckItem.getCartId());
        holder.userIdTextView.setText(cartCheckItem.getUserId());
    }

    @Override
    public int getItemCount() {
        return cartCheckItems.size();
    }

    public void updateItemList(List<CartCheckItem> newCartCheckItems) {
        cartCheckItems = newCartCheckItems;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cartIdTextView;
        TextView userIdTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cartIdTextView = itemView.findViewById(R.id.cart_id_text_view);
            userIdTextView = itemView.findViewById(R.id.user_id_text_view);
        }
    }
}


