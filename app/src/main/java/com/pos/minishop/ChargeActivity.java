package com.pos.minishop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.pos.minishop.model.TransModel;

import java.util.ArrayList;

public class ChargeActivity extends AppCompatActivity {
    EditText etCharge;
    Bundle bundle;
    private ArrayList<TransModel> productArray;
    int finalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);

        etCharge = findViewById(R.id.et_charge);

        bundle = getIntent().getExtras();

        if (bundle != null) {
            productArray = new ArrayList<>();
            productArray = (ArrayList<TransModel>) getIntent().getSerializableExtra("productArray");

            finalPrice = getIntent().getIntExtra("finalPrice", 0);
            etCharge.setText(""+finalPrice);
        }
    }
}