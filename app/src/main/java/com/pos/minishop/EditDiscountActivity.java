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
import com.pos.minishop.model.DiscountModel;
import com.pos.minishop.model.ProductModel;

import org.json.JSONException;
import org.json.JSONObject;

public class EditDiscountActivity extends AppCompatActivity {
    private EditText etName, etDiscount;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_discount);

        etName = findViewById(R.id.et_name);
        etDiscount = findViewById(R.id.et_discount);
        btnSubmit = findViewById(R.id.btn_submit);

        Intent intent = getIntent();
        DiscountModel discountModel = intent.getParcelableExtra("Item Data");

        etName.setText(discountModel.getName());
        etDiscount.setText(discountModel.getDiscount());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String discount = etDiscount.getText().toString().trim();

                boolean isEmpty = false;

                if (name.isEmpty()) {
                    isEmpty = true;
                    etName.setError("Required");
                }

                if (discount.isEmpty()) {
                    isEmpty = true;
                    etDiscount.setError("Required");
                }

                if (!isEmpty) {
                    SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                    String token = sp.getString("logged", "missing");

                    AndroidNetworking.put(BaseUrl.url + "api/discounts/" + discountModel.getId())
                            .addHeaders("Authorization", "Bearer " + token)
                            .addBodyParameter("name", name)
                            .addBodyParameter("discount", discount)
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String status = response.getString("status");
                                        String message = response.getString("message");

                                        if (status.equals("success")) {
                                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), DiscountManagementActivity.class);
                                            startActivity(intent);
                                            finish();
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
}