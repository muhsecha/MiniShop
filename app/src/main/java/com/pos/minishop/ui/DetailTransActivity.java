package com.pos.minishop.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.pos.minishop.R;
import com.pos.minishop.adapter.DetailHistoryAdapter;
import com.pos.minishop.baseUrl.BaseUrl;
import com.pos.minishop.model.HistoryModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailTransActivity extends AppCompatActivity {
    private final ArrayList<HistoryModel> listHistory = new ArrayList<>();
    private RecyclerView rvHistory;
    private TextView tvTime, tvDate, tvTrxNumber, tvTotalBuy;
    private String trxNumber;
    private Integer totalBuy = 0;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_trans);

        rvHistory = findViewById(R.id.rv_history);
        tvTime = findViewById(R.id.tv_detail_jam);
        tvDate = findViewById(R.id.tv_detail_tgl);
        tvTrxNumber = findViewById(R.id.tv_trx_number);
        tvTotalBuy = findViewById(R.id.tv_total);

        progressDialog = new ProgressDialog(this);
        Intent intent = getIntent();
        HistoryModel item = intent.getParcelableExtra("Item Data");
        trxNumber = item.getTrxNumber();
        tvTime.setText(item.getTime());
        tvDate.setText(item.getDate());
        tvTrxNumber.setText(trxNumber);

        rvHistory.setHasFixedSize(true);
        getHistory();
        showHistory();
    }

    public void showHistory() {
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        DetailHistoryAdapter detailHistoryAdapter = new DetailHistoryAdapter(listHistory);
        rvHistory.setAdapter(detailHistoryAdapter);
    }

    public void getHistory() {
        progressDialog.setTitle("Loading...");
        progressDialog.show();

        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        String token = sp.getString("logged", "missing");

        AndroidNetworking.get(BaseUrl.url + "api/transactions")
                .addHeaders("Authorization", "Bearer " + token)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");

                            if (status.equals("success")) {
                                progressDialog.dismiss();
                                JSONArray data = response.getJSONArray("data");

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject item = data.getJSONObject(i);

                                    if (trxNumber.equals(item.getString("trx_number"))) {
                                        String quantity = item.getString("quantity");

                                        JSONObject product = item.getJSONObject("product");
                                        String name = product.getString("name");
                                        String price = product.getString("price");
                                        String image = product.getString("image");

                                        Integer totalItem = Integer.valueOf(quantity) * Integer.valueOf(price);
                                        totalBuy += totalItem;

                                        HistoryModel history = new HistoryModel();
                                        history.setName(name);
                                        history.setQuantity(quantity);
                                        history.setPrice(price);
                                        history.setTotalItem(String.valueOf(totalItem));
                                        history.setImage(image);
                                        listHistory.add(history);
                                    }
                                }

                                showHistory();
                                tvTotalBuy.setText(String.valueOf(totalBuy));
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
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
}