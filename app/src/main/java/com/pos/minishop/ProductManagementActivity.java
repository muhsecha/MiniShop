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
import com.pos.minishop.R;
import com.pos.minishop.adapter.ProductAdapter;
import com.pos.minishop.baseUrl.BaseUrl;
import com.pos.minishop.model.ProductModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductManagementActivity extends AppCompatActivity {

    private RecyclerView rvProduct;
    private ArrayList<ProductModel> listProduct = new ArrayList<>();
    private Button btnAddProduct;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_management);
        rvProduct = findViewById(R.id.rv_product);
        btnAddProduct = findViewById(R.id.btn_addProduct_home);

        rvProduct.setHasFixedSize(true);
        getProducts();

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),InputProductActivity.class));
            }
        });
    }


    public void showProducts() {
        rvProduct.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ProductAdapter productAdapter = new ProductAdapter(listProduct);
        rvProduct.setAdapter(productAdapter);
    }

    public void getProducts() {
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
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

                                    ProductModel product = new ProductModel();
                                    product.setName(item.getString("name"));
                                    product.setPrice(item.getString("price"));
                                    product.setStock(item.getString("stock"));
                                    product.setImage("http://127.0.0.1:8000/storage/" + item.getString("image"));
                                    listProduct.add(product);
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