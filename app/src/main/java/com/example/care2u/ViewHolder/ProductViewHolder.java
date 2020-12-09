package com.example.care2u.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.care2u.Interface.ItemClickListener;
import com.example.care2u.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView homeProductName, homeProductPrice;
    public ImageView homeImageView;
    private ItemClickListener listener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        homeProductName = (TextView) itemView.findViewById(R.id.home_product_name);
        homeProductPrice = (TextView) itemView.findViewById(R.id.home_product_price);
        homeImageView = (ImageView) itemView.findViewById(R.id.home_product_image);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view, getAdapterPosition(),false);
    }
}
