package com.pos.minishop.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pos.minishop.CategoryManagementActivity;
import com.pos.minishop.DiscountManagementActivity;
import com.pos.minishop.HistoryTransActivity;
import com.pos.minishop.ProductManagementActivity;
import com.pos.minishop.R;

public class HomeFragment extends Fragment {

    LinearLayout lnP, lnC, lnD, lnH;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        lnP = root.findViewById(R.id.ln_product);
        lnC = root.findViewById(R.id.ln_category);
        lnD = root.findViewById(R.id.ln_discount);
        lnH = root.findViewById(R.id.ln_history);

        lnP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ProductManagementActivity.class));
            }
        });
        lnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CategoryManagementActivity.class));
            }
        });
        lnD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), DiscountManagementActivity.class));
            }
        });

        lnH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), HistoryTransActivity.class));
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Home");
    }
}