package com.pos.minishop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pos.minishop.R;
import com.pos.minishop.model.MemberModel;

import java.util.ArrayList;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {
    private final ArrayList<MemberModel> listMember;
    private OnItemClickCallback onItemClickCallback;

    public MemberAdapter(ArrayList<MemberModel> listMember) {
        this.listMember = listMember;
    }

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        MemberModel member = listMember.get(position);
        holder.nama.setText(member.getName());
        holder.address.setText(member.getAddress());
        holder.gender.setText(member.getGender());
        holder.date.setText(member.getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickCallback.onItemClicked(listMember.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listMember.size();
    }

    public interface OnItemClickCallback {
        void onItemClicked(MemberModel data);
    }

    public class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView nama, address, gender, date;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.tv_name_member);
            address = itemView.findViewById(R.id.tv_address_member);
            gender = itemView.findViewById(R.id.tv_gender_member);
            date = itemView.findViewById(R.id.tv_date_member);
        }
    }
}
