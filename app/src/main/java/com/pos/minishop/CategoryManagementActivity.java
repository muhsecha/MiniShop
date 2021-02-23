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

        getCategories();

        btnplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etInput.getText().toString().trim();

                boolean isEmpty = false;

                if (name.isEmpty()) {
                    isEmpty = true;
                    etInput.setError("Required");
                }

                if (!isEmpty) {
                    SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                    String token = sp.getString("logged", "missing");

                    AndroidNetworking.post(BaseUrl.url + "api/product-categories")
                            .addHeaders("Authorization", "Bearer " + token)
                            .addBodyParameter("name", name)
                            .setPriority(Priority.LOW)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String status = response.getString("status");
                                        String message = response.getString("message");

                                        if (status.equals("success")) {
                                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                            getCategories();
                                            etInput.setText("");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                                    Log.d("TAG", "onError: " + anError.getErrorDetail());
                                    Log.d("TAG", "onError: " + anError.getErrorBody());
                                    Log.d("TAG", "onError: " + anError.getErrorCode());
                                    Log.d("TAG", "onError: " + anError.getResponse());
                                }
                            });
                }
            }
        });

    }
    public void showCategories() {
        rvCategory.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        CategoryAdapter categoryAdapter = new CategoryAdapter(listCategory, CategoryManagementActivity.this);
        rvCategory.setAdapter(categoryAdapter);

        categoryAdapter.setOnItemClickCallback(new CategoryAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(CategoryModel data) {
                Intent intent = new Intent(getApplicationContext(), EditCategoryActivity.class);
                intent.putExtra("Item Data", data);
                startActivity(intent);
            }
        });
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
                                listCategory.clear();
                                JSONArray data = response.getJSONArray("data");

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject item = data.getJSONObject(i);

                                    CategoryModel category = new CategoryModel();
                                    category.setId(item.getString("id"));
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