package com.pos.minishop.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private final ArrayList<ProductModel> listProduct = new ArrayList<>();
    private RecyclerView rvProduct;
    private Button btnAddProduct;
    private ProgressDialog progressDialog;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_management);
        rvProduct = findViewById(R.id.rv_product);
        btnAddProduct = findViewById(R.id.btn_addProduct_home);

        progressDialog = new ProgressDialog(this);
        rvProduct.setHasFixedSize(true);
        showProducts();
        getProducts();

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), InputProductActivity.class));
            }
        });
    }

    public void showProducts() {
        rvProduct.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        productAdapter = new ProductAdapter(listProduct);
        rvProduct.setAdapter(productAdapter);

        productAdapter.setOnItemClickCallback(new ProductAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(ProductModel data) {
                Intent intent = new Intent(getApplicationContext(), EditProductActivity.class);
                intent.putExtra("Item Data", data);
                startActivity(intent);
            }
        });
    }

    public void getProducts() {
        progressDialog.setTitle("Loading...");
        progressDialog.show();

        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
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
                                progressDialog.dismiss();
                                JSONArray data = response.getJSONArray("data");

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject item = data.getJSONObject(i);

                                    ProductModel product = new ProductModel();
                                    product.setId(item.getString("id"));
                                    product.setProductCategoryId(item.getString("product_category_id"));
                                    product.setName(item.getString("name"));
                                    product.setPrice(item.getString("price"));
                                    product.setStock(item.getString("stock"));
                                    product.setDesc(item.getString("desc"));
                                    product.setImage(item.getString("image"));
                                    listProduct.add(product);
                                }

                                productAdapter.notifyDataSetChanged();
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