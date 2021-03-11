package com.pos.minishop.ui.member;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.pos.minishop.R;
import com.pos.minishop.adapter.MemberAdapter;
import com.pos.minishop.baseUrl.BaseUrl;
import com.pos.minishop.model.MemberModel;
import com.pos.minishop.ui.CreateMemberActivity;
import com.pos.minishop.ui.EditMemberActivity;
import com.pos.minishop.ui.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class MemberFragment extends Fragment {
    private final ArrayList<MemberModel> listMember = new ArrayList<>();
    private Button btnAdd;
    private RecyclerView rvMember;
    private MemberAdapter memberAdapter;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_member, container, false);
        btnAdd = root.findViewById(R.id.btn_add);
        rvMember = root.findViewById(R.id.rv_member);

        progressDialog = new ProgressDialog(getContext());
        rvMember.setHasFixedSize(true);
        showMembers();
        getMembers();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CreateMemberActivity.class));
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Member");
    }

    public void showMembers() {
        rvMember.setLayoutManager(new LinearLayoutManager(getContext()));
        memberAdapter = new MemberAdapter(listMember);
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
        progressDialog.setTitle("Loading...");
        progressDialog.show();

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
                                progressDialog.dismiss();
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

                                memberAdapter.notifyDataSetChanged();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                        Integer errorCode = anError.getErrorCode();

                        if (errorCode != 0) {
                            if (errorCode == 401) {
                                SharedPreferences.Editor editor = sp.edit();
                                editor.clear();
                                editor.apply();

                                startActivity(new Intent(getContext(), LoginActivity.class));
                            }

                            Log.d("TAG", "onError errorCode : " + anError.getErrorCode());
                            Log.d("TAG", "onError errorBody : " + anError.getErrorBody());
                            Log.d("TAG", "onError errorDetail : " + anError.getErrorDetail());
                        } else {
                            Log.d("TAG", "onError errorDetail : " + anError.getErrorDetail());
                        }
                    }
                });
    }
}