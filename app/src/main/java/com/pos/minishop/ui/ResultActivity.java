package com.pos.minishop.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.pos.minishop.R;

public class ResultActivity extends AppCompatActivity {
    TextView tvHasil;
    Button btnNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        tvHasil = findViewById(R.id.tv_hasilKembalian);
        btnNew = findViewById(R.id.btn_newTransaction);

        SharedPreferences mResult = getSharedPreferences("RESULT", MODE_PRIVATE);
        int result = mResult.getInt("hasil", 0);
        SharedPreferences.Editor editor = mResult.edit();
        editor.clear();
        tvHasil.setText("Rp. " + result);

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("randomVariable", true);
                startActivity(intent);
            }
        });
    }
}