package com.pos.minishop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pos.minishop.R;
import com.pos.minishop.model.HistoryModel;
import com.pos.minishop.model.ProductModel;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private ArrayList<HistoryModel> listHistory = new ArrayList<>();
    private OnItemClickCallback onItemClickCallback;

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public HistoryAdapter(ArrayList<HistoryModel> listHistory) {
        this.listHistory = listHistory;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_transaksi, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryModel history = listHistory.get(position);
        holder.tvTotalBuy.setText("Rp" + history.getTotalBuy());
        holder.tvDate.setText(history.getDate());
        holder.tvTime.setText(history.getTime());
        holder.tvTrxNumber.setText(history.getTrxNumber());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickCallback.onItemClicked(listHistory.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listHistory.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTotalBuy, tvDate, tvTime, tvTrxNumber;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTotalBuy = itemView.findViewById(R.id.tv_total_buy);
            tvDate = itemView.findViewById(R.id.tv_date_listTrans);
            tvTime = itemView.findViewById(R.id.tv_time_listTrans);
            tvTrxNumber = itemView.findViewById(R.id.tv_trx_number);
        }
    }

    public interface OnItemClickCallback {
        void onItemClicked(HistoryModel data);
    }
}
