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
import com.pos.minishop.baseUrl.BaseUrl;
import com.pos.minishop.model.TransModel;

import java.util.ArrayList;

public class TransAdapter extends RecyclerView.Adapter<TransAdapter.TransViewHolder> {

    static ArrayList<TransModel> listTrans;
    OnItemClickListener mListener;
    Context mContext;

    public TransAdapter(Context mContext, ArrayList<TransModel> listTrans) {
        this.mContext = mContext;
        TransAdapter.listTrans = listTrans;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
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

        if (!cart.getCartImage().equals("null")) {
            Glide.with(holder.itemView.getContext())
                    .load(BaseUrl.url + "storage/" + cart.getCartImage())
                    .into(holder.ivCart);
        }
    }

    @Override
    public int getItemCount() {
        return listTrans.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class TransViewHolder extends RecyclerView.ViewHolder {
        int counter;
        ImageView ivCart;
        TextView tvProduct, tvStock, tvprice, tvcount;
        RelativeLayout relativeLayout;

        public TransViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            ivCart = itemView.findViewById(R.id.iv_sales_transaction);
            tvProduct = itemView.findViewById(R.id.tv_nameProduct_transaction);
            tvStock = itemView.findViewById(R.id.tv_stock_transaction);
            tvprice = itemView.findViewById(R.id.tv_sellPrice_transaction);
            tvcount = itemView.findViewById(R.id.tv_countTransaction);
            relativeLayout = itemView.findViewById(R.id.rl_sales_transaction);

            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                            String stockNow = listTrans.get(position).getStock();
                            if (Integer.parseInt(stockNow) > 0) {
                                if (tvcount.getVisibility() != View.VISIBLE) {
                                    tvcount.setVisibility(View.VISIBLE);
                                }
                                counter++;
                                listTrans.get(position).setAmount(counter);
                                listTrans.get(position).setStock(String.valueOf(Integer.parseInt(stockNow) - 1));
                                stockNow = listTrans.get(position).getStock();
                                tvcount.setText("" + counter);
                                tvStock.setText(stockNow);
                            }
                        }
                    }
                }
            });
        }

    }
}
