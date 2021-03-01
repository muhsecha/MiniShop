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
import com.pos.minishop.model.HistoryModel;

import java.util.ArrayList;

public class DetailHistoryAdapter extends RecyclerView.Adapter<DetailHistoryAdapter.DetailHistoryViewHolder> {
    private ArrayList<HistoryModel> listHistory = new ArrayList<>();

    public DetailHistoryAdapter(ArrayList<HistoryModel> listHistory) {
        this.listHistory = listHistory;
    }

    @NonNull
    @Override
    public DetailHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_detail_transaksi, parent, false);
        return new DetailHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailHistoryViewHolder holder, int position) {
        HistoryModel history = listHistory.get(position);
        holder.tvName.setText(history.getName());
        holder.tvQuantity.setText(history.getQuantity());
        holder.tvPrice.setText(history.getPrice());
        holder.tvTotalItem.setText(history.getTotalItem());

        if (history.getImage() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(history.getImage())
                    .into(holder.ivProduct);
        }
    }

    @Override
    public int getItemCount() {
        return listHistory.size();
    }

    public class DetailHistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvQuantity, tvPrice, tvTotalItem;
        private ImageView ivProduct;

        public DetailHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_detail_name);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvPrice = itemView.findViewById(R.id.tv_detail_price);
            tvTotalItem = itemView.findViewById(R.id.tv_total_detail);
            ivProduct = itemView.findViewById(R.id.iv_detail_list);
        }
    }
}
