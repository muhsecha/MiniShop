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
import com.pos.minishop.model.TransModel;

import java.util.ArrayList;

public class TransAdapter extends RecyclerView.Adapter<TransAdapter.TransViewHolder>{

    private ArrayList<TransModel> listTrans;
    OnItemClickListener mListener;
    Context mContext;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public TransAdapter(Context mContext, ArrayList<TransModel> listTrans) {
        this.mContext = mContext;
        this.listTrans = listTrans;
    }


    @NonNull
    @Override
    public TransAdapter.TransViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_sales_transaction, parent, false);
        return new TransViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TransAdapter.TransViewHolder holder, int position) {
        TransModel cart = listTrans.get(position);
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
        return listTrans.size();
    }

    public static class TransViewHolder extends RecyclerView.ViewHolder {
        int counter;
        ImageView ivCart;
        TextView tvProduct,tvStock,tvprice,tvcount;
        RelativeLayout relativeLayout;
        public TransViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            ivCart =  itemView.findViewById(R.id.iv_sales_transaction);
            tvProduct =  itemView.findViewById(R.id.tv_nameProduct_transaction);
            tvStock =  itemView.findViewById(R.id.tv_stock_transaction);
            tvprice =  itemView.findViewById(R.id.tv_sellPrice_transaction);
            tvcount =  itemView.findViewById(R.id.tv_countTransaction);
            relativeLayout = itemView.findViewById(R.id.rl_sales_transaction);

            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                            tvcount.setVisibility(View.VISIBLE);
                            counter++;
                            tvcount.setText("" + counter);
                        }
                    }
                }
            });
        }

    }
}
