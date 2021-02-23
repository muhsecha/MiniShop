package com.pos.minishop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pos.minishop.adapter.CartAdapter;
import com.pos.minishop.adapter.TransAdapter;
import com.pos.minishop.model.TransModel;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    Button btnSubmit;
    TextView tvtotal;
    Bundle bundle;
    private ArrayList<TransModel> productArray;
    RecyclerView recyclerView;
    CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.rv_list_cart);
        tvtotal = findViewById(R.id.tv_total);
        btnSubmit = findViewById(R.id.btn_submitCart);

        bundle = getIntent().getExtras();

        if (bundle != null) {
            productArray = new ArrayList<>();
            productArray = (ArrayList<TransModel>) getIntent().getSerializableExtra("productArray");
            addData();
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CartActivity.this, "submit", Toast.LENGTH_SHORT).show();
            }
        });
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
}