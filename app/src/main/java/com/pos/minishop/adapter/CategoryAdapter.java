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
import com.pos.minishop.model.CategoryModel;
import com.pos.minishop.ui.CategoryManagementActivity;
import com.pos.minishop.ui.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private final ArrayList<CategoryModel> listCategory;
    private final CategoryManagementActivity categoryManagementActivity;
    private OnItemClickCallback onItemClickCallback;

    public CategoryAdapter(ArrayList<CategoryModel> listCategory, Activity CategoryManagementActivity) {
        this.listCategory = listCategory;
        this.categoryManagementActivity = (CategoryManagementActivity) CategoryManagementActivity;
    }

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryViewHolder holder, int position) {
        CategoryModel category = listCategory.get(position);
        holder.name.setText(category.getName());

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryManagementActivity.deleteCategory(category.getId());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickCallback.onItemClicked(listCategory.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listCategory.size();
    }

    public interface OnItemClickCallback {
        void onItemClicked(CategoryModel data);
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        Button btnDelete;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_listCategory_list);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
