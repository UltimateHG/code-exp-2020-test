package com.example.code_exp_2020_test;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;


public class AccountFragment extends Fragment {

    Button accountLogoutButton;
    Button accountChangePasswordButton;
    TextView accountUsernameText;
    TextView accountPointsText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FirebaseAuth mAuth;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        mAuth = FirebaseAuth.getInstance();

        accountUsernameText = (TextView)rootView.findViewById(R.id.accountUsernameText);
        accountPointsText = (TextView)rootView.findViewById(R.id.accountPointsText);

        accountUsernameText.setText(mAuth.getCurrentUser().getDisplayName());
        accountPointsText.setText("2 points");

        accountLogoutButton = (Button)rootView.findViewById(R.id.accountLogoutButton);
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
        accountChangePasswordButton = (Button)rootView.findViewById(R.id.accountChangePasswordButton);
        //create on click listener
        accountChangePasswordButton.setOnClickListener(view -> {
            Log.d("Change","Pressed");
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });
        return rootView;
    }
}