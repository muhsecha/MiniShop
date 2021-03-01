package com.pos.minishop;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.pos.minishop.baseUrl.BaseUrl;
import com.pos.minishop.model.MemberCategoryModel;
import com.pos.minishop.model.MemberModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class EditMemberActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;
    private Button btnSubmit;
    private EditText etFullName, etGender, etAddress, etDate;
    private Spinner spinnerMemberCategory;
    private ArrayList<MemberCategoryModel> listMemberCategory = new ArrayList<>();
    private String idMemberCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_member);

        btnSubmit = findViewById(R.id.btn_submit);
        etFullName = findViewById(R.id.et_fullName);
        etGender = findViewById(R.id.et_gender);
        etAddress = findViewById(R.id.et_address);
        etDate = findViewById(R.id.et_datePicker);
        spinnerMemberCategory = findViewById(R.id.spinner_member_category);

        Intent intent = getIntent();
        MemberModel item = intent.getParcelableExtra("Item Data");
        idMemberCategory = item.getMemberCategoryId();
        etFullName.setText(item.getName());
        etGender.setText(item.getGender());
        etAddress.setText(item.getAddress());
        etDate.setText(item.getDate());

        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                etDate.setText(sdf.format(myCalendar.getTime()));
            }
        };

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditMemberActivity.this, date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = etFullName.getText().toString().trim();
                String gender = etGender.getText().toString().trim();
                String address = etAddress.getText().toString().trim();
                String date = etDate.getText().toString().trim();

                boolean isEmpty = false;

                if (fullName.isEmpty()) {
                    isEmpty = true;
                    etFullName.setError("Required");
                }

                if (gender.isEmpty()) {
                    isEmpty = true;
                    etGender.setError("Required");
                }

                if (address.isEmpty()) {
                    isEmpty = true;
                    etAddress.setError("Required");
                }

                if (address.isEmpty()) {
                    isEmpty = true;
                    etAddress.setError("Required");
                }

                if (date.isEmpty()) {
                    isEmpty = true;
                    etDate.setError("Required");
                }

                if (!isEmpty) {
                    SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                    String token = sp.getString("logged", "missing");

                    AndroidNetworking.put(BaseUrl.url + "api/members/" + item.getId())
                            .addHeaders("Authorization", "Bearer " + token)
                            .addBodyParameter("full_name", fullName)
                            .addBodyParameter("gender", gender)
                            .addBodyParameter("address", address)
                            .addBodyParameter("dob", date)
                            .addBodyParameter("member_category_id", idMemberCategory)
                            .setPriority(Priority.LOW)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String status = response.getString("status");
                                        String message = response.getString("message");

                                        if (status.equals("success")) {
                                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "gagal", Toast.LENGTH_SHORT).show();
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
        });

        getMemberCategories();
    }

    public void showMemberCategory() {
        ArrayAdapter<MemberCategoryModel> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, listMemberCategory);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMemberCategory.setAdapter(adapter);
        spinnerMemberCategory.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        MemberCategoryModel memberCategory = (MemberCategoryModel) parent.getSelectedItem();
        idMemberCategory = memberCategory.getId();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void getMemberCategories() {
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        String token = sp.getString("logged", "missing");

        AndroidNetworking.get(BaseUrl.url + "api/member-categories")
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

                                    MemberCategoryModel memberCategory = new MemberCategoryModel();
                                    memberCategory.setId(item.getString("id"));
                                    memberCategory.setName(item.getString("name"));
                                    listMemberCategory.add(memberCategory);
                                }

                                showMemberCategory();
                                if (idMemberCategory != null) {
                                    for(int i = 1; i < listMemberCategory.size(); i++) {
                                        String id = listMemberCategory.get(i).getId();
                                        if(id.equals(idMemberCategory)) {
                                            spinnerMemberCategory.setSelection(i);
                                        }
                                    }
                                }
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