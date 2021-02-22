package com.pos.minishop.ui.transaksi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.pos.minishop.CartActivity;
import com.pos.minishop.R;
import com.pos.minishop.adapter.TransAdapter;
import com.pos.minishop.baseUrl.BaseUrl;
import com.pos.minishop.model.TransModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class TransFragment extends Fragment {

    int count;
    RecyclerView rvTrans;
    private ArrayList<TransModel> listCart = new ArrayList<>();
    private TransAdapter adapter;
    TextView tvCount;
    ImageView ivC;
    TransFragment context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_transaction, container, false);
        rvTrans = root.findViewById(R.id.rv_cart);
        tvCount = root.findViewById(R.id.tv_countItem);
        ivC = root.findViewById(R.id.iv_count);

        context = this;

        ivC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CartActivity.class));
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
        adapter = new TransAdapter(getActivity(), listCart);
        rvTrans.setAdapter(adapter);

        adapter.setOnItemClickListener(new TransAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                count++;
                tvCount.setText("" + count);
            }
        });
    }

    public void getProducts() {
        SharedPreferences sp = getActivity().getSharedPreferences("login", MODE_PRIVATE);
        String token = sp.getString("logged", "missing");

        AndroidNetworking.get(BaseUrl.url + "api/products")
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

                                    TransModel transModel = new TransModel();
                                    transModel.setNameProduct(item.getString("name"));
                                    transModel.setPrice("Rp. " + item.getString("price"));
                                    transModel.setStock(item.getString("stock"));
                                    transModel.setCartImage("http://127.0.0.1:8000/storage/" + item.getString("image"));
                                    listCart.add(transModel);
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