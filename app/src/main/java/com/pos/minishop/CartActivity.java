package com.pos.minishop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.pos.minishop.adapter.CartAdapter;
import com.pos.minishop.baseUrl.BaseUrl;
import com.pos.minishop.model.DiscountModel;
import com.pos.minishop.model.MemberCategoryModel;
import com.pos.minishop.model.MemberModel;
import com.pos.minishop.model.ProductCategoryModel;
import com.pos.minishop.model.TransModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    Button btnSubmit;
    TextView tvtotal, tvFinalPrice;
    Bundle bundle;
    int finalPrice = 0;
    private ArrayList<TransModel> productArray;
    RecyclerView recyclerView;
    CartAdapter adapter;
    private ArrayList<DiscountModel> listDiscount = new ArrayList<>();
    private ArrayList<MemberModel> listMember = new ArrayList<>();
    private Spinner spinnerDiscounts, spinnerMembers;
    private String idDiscount = null, idMember = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.rv_list_cart);
        tvtotal = findViewById(R.id.tv_total);
        tvFinalPrice = findViewById(R.id.tv_cart_final_price);
        btnSubmit = findViewById(R.id.btn_submitCart);
        spinnerDiscounts = findViewById(R.id.spinner_discounts);
        spinnerMembers = findViewById(R.id.spinner_members);

        bundle = getIntent().getExtras();

        if (bundle != null) {
            productArray = new ArrayList<>();
            productArray = (ArrayList<TransModel>) getIntent().getSerializableExtra("productArray");
            addData();
            hitung();
        }
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CartActivity.this, "submit", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CartActivity.this, ChargeActivity.class);
                intent.putExtra("productArray", productArray);
                intent.putExtra("finalPrice", finalPrice);
                intent.putExtra("member_id", idMember);
                intent.putExtra("discount_id", idDiscount);
                startActivity(intent);
            }
        });

        getDiscounts();
        getMembers();
    }

    private void addData() {
        adapter = new CartAdapter(CartActivity.this, productArray);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void hitung(){
        productArray = new ArrayList<>();
        productArray = (ArrayList<TransModel>) getIntent().getSerializableExtra("productArray");
        for (int i = 0;i < productArray.size();i++){
            finalPrice += productArray.get(i).getAmount() * productArray.get(i).getPriceInt();
        }
        tvFinalPrice.setText("TOTAL : " + String.valueOf(finalPrice));
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
                                JSONArray data = response.getJSONArray("data");

                                DiscountModel discountDefault = new DiscountModel();
                                discountDefault.setName("Choose discount");
                                listDiscount.add(discountDefault);

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject item = data.getJSONObject(i);

                                    DiscountModel discount = new DiscountModel();
                                    discount.setId(item.getString("id"));
                                    discount.setName(item.getString("name"));
                                    listDiscount.add(discount);
                                }

                                showDiscoutns();
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

    public void showDiscoutns() {
        ArrayAdapter<DiscountModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listDiscount);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDiscounts.setAdapter(adapter);
        spinnerDiscounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DiscountModel discount = (DiscountModel) parent.getSelectedItem();

                if (!discount.getName().equals("Choose discount")) {
                    idDiscount = discount.getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void getMembers() {
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        String token = sp.getString("logged", "missing");

        AndroidNetworking.get(BaseUrl.url + "api/members")
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

                                MemberModel memberDefault = new MemberModel();
                                memberDefault.setName("Choose member");
                                listMember.add(memberDefault);

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject item = data.getJSONObject(i);

                                    MemberModel member = new MemberModel();
                                    member.setId(item.getString("id"));
                                    member.setName(item.getString("full_name"));
                                    listMember.add(member);
                                }

                                showMembers();
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

    public void showMembers() {
        ArrayAdapter<MemberModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listMember);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMembers.setAdapter(adapter);
        spinnerMembers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MemberModel member = (MemberModel) parent.getSelectedItem();

                if (!member.getName().equals("Choose member")) {
                    idMember = member.getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}