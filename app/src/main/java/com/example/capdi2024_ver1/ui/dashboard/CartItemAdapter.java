package com.example.capdi2024_ver1.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capdi2024_ver1.R;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {

    private final List<CartItem> cartItems;

    public CartItemAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.cartIdTextView.setText("Cart ID: " + cartItem.getCartId());
        holder.itemIdTextView.setText("Item ID: " + cartItem.getItemId());
        holder.itemValueTextView.setText("Item Value: " + cartItem.getItemValue());
        holder.itemCountTextView.setText("count: "+cartItem.getCount());
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cartIdTextView;
        TextView itemIdTextView;
        TextView itemValueTextView;
        TextView itemCountTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cartIdTextView = itemView.findViewById(R.id.cart_id);
            itemIdTextView = itemView.findViewById(R.id.item_id);
            itemValueTextView = itemView.findViewById(R.id.item_value);
            itemCountTextView = itemView.findViewById(R.id.item_count);
        }
    }
}

