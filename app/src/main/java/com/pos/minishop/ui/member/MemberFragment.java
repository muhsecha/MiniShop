package com.pos.minishop.ui.member;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.pos.minishop.MainActivity;
import com.pos.minishop.R;
import com.pos.minishop.baseUrl.BaseUrl;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class MemberFragment extends Fragment {
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    TextView datedeparture;
    private Button btnSubmit;
    private EditText etFullName, etGender, etAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_member, container, false);
        datedeparture = root.findViewById(R.id.et_datePicker);
        btnSubmit = root.findViewById(R.id.btn_submit);
        etFullName = root.findViewById(R.id.et_fullName);
        etGender = root.findViewById(R.id.et_gender);
        etAddress = root.findViewById(R.id.et_address);

        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TextView tanggal = root.findViewById(R.id.et_datePicker);
                String myFormat = "dd-MMMM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                tanggal.setText(sdf.format(myCalendar.getTime()));
            }
        };

        datedeparture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity().getApplication(), date,
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

                if (!isEmpty) {
                    SharedPreferences sp = getActivity().getSharedPreferences("login", MODE_PRIVATE);
                    String token = sp.getString("logged", "missing");

                    AndroidNetworking.post(BaseUrl.url + "members")
                        .addHeaders("Authorization", "Bearer " + token)
                        .addBodyParameter("full_name", fullName)
                        .addBodyParameter("gender", gender)
                        .addBodyParameter("address", address)
                        .addBodyParameter("dob", "2021-02-18")
                        .addBodyParameter("member_category_id", "1")
                        .setPriority(Priority.LOW)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String status = response.getString("status");
                                    String message = response.getString("message");

                                    if (status.equals("success")) {
                                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "gagal", Toast.LENGTH_SHORT).show();
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

        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Log Out");
    }
}