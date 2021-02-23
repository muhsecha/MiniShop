package com.pos.minishop.adapter;

import android.app.Activity;
import android.content.DialogInterface;
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
import com.pos.minishop.CategoryManagementActivity;
import com.pos.minishop.R;
import com.pos.minishop.baseUrl.BaseUrl;
import com.pos.minishop.model.CategoryModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class CategoryAdapter  extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>{
    private ArrayList<CategoryModel> listCategory;
    private OnItemClickCallback onItemClickCallback;
    private CategoryManagementActivity categoryManagementActivity;

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public CategoryAdapter(ArrayList<CategoryModel> listCategory, Activity CategoryManagementActivity) {
        this.listCategory = listCategory;
        this.categoryManagementActivity = (CategoryManagementActivity) CategoryManagementActivity;
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
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setMessage("Delete ?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                SharedPreferences sp = holder.itemView.getContext().getSharedPreferences("login", MODE_PRIVATE);
                                String token = sp.getString("logged", "missing");

                                AndroidNetworking.delete(BaseUrl.url + "api/product-categories/" + category.getId())
                                        .addHeaders("Authorization", "Bearer " + token)
                                        .setPriority(Priority.MEDIUM)
                                        .build()
                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    String status = response.getString("status");

                                                    if (status.equals("success")) {
                                                        String message = response.getString("message");

                                                        Toast.makeText(holder.itemView.getContext(), message, Toast.LENGTH_SHORT).show();
                                                        categoryManagementActivity.getCategories();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onError(ANError anError) {
                                                Toast.makeText(holder.itemView.getContext(), "error", Toast.LENGTH_SHORT).show();
                                                Log.d("TAG", "onError: " + anError.getErrorDetail());
                                                Log.d("TAG", "onError: " + anError.getErrorBody());
                                                Log.d("TAG", "onError: " + anError.getErrorCode());
                                                Log.d("TAG", "onError: " + anError.getResponse());
                                            }
                                        });
                            }
                        }).create().show();
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

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        Button btnDelete;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_listCategory_list);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }

    public interface OnItemClickCallback {
        void onItemClicked(CategoryModel data);
    }
}
