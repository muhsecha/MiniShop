package com.pos.minishop.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
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
import com.pos.minishop.model.TransModel;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{

    static ArrayList<TransModel> listTrans;
    OnItemClickListener mListener;
    Context mContext;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public CartAdapter(Context mContext, ArrayList<TransModel> listTrans) {
        this.mContext = mContext;
        this.listTrans = listTrans;
    }


    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cart, parent, false);
        return new CartViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {
        TransModel cart = listTrans.get(position);
        holder.tvProduct.setText(cart.getNameProduct());
        holder.tvStock.setText(String.valueOf(cart.getAmount()));
        holder.tvprice.setText(cart.getPrice());

        if (cart.getCartImage() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(cart.getCartImage())
                    .into(holder.ivCart);
        }
    }

    @Override
    public int getItemCount() {
        return listTrans.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCart;
        TextView tvProduct,tvStock,tvprice;
        RelativeLayout relativeLayout;
        public CartViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            ivCart =  itemView.findViewById(R.id.iv_cart_list);
            tvProduct =  itemView.findViewById(R.id.tv_cart_name);
            tvprice =  itemView.findViewById(R.id.tv_cart_price);
            tvStock =  itemView.findViewById(R.id.tv_cart_stock);
            relativeLayout = itemView.findViewById(R.id.rl_cart);

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
