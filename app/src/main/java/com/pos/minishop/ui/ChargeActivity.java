package com.pos.minishop.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.pos.minishop.model.TransModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChargeActivity extends AppCompatActivity {
    int finalPrice;
    int input;
    private EditText etCharge;
    private TextView bayar;
    private Button btnSubmit;
    private Bundle bundle;
    private ArrayList<TransModel> productArray;
    private String idDiscount = null, idMember = null;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);

        etCharge = findViewById(R.id.et_charge);
        bayar = findViewById(R.id.tv_bayar);
        btnSubmit = findViewById(R.id.btn_submit_charge);

        progressDialog = new ProgressDialog(this);
        bundle = getIntent().getExtras();

        if (bundle != null) {
            productArray = new ArrayList<>();
            productArray = (ArrayList<TransModel>) getIntent().getSerializableExtra("productArray");


            finalPrice = getIntent().getIntExtra("finalPrice", 0);
            idDiscount = getIntent().getStringExtra("discount_id");
            idMember = getIntent().getStringExtra("member_id");
            if (TextUtils.isEmpty(idMember)) idMember = null;
            if (TextUtils.isEmpty(idDiscount)) idDiscount = null;
            bayar.setText("Rp. " + finalPrice);
            etCharge.setText("" + finalPrice);
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });
    }

    private void validate() {
        Log.d("SOY", "1");
        SharedPreferences sp = getApplicationContext().getSharedPreferences("login", MODE_PRIVATE);
        String token = sp.getString("logged", "missing");
        String user_id = sp.getString("user_id", "missing");

        int chargeInput = Integer.parseInt(etCharge.getText().toString());
        if (chargeInput < finalPrice) {
            Log.d("SOY", "2");
            Toast.makeText(ChargeActivity.this, "Uangnya kurang", Toast.LENGTH_SHORT).show();
        } else {
            try {
                Log.d("SOY", "3");
                input = Integer.parseInt(etCharge.getText().toString());
                int hitung = (input - finalPrice);
                if (hitung < 0) {
                    Toast.makeText(this, "Eror count", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences mResult = getSharedPreferences("RESULT", MODE_PRIVATE);
                    SharedPreferences.Editor editor = mResult.edit();
                    editor.putInt("hasil", hitung);
                    editor.apply();
                }

                JSONObject transaction = new JSONObject();
                JSONArray transactionArray = new JSONArray();
                productArray = new ArrayList<>();
                productArray = (ArrayList<TransModel>) getIntent().getSerializableExtra("productArray");
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i < productArray.size(); ++i) {
                                JSONObject transactionArrayData = new JSONObject();
                                transactionArrayData.put("product_id", Integer.parseInt(productArray.get(i).getProductId()));
                                transactionArrayData.put("quantity", productArray.get(i).getAmount());
                                transactionArrayData.put("total", productArray.get(i).getPriceInt());
                                transactionArray.put(i, transactionArrayData);
                            }
                        } catch (Exception e) {
                            Log.d("error", e.toString());
                        }
                    }
                };
                runnable.run();
                transaction.put("user_id", user_id);
                transaction.put("transaction", transactionArray);
                Log.d("SOY", transaction.toString());

                progressDialog.setTitle("Loading...");
                progressDialog.show();

                AndroidNetworking.post(BaseUrl.url + "api/transactions")
                        .addHeaders("Authorization", "Bearer " + token)
                        .addBodyParameter("member_id", idMember)
                        .addBodyParameter("discount_id", idDiscount)
                        .addJSONObjectBody(transaction)
                        .setContentType("application/json")
                        .setPriority(Priority.LOW)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String status = response.getString("status");
                                    Log.d("hasil", "onResponse: " + status);
                                    Toast.makeText(ChargeActivity.this, status, Toast.LENGTH_SHORT).show();
                                    if (status.equalsIgnoreCase("success")) {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(getApplicationContext(), ResultActivity.class));
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
                                Toast.makeText(getApplicationContext(), "error toast", Toast.LENGTH_LONG).show();
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
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}