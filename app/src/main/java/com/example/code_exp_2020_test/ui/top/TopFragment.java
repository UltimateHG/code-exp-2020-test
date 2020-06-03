package com.example.code_exp_2020_test.ui.top;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.code_exp_2020_test.R;

public class TopFragment extends Fragment {

    private TopViewModel topViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        topViewModel =
                ViewModelProviders.of(this).get(TopViewModel.class);
        View root = inflater.inflate(R.layout.fragment_top, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        topViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}