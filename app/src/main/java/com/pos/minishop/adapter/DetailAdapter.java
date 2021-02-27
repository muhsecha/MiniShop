package com.pos.minishop.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pos.minishop.CategoryManagementActivity;
import com.pos.minishop.R;
import com.pos.minishop.model.CategoryModel;
import com.pos.minishop.model.DetailModel;

import java.util.ArrayList;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.DetailViewHolder> {
    private ArrayList<DetailModel> listDetail;

    public DetailAdapter(ArrayList<DetailModel> listDetail) {
        this.listDetail = listDetail;
    }

    @NonNull
    @Override
    public DetailAdapter.DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_detail_transaksi,parent,false);
        return new DetailViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull DetailAdapter.DetailViewHolder holder, int position) {
        DetailModel detailModel = listDetail.get(position);
        holder.nama.setText(detailModel.getNama());
        holder.stock.setText(detailModel.getStock());
        holder.harga.setText(detailModel.getHarga());
        holder.total.setText(detailModel.getTotal());
        holder.jam.setText(detailModel.getJam());
        holder.tanggal.setText(detailModel.getTanggal());
    }

    @Override
    public int getItemCount() {
        return listDetail.size();
    }

    public class DetailViewHolder extends RecyclerView.ViewHolder {
        TextView nama,stock,harga,total,jam,tanggal;

        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.tv_detail_name);
            stock = itemView.findViewById(R.id.tv_detail_stock);
            harga = itemView.findViewById(R.id.tv_detail_price);
            total = itemView.findViewById(R.id.tv_total_detail);
            jam = itemView.findViewById(R.id.tv_detail_jam);
            tanggal = itemView.findViewById(R.id.tv_detail_tgl);
        }
    }
}
