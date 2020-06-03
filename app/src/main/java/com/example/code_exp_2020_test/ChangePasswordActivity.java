package com.example.code_exp_2020_test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText inputEmail;
    private Button btnReset, btnBack;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        inputEmail = (EditText) findViewById(R.id.changePasswordEmailInput);
        btnReset = (Button) findViewById(R.id.changePasswordSendResetEmailButton);
        btnBack = (Button) findViewById(R.id.changePasswordBackButton);

        mAuth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();

                if (!email.equals(mAuth.getCurrentUser().getEmail())) {
                    Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ChangePasswordActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                        finish();
                                    } else {
                                        Toast.makeText(ChangePasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}