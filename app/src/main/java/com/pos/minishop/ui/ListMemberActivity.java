package com.pos.minishop.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.pos.minishop.R;
import com.pos.minishop.model.ListMemberModel;

import java.util.ArrayList;

public class ListMemberActivity extends AppCompatActivity {
    private final ArrayList<ListMemberModel> listMember = new ArrayList<>();
    private RecyclerView rvlistmember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_member);
        rvlistmember = findViewById(R.id.rv_listmember);
    }
}