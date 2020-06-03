package com.example.code_exp_2020_test;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

public class NavViewModel extends ViewModel {

    private MutableLiveData<FirebaseAuth> mAuth;

    public NavViewModel() {
        mAuth = new MutableLiveData<>();
        mAuth.setValue(FirebaseAuth.getInstance());
    }

    public LiveData<FirebaseAuth> getFirebaseAuth() {
        return mAuth;
    }
}