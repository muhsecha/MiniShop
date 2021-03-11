package com.pos.minishop.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.pos.minishop.R;
import com.pos.minishop.baseUrl.BaseUrl;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    TextView tv_reg;
    Button btn_login;
    EditText etEmail, etPass;
    SharedPreferences sp;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tv_reg = findViewById(R.id.tv_register);
        btn_login = findViewById(R.id.btn_login);
        etEmail = findViewById(R.id.et_Email_Login);
        etPass = findViewById(R.id.et_Password_Login);

        progressDialog = new ProgressDialog(this);

        sp = getSharedPreferences("login", MODE_PRIVATE);
        sp.edit().putString("logged", sp.getString("logged", "missing")).apply();

        String user = sp.getString("logged", "missing");

        if (!user.equals("missing")) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        tv_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String password = etPass.getText().toString();
                progressDialog.setTitle("Logging In...");
                progressDialog.show();

                AndroidNetworking.post(BaseUrl.url + "api/login")
                        .addBodyParameter("email", email)
                        .addBodyParameter("password", password)
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
                                        sp.edit().putString("user_id", data.getString("id")).apply();

                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                        progressDialog.dismiss();
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
                                    Log.d("TAG", "onError errorCode : " + anError.getErrorCode());
                                    Log.d("TAG", "onError errorBody : " + anError.getErrorBody());
                                    Log.d("TAG", "onError errorDetail : " + anError.getErrorDetail());
                                } else {
                                    Log.d("TAG", "onError errorDetail : " + anError.getErrorDetail());
                                }
                            }
                        });
            }
        });
    }
}