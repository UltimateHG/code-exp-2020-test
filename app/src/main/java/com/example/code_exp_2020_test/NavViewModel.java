package com.example.code_exp_2020_test;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NavViewModel extends ViewModel {

    private MutableLiveData<FirebaseUser> mUser;
    private FirebaseAuth mAuth;

    public NavViewModel() {
        mAuth = FirebaseAuth.getInstance();
        mUser = new MutableLiveData<>();
        mUser.setValue(mAuth.getCurrentUser());
    }

    public LiveData<FirebaseUser> getFirebaseUser() {
        return mUser;
    }
    public void refreshFirebaseUser() { mUser.setValue(mAuth.getCurrentUser()); }
    public FirebaseAuth getFirebaseAuth() { return mAuth; }
}