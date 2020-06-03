package com.example.code_exp_2020_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private String current_user_id;

    private FloatingActionButton addPostBtn;

    private BottomNavigationView mainbottomNav;

    /**
     * DECLARE FRAGMENTS HERE
     */
    private HomeFragment homeFragment;
    private TopFragment topFragment;
    private AccountFragment accountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Define variables
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //Handle main menu fragment + check if user has logged out
        if(mAuth.getCurrentUser() != null) {
            mainbottomNav = findViewById(R.id.mainBottomNav);

            /**
             * INITIALIZE ALL YOUR FRAGMENTS HERE
             */
            homeFragment = new HomeFragment();
            topFragment = new TopFragment();
            accountFragment = new AccountFragment();

            initializeFragment();

            mainbottomNav.setOnNavigationItemSelectedListener((item) -> {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
                switch(item.getItemId()) {
                    case R.id.bottom_action_home:
                        replaceFragment(homeFragment, currentFragment);
                        return true;
                    case R.id.bottom_action_top:
                        replaceFragment(topFragment, currentFragment);
                        return true;
                    case R.id.bottom_action_account:
                        replaceFragment(accountFragment, currentFragment);
                        return true;
                    default:
                        return false;
                }
            });

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

        //Set toolbar name
        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setActionBar(mainToolbar);
        getSupportActionBar().setTitle("FakeMeh?");
        getSupportActionBar().hide();
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

    private void logout() {
        mAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void initializeFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //Add all transactions below and hide them
        fragmentTransaction.add(R.id.main_container, homeFragment);
        fragmentTransaction.add(R.id.main_container, topFragment);
        fragmentTransaction.add(R.id.main_container, accountFragment);
        fragmentTransaction.hide(topFragment);
        fragmentTransaction.hide(accountFragment);
        fragmentTransaction.commit();
    }

    private void replaceFragment(Fragment fragment, Fragment currentFragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //Add more ifs for more fragments
        if(fragment == homeFragment) {
            fragmentTransaction.hide(topFragment);
            fragmentTransaction.hide(accountFragment);
            getSupportActionBar().setTitle("Home");
        }
        if(fragment == topFragment) {
            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(accountFragment);
            getSupportActionBar().setTitle("Top Posts");
        }
        if(fragment == accountFragment) {
            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(topFragment);
            getSupportActionBar().setTitle("Account");
        }

        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onResume() {super.onResume();}

    @Override
    public void onStop() {
        super.onStop();
        if(authStateListener!=null) mAuth.removeAuthStateListener(authStateListener);
    }
}
