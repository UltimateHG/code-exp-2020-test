package com.example.code_exp_2020_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DebugMainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextView textView;
    private Button logoutButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_main);
        mAuth = FirebaseAuth.getInstance();

        textView = findViewById(R.id.textView3);

        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mAuth.signOut();
                    Toast.makeText(DebugMainActivity.this, "Logout successful.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
                catch (Exception e){
                    Toast.makeText(DebugMainActivity.this, "Logout failed. Try again later", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        textView.setText(currentUser.getDisplayName() + " " + currentUser.getUid() + " " + currentUser.getIdToken(true) + " " + currentUser.getEmail());
    }
}
