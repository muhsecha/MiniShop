package com.pos.minishop.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pos.minishop.DiscountManagementActivity;
import com.pos.minishop.R;
import com.pos.minishop.model.CategoryModel;
import com.pos.minishop.model.DiscountModel;
import com.pos.minishop.model.ListMemberModel;

import java.util.ArrayList;

public class MemberLisrAdapter extends RecyclerView.Adapter<MemberLisrAdapter.MemberViewHolder> {
    private ArrayList<ListMemberModel> listMember;

    public MemberLisrAdapter(ArrayList<ListMemberModel> listMember) {
        this.listMember = listMember;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        ListMemberModel listMemberModel = listMember.get(position);
        holder.nama.setText(listMemberModel.getNama());
        holder.address.setText(listMemberModel.getAlamat());
        holder.gender.setText(listMemberModel.getGender());
        holder.date.setText(listMemberModel.getDate());
    }

    @Override
    public int getItemCount() {
        return listMember.size();
    }

    public class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView nama,address,gender,date;
        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.tv_name_member);
            address = itemView.findViewById(R.id.tv_address_member);
            gender = itemView.findViewById(R.id.tv_gender_member);
            date = itemView.findViewById(R.id.tv_date_member);
        }
    }
}
