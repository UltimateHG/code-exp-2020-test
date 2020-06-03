package com.example.code_exp_2020_test;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private static final String TAG = "FirebaseAuth";

    private EditText loginEmailInput;
    private EditText loginPasswordInput;

    private Button goSignupButton;
    private Button loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        try {
            if (!TextUtils.isEmpty(user.getDisplayName())) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }
        catch(Exception e){

        }

        loginEmailInput = findViewById(R.id.loginEmailInput);
        loginPasswordInput = findViewById(R.id.loginPasswordInput);

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        goSignupButton = findViewById(R.id.goSignupButton);
        goSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    public void loginUser(){
        final String email = loginEmailInput.getText().toString();
        final String password = loginPasswordInput.getText().toString();

        Log.d("Email", email);
        Log.d("Password", password);

        if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length() >= 8) {
            mAuth.signInWithEmailAndPassword(loginEmailInput.getText().toString(), loginPasswordInput.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "Successful login");

                                Toast.makeText(LoginActivity.this, "Logged in!",
                                        Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "Login failed", task.getException());
                                Toast.makeText(LoginActivity.this, "Login failed...",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else {
            Toast.makeText(LoginActivity.this, "Enter a valid email and password",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
