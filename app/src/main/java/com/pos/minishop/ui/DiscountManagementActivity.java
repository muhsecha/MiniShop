package com.pos.minishop.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pos.minishop.R;
import com.pos.minishop.adapter.DiscountAdapter;
import com.pos.minishop.baseUrl.BaseUrl;
import com.pos.minishop.model.DiscountModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DiscountManagementActivity extends AppCompatActivity {
    private final ArrayList<DiscountModel> listDiscount = new ArrayList<>();
    private RecyclerView rvDiscount;
    private FloatingActionButton btnAddDiscount;
    private DiscountAdapter discountAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount_management);

        rvDiscount = findViewById(R.id.rv_discount);
        btnAddDiscount = findViewById(R.id.btn_add_discount);

        progressDialog = new ProgressDialog(this);
        rvDiscount.setHasFixedSize(true);
        showDiscounts();
        getDiscounts();

        btnAddDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CreateDiscountActivity.class));
            }
        });
    }

    public void showDiscounts() {
        rvDiscount.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        discountAdapter = new DiscountAdapter(listDiscount, DiscountManagementActivity.this);
        rvDiscount.setAdapter(discountAdapter);

        discountAdapter.setOnItemClickCallback(new DiscountAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(DiscountModel data) {
                Intent intent = new Intent(getApplicationContext(), EditDiscountActivity.class);
                intent.putExtra("Item Data", data);
                startActivity(intent);
            }
        });
    }

    public void getDiscounts() {
        progressDialog.setTitle("Loading...");
        progressDialog.show();

        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        String token = sp.getString("logged", "missing");

        AndroidNetworking.get(BaseUrl.url + "api/discounts")
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
                                listDiscount.clear();
                                JSONArray data = response.getJSONArray("data");

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject item = data.getJSONObject(i);

                                    DiscountModel discount = new DiscountModel();
                                    discount.setId(item.getString("id"));
                                    discount.setName(item.getString("name"));
                                    discount.setDiscount(item.getString("discount"));
                                    listDiscount.add(discount);
                                }

                                discountAdapter.notifyDataSetChanged();
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

    public void deleteDiscount(String id) {
        new AlertDialog.Builder(this)
                .setMessage("Delete ?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        progressDialog.setTitle("Loading...");
                        progressDialog.show();

                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        String token = sp.getString("logged", "missing");

                        AndroidNetworking.delete(BaseUrl.url + "api/discounts/" + id)
                                .addHeaders("Authorization", "Bearer " + token)
                                .setPriority(Priority.MEDIUM)
                                .build()
                                .getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            String status = response.getString("status");
                                            String message = response.getString("message");

                                            if (status.equals("success")) {
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
                                                getDiscounts();
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
                                        Toast.makeText(getApplication(), "error", Toast.LENGTH_SHORT).show();
                                        Integer errorCode = anError.getErrorCode();

                                        if (errorCode != 0) {
                                            if (errorCode == 401) {
                                                SharedPreferences.Editor editor = sp.edit();
                                                editor.clear();
                                                editor.apply();

                                                startActivity(new Intent(getApplication(), LoginActivity.class));
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
                }).create().show();
    }
}