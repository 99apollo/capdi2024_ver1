package com.example.capdi2024_ver1.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        holder.cartIdTextView.setText("price : " + cartItem.getPrice());
        holder.itemIdTextView.setText("Item ID: " + cartItem.getItemId());
        holder.itemCountTextView.setText("count: " + cartItem.getCount());

        // getItemValue()가 이미지 파일 이름과 일치한다고 가정
        String itemValue = cartItem.getItemValue();

        // 밸류값의 7번째 글자를 사용하여 리소스 이름 생성
        if (itemValue.length() >= 7) {
            String resourceName = "u" + itemValue.charAt(6);

            // 리소스 이름으로 리소스 ID 가져오기
            int imageResourceId = holder.itemView.getContext().getResources().getIdentifier(resourceName, "drawable", holder.itemView.getContext().getPackageName());

            // 리소스 ID가 유효한지 확인
            if (imageResourceId != 0) {
                holder.itemImageView.setImageResource(imageResourceId);
            } else {
                // 유효하지 않은 경우 기본 이미지 설정
                holder.itemImageView.setImageResource(R.drawable.default_image); // 기본 이미지로 대체
            }
        } else {
            // 밸류값이 7글자 이상이 아닌 경우 기본 이미지 설정
            holder.itemImageView.setImageResource(R.drawable.default_image); // 기본 이미지로 대체
        }
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
        ImageView itemImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cartIdTextView = itemView.findViewById(R.id.cart_id);
            itemIdTextView = itemView.findViewById(R.id.item_id);
            itemCountTextView = itemView.findViewById(R.id.item_count);
            itemImageView = itemView.findViewById(R.id.item_Image);
        }
    }
}

