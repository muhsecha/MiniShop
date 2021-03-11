package com.pos.minishop.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.pos.minishop.R;
import com.pos.minishop.baseUrl.BaseUrl;
import com.pos.minishop.model.DiscountModel;
import com.pos.minishop.ui.DiscountManagementActivity;
import com.pos.minishop.ui.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.DiscountViewHolder> {
    private final ArrayList<DiscountModel> listDiscount;
    private final DiscountManagementActivity discountManagementActivity;
    private OnItemClickCallback onItemClickCallback;

    public DiscountAdapter(ArrayList<DiscountModel> listDiscount, Activity DiscountManagementActivity) {
        this.listDiscount = listDiscount;
        this.discountManagementActivity = (DiscountManagementActivity) DiscountManagementActivity;
    }

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    @NonNull
    @Override
    public DiscountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_discount, parent, false);
        return new DiscountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscountViewHolder holder, int position) {
        DiscountModel discountModel = listDiscount.get(position);
        holder.name.setText(discountModel.getName());
        holder.discount.setText(discountModel.getDiscount() + "%");

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setMessage("Delete ?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                SharedPreferences sp = holder.itemView.getContext().getSharedPreferences("login", MODE_PRIVATE);
                                String token = sp.getString("logged", "missing");

                                AndroidNetworking.delete(BaseUrl.url + "api/discounts/" + discountModel.getId())
                                        .addHeaders("Authorization", "Bearer " + token)
                                        .setPriority(Priority.MEDIUM)
                                        .build()
                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    String status = response.getString("status");
                                                    String message = response.getString("message");

                                                    if (status.equals("success")) {
                                                        Toast.makeText(holder.itemView.getContext(), message, Toast.LENGTH_SHORT).show();
                                                        discountManagementActivity.getDiscounts();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onError(ANError anError) {
                                                Toast.makeText(holder.itemView.getContext(), "error", Toast.LENGTH_SHORT).show();
                                                Integer errorCode = anError.getErrorCode();

                                                if (errorCode != 0) {
                                                    if (errorCode == 401) {
                                                        SharedPreferences.Editor editor = sp.edit();
                                                        editor.clear();
                                                        editor.apply();

                                                        holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), LoginActivity.class));
                                                    }

                                                    Log.d("TAG", "onError errorCode : " + anError.getErrorCode());
                                                    Log.d("TAG", "onError errorBody : " + anError.getErrorBody());
                                                    Log.d("TAG", "onError errorDetail : " + anError.getErrorDetail());
                                                } else {
                                                    Log.d("TAG", "onError errorDetail : " + anError.getErrorDetail());
                                                }
                                            }
                                        });
                            }
                        }).create().show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickCallback.onItemClicked(listDiscount.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listDiscount.size();
    }

    public interface OnItemClickCallback {
        void onItemClicked(DiscountModel data);
    }

    public class DiscountViewHolder extends RecyclerView.ViewHolder {
        TextView name, discount;
        Button btnDelete;

        public DiscountViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_discountName_list);
            discount = itemView.findViewById(R.id.tv_percenDisc_list);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
