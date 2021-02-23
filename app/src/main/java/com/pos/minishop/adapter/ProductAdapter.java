package com.pos.minishop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pos.minishop.CategoryManagementActivity;
import com.pos.minishop.R;
import com.pos.minishop.model.CategoryModel;
import com.pos.minishop.model.ProductModel;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{
    private ArrayList<ProductModel> listProduct;
    private OnItemClickCallback onItemClickCallback;

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public ProductAdapter(ArrayList<ProductModel> listProduct) {
        this.listProduct = listProduct;
    }

    @NonNull
    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder holder, int position) {
        ProductModel productModel = listProduct.get(position);
        holder.name.setText(productModel.getName());
        holder.price.setText("Rp. " + productModel.getPrice());
        holder.stock.setText(productModel.getStock());

        if (productModel.getImage() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(productModel.getImage())
                    .into(holder.image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickCallback.onItemClicked(listProduct.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, price, stock;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.iv_product_list);
            name = itemView.findViewById(R.id.tv_nameProduct_list);
            price = itemView.findViewById(R.id.tv_realPrice_list);
            stock = itemView.findViewById(R.id.tv_countProduct_list);
        }
    }

    public interface OnItemClickCallback {
        void onItemClicked(ProductModel data);
    }
}
