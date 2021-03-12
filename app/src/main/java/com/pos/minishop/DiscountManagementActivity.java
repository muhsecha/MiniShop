package com.pos.minishop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pos.minishop.adapter.DiscountAdapter;
import com.pos.minishop.adapter.ProductAdapter;
import com.pos.minishop.baseUrl.BaseUrl;
import com.pos.minishop.model.DiscountModel;
import com.pos.minishop.model.ProductModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DiscountManagementActivity extends AppCompatActivity {
    private RecyclerView rvDiscount;
    private ArrayList<DiscountModel> listDiscount = new ArrayList<>();
    private Button btnAddDiscount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount_management);

        rvDiscount = findViewById(R.id.rv_discount);
        btnAddDiscount = findViewById(R.id.btn_add_discount);

        rvDiscount.setHasFixedSize(true);
        getDiscounts();

        btnAddDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CreateDiscountActivity.class));
            }
        });
    }

    public void showProducts() {
        rvDiscount.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        DiscountAdapter discountAdapter = new DiscountAdapter(listDiscount, DiscountManagementActivity.this);
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