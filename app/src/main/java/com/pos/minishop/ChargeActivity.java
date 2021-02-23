package com.pos.minishop;

import androidx.appcompat.app.AppCompatActivity;

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
import com.pos.minishop.baseUrl.BaseUrl;
import com.pos.minishop.model.TransModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChargeActivity extends AppCompatActivity {
    EditText etCharge;
    Button btnSubmit;
    Bundle bundle;
    private ArrayList<TransModel> productArray;
    int finalPrice;
    private String idDiscount = null, idMember = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);

        etCharge = findViewById(R.id.et_charge);
        btnSubmit = findViewById(R.id.btn_submit_charge);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        bundle = getIntent().getExtras();

        if (bundle != null) {
            productArray = new ArrayList<>();
            productArray = (ArrayList<TransModel>) getIntent().getSerializableExtra("productArray");


            finalPrice = getIntent().getIntExtra("finalPrice", 0);
            idDiscount = getIntent().getStringExtra("discount_id");
            idMember = getIntent().getStringExtra("member_id");
            etCharge.setText(""+finalPrice);
        }
    }

    private void validate() {
        SharedPreferences sp = getApplicationContext().getSharedPreferences("login", MODE_PRIVATE);
        String token = sp.getString("logged", "missing");
        String user_id = sp.getString("user_id", "missing");

        int chargeInput = Integer.parseInt(etCharge.getText().toString());
        if(chargeInput < finalPrice) {
            Toast.makeText(ChargeActivity.this, "Uangnya kurang", Toast.LENGTH_SHORT).show();
        } else {
            try {
                JSONObject transaction = new JSONObject();
                JSONObject transactionArrayData = new JSONObject();
                JSONArray transactionArray= new JSONArray();
                productArray = new ArrayList<>();
                productArray = (ArrayList<TransModel>) getIntent().getSerializableExtra("productArray");
                for (int i = 0;i < productArray.size(); ++i){
                    transactionArrayData.put("product_id", Integer.parseInt(productArray.get(i).getProductId()));
                    transactionArrayData.put("quantity", productArray.get(i).getAmount());
                    transactionArrayData.put("total", 10000);
                    transactionArrayData.put("member_id", 1);
                    transactionArrayData.put("discount", 0);
                    transactionArray.put(i, transactionArrayData);
                }
                transaction.put("user_id", user_id);
                transaction.put("transaction", transactionArray);
                Log.d("SOY", transaction.toString());

                AndroidNetworking.post(BaseUrl.url + "api/transactions")
                        .addHeaders("Content-Type", "application/json")
                        .addHeaders("Authorization", "Bearer " + token)
                        .addBodyParameter("user_id", transaction.getString("user_id"))
                        .addBodyParameter("member_id", idMember)
                        .addBodyParameter("discount_id", idDiscount)
                        .addJSONObjectBody(transaction)
                        .setPriority(Priority.LOW)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String status = response.getString("status");
                                    Toast.makeText(ChargeActivity.this, status, Toast.LENGTH_SHORT).show();
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
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}