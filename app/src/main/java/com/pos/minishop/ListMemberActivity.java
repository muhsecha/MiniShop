package com.pos.minishop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.pos.minishop.model.ListMemberModel;
import com.pos.minishop.model.ProductModel;

import java.util.ArrayList;

public class ListMemberActivity extends AppCompatActivity {
    private RecyclerView rvlistmember;
    private ArrayList<ListMemberModel> listMember = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_member);
        rvlistmember = findViewById(R.id.rv_listmember);
    }
}