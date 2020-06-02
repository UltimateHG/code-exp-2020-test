package com.example.code_exp_2020_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends Activity {

    private Toolbar mainToolbar;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private String current_user_id;

    private FloatingActionButton addPostBtn;

    private BottomNavigationView mainbottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Define variables
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //Set toolbar name
        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setActionBar(mainToolbar);

        getActionBar().setTitle("Fake Meh?");

        //Handle main menu fragment + check if user has logged out
        if(mAuth.getCurrentUser() != null) {
            mainbottomNav = findViewById(R.id.mainBottomNav);

            /**Fragment stuff, to be added later over here
             *
             *
             */

            //Auth state listener, on auth state change, check if user is now null, then go back to LoginActivity
            authStateListener = (FirebaseAuth.AuthStateListener) (firebaseAuth) -> {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            };

            //Handle add post button
            addPostBtn = findViewById(R.id.add_post_btn);
            addPostBtn.setOnClickListener((v) -> {
                Intent newPostIntent = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(newPostIntent);
            });
        }
    }

}
