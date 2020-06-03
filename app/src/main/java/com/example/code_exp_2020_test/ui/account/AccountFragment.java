package com.example.code_exp_2020_test.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.code_exp_2020_test.ChangePasswordActivity;
import com.example.code_exp_2020_test.LoginActivity;
import com.example.code_exp_2020_test.R;
import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {
    Button accountLogoutButton;
    Button accountChangePasswordButton;
    TextView accountUsernameText;
    TextView accountPointsText;
    FirebaseAuth mAuth;

    private AccountViewModel accountViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        accountViewModel =
                ViewModelProviders.of(this).get(AccountViewModel.class);
        View root = inflater.inflate(R.layout.fragment_account, container, false);
        mAuth = FirebaseAuth.getInstance();

        accountUsernameText = (TextView)root.findViewById(R.id.accountUsernameText);
        accountPointsText = (TextView)root.findViewById(R.id.accountPointsText);

        accountUsernameText.setText(mAuth.getCurrentUser().getDisplayName());
        accountPointsText.setText("2 points");

        accountLogoutButton = (Button)root.findViewById(R.id.accountLogoutButton);
        accountLogoutButton.setOnClickListener(v -> {
            try {
                mAuth.signOut();
                Toast.makeText(getActivity(), "Logout successful.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().finish();
                startActivity(intent);
            }
            catch (Exception e){
                Toast.makeText(getActivity(), "Logout failed. Try again later", Toast.LENGTH_SHORT).show();
            }
        });

        //change profile details
        accountChangePasswordButton = (Button)root.findViewById(R.id.accountChangePasswordButton);
        //create on click listener
        accountChangePasswordButton.setOnClickListener(view -> {
            Log.d("Change","Pressed");
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });
        return root;
    }
}