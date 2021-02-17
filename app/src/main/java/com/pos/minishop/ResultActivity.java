package com.pos.minishop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pos.minishop.ui.home.HomeFragment;
import com.pos.minishop.ui.transaksi.TransFragment;

public class ResultActivity extends AppCompatActivity {
    TextView tvHasil;
    Button btnNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        tvHasil = findViewById(R.id.tv_hasilKembalian);
        btnNew = findViewById(R.id.btn_newTransaction);

        SharedPreferences mSettings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String hasil = mSettings.getString("result", "");
        tvHasil.setText("Rp. "+hasil);


        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), TransFragment.class));
            }
        });
    }
}