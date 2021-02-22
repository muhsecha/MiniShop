package com.pos.minishop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pos.minishop.R;
import com.pos.minishop.model.CartModel;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{

    private ArrayList<CartModel> listCart;
    OnItemClickListener mListener;
    Context mContext;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public CartAdapter(Context mContext, ArrayList<CartModel> listCart) {
        this.mContext = mContext;
        this.listCart = listCart;
    }


    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_sales_transaction, parent, false);
        return new CartViewHolder(view, mListener);
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
        RelativeLayout relativeLayout;
        public CartViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            ivCart =  itemView.findViewById(R.id.iv_sales_transaction);
            tvProduct =  itemView.findViewById(R.id.tv_nameProduct_transaction);
            tvStock =  itemView.findViewById(R.id.tv_stock_transaction);
            tvprice =  itemView.findViewById(R.id.tv_sellPrice_transaction);
            relativeLayout = itemView.findViewById(R.id.rl_sales_transaction);

            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
