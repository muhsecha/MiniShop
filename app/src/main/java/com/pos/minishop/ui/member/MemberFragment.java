package com.pos.minishop.ui.member;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.pos.minishop.CreateMemberActivity;
import com.pos.minishop.EditMemberActivity;
import com.pos.minishop.EditProductActivity;
import com.pos.minishop.InputProductActivity;
import com.pos.minishop.MainActivity;
import com.pos.minishop.R;
import com.pos.minishop.adapter.MemberAdapter;
import com.pos.minishop.baseUrl.BaseUrl;
import com.pos.minishop.model.MemberCategoryModel;
import com.pos.minishop.model.MemberModel;
import com.pos.minishop.model.ProductModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class MemberFragment extends Fragment {
    private Button btnAdd;
    private RecyclerView rvMember;
    private ArrayList<MemberModel> listMember = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_member, container, false);
        btnAdd = root.findViewById(R.id.btn_add);
        rvMember = root.findViewById(R.id.rv_member);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CreateMemberActivity.class));
            }
        });

        rvMember.setHasFixedSize(true);
        getMembers();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Member");
    }

    public void showMembers() {
        rvMember.setLayoutManager(new LinearLayoutManager(getContext()));
        MemberAdapter memberAdapter = new MemberAdapter(listMember);
        rvMember.setAdapter(memberAdapter);

        memberAdapter.setOnItemClickCallback(new MemberAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(MemberModel data) {
                Intent intent = new Intent(getContext(), EditMemberActivity.class);
                intent.putExtra("Item Data", data);
                startActivity(intent);
            }
        });
    }

    public void getMembers() {
        SharedPreferences sp = getActivity().getSharedPreferences("login", MODE_PRIVATE);
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

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject item = data.getJSONObject(i);

                                    MemberModel member = new MemberModel();
                                    member.setId(item.getString("id"));
                                    member.setName(item.getString("full_name"));
                                    member.setAddress(item.getString("address"));
                                    member.setDate(item.getString("dob"));
                                    member.setGender(item.getString("gender"));
                                    member.setMemberCategoryId(item.getString("member_category_id"));
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
}