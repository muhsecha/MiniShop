package com.pos.minishop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pos.minishop.R;
import com.pos.minishop.model.CartModel;
import com.pos.minishop.model.ProductModel;
import com.pos.minishop.model.TransModel;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{
    private ArrayList<CartModel> listcart;
    public CartAdapter(ArrayList<CartModel> listcart) {
        this.listcart = listcart;
    }
    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return listcart.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
