package com.pos.minishop.ui.transaksi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.pos.minishop.CalcuActivity;
import com.pos.minishop.R;
import com.pos.minishop.adapter.CartAdapter;
import com.pos.minishop.adapter.ProductAdapter;
import com.pos.minishop.baseUrl.BaseUrl;
import com.pos.minishop.model.CartModel;
import com.pos.minishop.model.ProductModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class TransFragment extends Fragment {

    int count;
    RecyclerView rvTrans;
    RelativeLayout rlProduct;
    private ArrayList<CartModel> listCart = new ArrayList<>();
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

        getProducts();

        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Transaction");
    }

    public void showProducts() {
        rvTrans.setLayoutManager(new LinearLayoutManager(getContext()));
        CartAdapter cartAdapter = new CartAdapter(listCart);
        rvTrans.setAdapter(cartAdapter);
    }

    public void getProducts() {
        SharedPreferences sp = getActivity().getSharedPreferences("login", MODE_PRIVATE);
        String token = sp.getString("logged", "missing");

        AndroidNetworking.get(BaseUrl.url + "products")
                .addHeaders("Authorization", "Bearer " + token)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");

                            if (status.equals("success")) {
                                JSONArray data = response.getJSONArray("data");

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject item = data.getJSONObject(i);

                                    CartModel cartModel = new CartModel();
                                    cartModel.setNameProduct(item.getString("name"));
                                    cartModel.setPrice(item.getString("price"));
                                    cartModel.setStock(item.getString("stock"));
                                    cartModel.setCartImage("http://127.0.0.1:8000/storage/" + item.getString("image"));
                                    listCart.add(cartModel);
                                }

                                showProducts();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    @Override
                    public void onError(ANError anError) {
                        Log.d("TAG", "onError: " + anError.getErrorDetail());
                        Log.d("TAG", "onError: " + anError.getErrorBody());
                        Log.d("TAG", "onError: " + anError.getErrorCode());
                        Log.d("TAG", "onError: " + anError.getResponse());
                    }
                });
    }
}