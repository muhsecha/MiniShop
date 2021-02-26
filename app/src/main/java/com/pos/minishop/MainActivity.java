package com.pos.minishop;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.navigation.NavigationView;
import com.pos.minishop.baseUrl.BaseUrl;
import com.pos.minishop.ui.home.HomeFragment;
import com.pos.minishop.ui.member.MemberFragment;
import com.pos.minishop.ui.transaksi.TransFragment;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    Fragment fragment;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(0).setChecked(true);
        Boolean fromResult = getIntent().getBooleanExtra("randomVariable", false);
        if(fromResult) firstFragmentDisplay(R.id.nav_trans);
        else firstFragmentDisplay(R.id.nav_dash);
    }

    private void firstFragmentDisplay(int itemId) {

        fragment = null;

        switch (itemId) {
            case R.id.nav_dash:
                fragment = new HomeFragment();
                break;
            case R.id.nav_trans:
                fragment = new TransFragment();
                break;
            case R.id.nav_logout:
                logout();
                break;
            case R.id.nav_member:
                fragment = new MemberFragment();
                break;
        }

        if (fragment != null) {
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fLayout, fragment);
            transaction.commit();
        }

        drawer.closeDrawers();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        firstFragmentDisplay(item.getItemId());
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    public void logout() {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("Apakah anda yakin ingin keluar ?")
                .setNegativeButton("Tidak", null)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        String token = sp.getString("logged", "missing");

                        AndroidNetworking.post(BaseUrl.url + "api/logout")
                                .addHeaders("Authorization", "Bearer " + token)
                                .setPriority(Priority.MEDIUM)
                                .build()
                                .getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            String status = response.getString("status");

                                            if (status.equals("success")) {
                                                SharedPreferences.Editor editor = sp.edit();
                                                editor.clear();
                                                editor.apply();

                                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                startActivity(intent);
                                                finish();
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
                }).create().show();
    }
}