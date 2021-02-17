package com.pos.minishop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pos.minishop.R;
import com.pos.minishop.model.CartModel;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{

    private ArrayList<CartModel> listCart;

    public CartAdapter(ArrayList<CartModel> listCart) {
        this.listCart = listCart;
    }


    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_sales_transaction, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {
        CartModel cart = listCart.get(position);
        holder.tvProduct.setText(cart.getNameProduct());
        holder.tvStock.setText(cart.getStock());
        holder.tvprice.setText(cart.getPrice());

        if (cart.getCartImage() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(cart.getCartImage())
                    .into(holder.ivCart);
        }
    }

    @Override
    public int getItemCount() {
        return listCart.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCart;
        TextView tvProduct,tvStock,tvprice;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCart =  itemView.findViewById(R.id.iv_sales_transaction);
            tvProduct =  itemView.findViewById(R.id.tv_nameProduct_transaction);
            tvStock =  itemView.findViewById(R.id.tv_stock_transaction);
            tvprice =  itemView.findViewById(R.id.tv_sellPrice_transaction);
        }
    }
}
