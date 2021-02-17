package com.pos.minishop.ui.transaksi;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.pos.minishop.CalcuActivity;
import com.pos.minishop.R;
import com.pos.minishop.adapter.CartAdapter;
import com.pos.minishop.model.CartModel;

import java.util.ArrayList;

public class TransFragment extends Fragment {

    int count;
    RecyclerView rvTrans;
    RelativeLayout rlProduct;
    private ArrayList<CartModel> cartModel;
    CartAdapter cartAdapters;
    TextView tvCount;
    ImageView ivC;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_transaction, container, false);
        rvTrans = root.findViewById(R.id.rv_cart);
        rlProduct =  root.findViewById(R.id.rl_listProduct);
        tvCount = root.findViewById(R.id.tv_countItem);
        ivC = root.findViewById(R.id.iv_count);

        ivC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CalcuActivity.class));
            }
        });


        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Transaction");
    }
}