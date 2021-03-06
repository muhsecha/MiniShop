package com.pos.minishop.ui.transaksi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.pos.minishop.R;
import com.pos.minishop.adapter.TransAdapter;
import com.pos.minishop.baseUrl.BaseUrl;
import com.pos.minishop.model.TransModel;
import com.pos.minishop.ui.CartActivity;
import com.pos.minishop.ui.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static io.realm.Realm.getApplicationContext;

public class TransFragment extends Fragment implements Serializable {

    final static int PRODUCT_REQUEST = 100;
    final static int PRODUCT_RESULT = 101;
    private final ArrayList<TransModel> listCart = new ArrayList<>();
    int count;
    Boolean exist = false;
    RecyclerView rvTrans;
    ProgressDialog progressDialog;
    TextView tvCount;
    ImageView ivC;
    TransFragment context;
    private ArrayList<TransModel> productArray;
    private TransAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_transaction, container, false);
        rvTrans = root.findViewById(R.id.rv_cart);
        tvCount = root.findViewById(R.id.tv_countItem);
        ivC = root.findViewById(R.id.iv_count);

        progressDialog = new ProgressDialog(getContext());

        context = this;

        ivC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<TransModel> productArray = new ArrayList<>();

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < listCart.size(); i++) {
                            if (listCart.get(i).getAmount() > 0) {
                                productArray.add(listCart.get(i));
                                exist = true;
                            }
                        }
                    }
                };
                runnable.run();
                if (exist) {
                    Intent intent = new Intent(getActivity(), CartActivity.class);
                    intent.putExtra("productArray", productArray);
                    Log.d("productArray", productArray.toString());
                    startActivityForResult(intent, PRODUCT_REQUEST);
                }
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

        progressDialog.setTitle("Getting Data...");
        progressDialog.show();

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
                                    transModel.setProductId(item.getString("id"));
                                    transModel.setNameProduct(item.getString("name"));
                                    transModel.setPrice("Rp. " + item.getString("price"));
                                    transModel.setPriceInt(item.getInt("price"));
                                    transModel.setStock(item.getString("stock"));
                                    transModel.setCartImage(item.getString("image"));
                                    listCart.add(transModel);
                                    progressDialog.dismiss();
                                }

                                showProducts();
                            } else {
                                Toast.makeText(getApplicationContext(), "gagal", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        Integer errorCode = anError.getErrorCode();

                        if (errorCode != 0) {
                            if (errorCode == 401) {
                                SharedPreferences.Editor editor = sp.edit();
                                editor.clear();
                                editor.apply();

                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PRODUCT_RESULT) {
//            String total = data.getStringExtra("total");
//            String nama = data.getStringExtra("nama");
            productArray = new ArrayList<>();
            productArray = (ArrayList<TransModel>) data.getSerializableExtra("productArray");

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < listCart.size(); i++) {
                        listCart.get(i).setAmount(0);
                        for (int j = 0; j < productArray.size(); j++) {
                            if (listCart.get(i).getNameProduct().equals(productArray.get(j).getNameProduct())) {
                                listCart.get(i).setAmount(productArray.get(j).getAmount());
                            }
                        }
                    }
                }
            };
            runnable.run();
            adapter.notifyDataSetChanged();
        }
    }
}