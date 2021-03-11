package com.pos.minishop.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.pos.minishop.R;
import com.pos.minishop.adapter.HistoryAdapter;
import com.pos.minishop.baseUrl.BaseUrl;
import com.pos.minishop.model.HistoryModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryTransActivity extends AppCompatActivity {
    private final ArrayList<HistoryModel> listHistory = new ArrayList<>();
    private RecyclerView rvHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_trans);

        rvHistory = findViewById(R.id.rv_history);

        rvHistory.setHasFixedSize(true);
        getHistory();
    }

    public void showHistory() {
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        HistoryAdapter historyAdapter = new HistoryAdapter(listHistory);
        rvHistory.setAdapter(historyAdapter);

        historyAdapter.setOnItemClickCallback(new HistoryAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(HistoryModel data) {
                Intent intent = new Intent(getApplicationContext(), DetailTransActivity.class);
                intent.putExtra("Item Data", data);
                startActivity(intent);
            }
        });
    }

    public void getHistory() {
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
                                JSONArray data = response.getJSONArray("data");

                                String trxNumber = null;
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject item = data.getJSONObject(i);

                                    if (trxNumber == null || !trxNumber.equals(item.getString("trx_number"))) {
                                        String[] createdAt = item.getString("created_at").split(" ");
                                        String date = createdAt[0];
                                        String time = createdAt[1];

                                        HistoryModel history = new HistoryModel();
                                        history.setTime(time);
                                        history.setDate(date);
                                        history.setTotalBuy(item.getString("total"));
                                        history.setTrxNumber(item.getString("trx_number"));
                                        listHistory.add(history);

                                        trxNumber = item.getString("trx_number");
                                    }
                                }

                                showHistory();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
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