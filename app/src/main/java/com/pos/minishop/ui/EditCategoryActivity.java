package com.pos.minishop.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.pos.minishop.R;
import com.pos.minishop.baseUrl.BaseUrl;
import com.pos.minishop.model.CategoryModel;

import org.json.JSONException;
import org.json.JSONObject;

public class EditCategoryActivity extends AppCompatActivity {
    private EditText etName;
    private Button btnSubmit;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        etName = findViewById(R.id.et_name);
        btnSubmit = findViewById(R.id.btn_submit);

        progressDialog = new ProgressDialog(this);
        Intent intent = getIntent();
        CategoryModel category = intent.getParcelableExtra("Item Data");

        etName.setText(category.getName());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();

                boolean isEmpty = false;

                if (name.isEmpty()) {
                    isEmpty = true;
                    etName.setError("Required");
                }

                if (!isEmpty) {
                    progressDialog.setTitle("Loading...");
                    progressDialog.show();

                    SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                    String token = sp.getString("logged", "missing");

                    AndroidNetworking.put(BaseUrl.url + "api/product-categories/" + category.getId())
                            .addHeaders("Authorization", "Bearer " + token)
                            .addBodyParameter("name", name)
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String status = response.getString("status");

                                        if (status.equals("success")) {
                                            progressDialog.dismiss();
                                            String message = response.getString("message");

                                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), CategoryManagementActivity.class);
                                            startActivity(intent);
                                            finish();
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
        });
    }
}