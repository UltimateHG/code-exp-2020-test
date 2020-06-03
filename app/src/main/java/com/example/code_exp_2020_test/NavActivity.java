package com.example.code_exp_2020_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;



public class NavActivity extends AppCompatActivity {

    private NavViewModel navViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        navViewModel = new ViewModelProvider(this).get(NavViewModel.class);

        setContentView(R.layout.activity_nav);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_top,
                R.id.navigation_news,
                R.id.navigation_resource,
                R.id.navigation_account)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        if (navViewModel.getFirebaseUser().getValue() == null) {
            menu.removeItem(R.id.action_logout_btn);
        } else {
            menu.removeItem(R.id.action_login_btn);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_logout_btn:
                logout();
                return true;
            case R.id.action_login_btn:
                login();
                return true;
            default:
                return true;
        }
    }

    private void logout() {
        navViewModel.getFirebaseAuth().signOut();
        navViewModel.refreshFirebaseUser();
        Toast.makeText(this, "Logout successful.", Toast.LENGTH_SHORT).show();
        invalidateOptionsMenu();
    }

    private void login() {
        startActivity(new Intent(NavActivity.this, LoginActivity.class));
        invalidateOptionsMenu();
    }
}