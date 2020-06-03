package com.example.code_exp_2020_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class NavActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private String current_user_id;

    private FloatingActionButton addPostBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_top, R.id.navigation_account)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //Define variables
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //Handle main menu fragment + check if user has logged out
        if(mAuth.getCurrentUser() != null) {


            //Auth state listener, on auth state change, check if user is now null, then go back to LoginActivity
            authStateListener = (FirebaseAuth.AuthStateListener) (firebaseAuth) -> {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(NavActivity.this, LoginActivity.class));
                    finish();
                }
            };

            //Handle add post button
            addPostBtn = findViewById(R.id.add_post_btn);
            addPostBtn.setOnClickListener((v) -> {
                Intent newPostIntent = new Intent(NavActivity.this, NewPostActivity.class);
                startActivity(newPostIntent);
            });
        }

        //Set toolbar name
//        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
//        setActionBar(mainToolbar);
        getSupportActionBar().setTitle("FakeMeh?");
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_logout_btn:
                logout();
                return true;
            case R.id.action_settings_btn:
                return true;
            default:
                return true;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(authStateListener!=null) mAuth.removeAuthStateListener(authStateListener);
    }

    private void logout() {
        mAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {
        Intent intent = new Intent(NavActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}