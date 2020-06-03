package com.example.code_exp_2020_test;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private static final String TAG = "FirebaseAuth";

    private EditText signupEmailInput;
    private EditText signupUsernameInput;
    private EditText signupPasswordInput;

    private Button signupButton;
    private Button goLoginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();

        signupEmailInput = findViewById(R.id.signupEmailInput);
        signupUsernameInput = findViewById(R.id.signupUsernameInput);
        signupPasswordInput = findViewById(R.id.signupPasswordInput);

        signupButton = findViewById(R.id.signupButton);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });

        goLoginButton = findViewById(R.id.goLoginButton);
        goLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        /*
        if (currentUser != null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }*/
    }

    public void createUser(){
        final String email = signupEmailInput.getText().toString();
        final String password = signupPasswordInput.getText().toString();
        final String username = signupUsernameInput.getText().toString();

        if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length() >= 8 && signupUsernameInput.length() > 0) {
            mAuth.createUserWithEmailAndPassword(signupEmailInput.getText().toString(), signupPasswordInput.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(username).build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "Username set");
                                                }
                                            }
                                        });

                                Toast.makeText(SignupActivity.this, "Registration successful! Please sign in.",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignupActivity.this, "Registration failed. Try again later.",
                                        Toast.LENGTH_LONG).show();
                            }

                            // ...
                        }
                    });
        }

        else {
            Toast.makeText(SignupActivity.this, "Please enter a valid email, a username with at least 5 characters and a password with at least 8 characters",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
