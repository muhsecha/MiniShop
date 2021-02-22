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
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.pos.minishop.adapter.CategoryAdapter;
import com.pos.minishop.baseUrl.BaseUrl;
import com.pos.minishop.model.CategoryModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryManagementActivity extends AppCompatActivity {
    private RecyclerView rvCategory;
    private ArrayList<CategoryModel> listCategory = new ArrayList<>();
    EditText etInput;
    Button btnplus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);

        etInput = findViewById(R.id.et_addCategory);
        btnplus = findViewById(R.id.btn_plus_category);
        rvCategory = findViewById(R.id.rv_category);
        rvCategory.setHasFixedSize(true);


        btnplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("masuk", "onClick: ");
                String category = etInput.getText().toString();
                AndroidNetworking.post(BaseUrl.url + "api/product-categories")
                        .addBodyParameter("name",category)
                        .setPriority(Priority.LOW)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String status = response.getString("status");

                                    if (status.equals("success")) {
                                        JSONObject data = response.getJSONObject("data");

                                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                                        sp.edit().putString("logged", data.getString("api_token")).apply();
                                        Toast.makeText(CategoryManagementActivity.this, "Success Add Category", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(getApplicationContext(), "gagal", Toast.LENGTH_SHORT).show();
//                                        progressDialog.dismiss();
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
        });

    }
    public void showCategories() {
        rvCategory.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        CategoryAdapter categoryAdapter = new CategoryAdapter(listCategory);
        rvCategory.setAdapter(categoryAdapter);
    }

    public void getCategories() {
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        String token = sp.getString("logged", "missing");

        AndroidNetworking.get(BaseUrl.url + "api/product-categories")
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

                                    CategoryModel category = new CategoryModel();
                                    category.setName(item.getString("name"));
                                    listCategory.add(category);
                                }

                                showCategories();
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