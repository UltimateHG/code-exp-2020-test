package com.example.code_exp_2020_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity {

    private Toolbar newPostToolbar;
    private EditText newPostTitle, newPostBody;
    private Button newPostButton;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        //Initialize variables
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = firebaseAuth.getInstance();

        //Get current user id
        current_user_id = firebaseAuth.getCurrentUser().getUid();

        //Set toolbar name and layout
        //newPostToolbar = findViewById(R.id.new_post_toolbar);
        //setActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Write New Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialize new post fields and post button
        newPostTitle = findViewById(R.id.new_post_title);
        newPostBody = findViewById(R.id.new_post_body);
        newPostButton = findViewById(R.id.post_btn);

        //Handle new post button
        newPostButton.setOnClickListener((v) -> {
            final String title = newPostTitle.getText().toString();
            final String body = newPostBody.getText().toString();

            //Place post details onto a hashmap
            Map<String, Object> postMap = new HashMap<>();
            postMap.put("title", title);
            postMap.put("body", body);
            postMap.put("user_id",current_user_id);
            postMap.put("username", firebaseAuth.getCurrentUser().getDisplayName());
            postMap.put("status", "unverified");
            postMap.put("timestamp", FieldValue.serverTimestamp());
            postMap.put("commentCount", 0);

            //Store hashmap into firebase
            firebaseFirestore.collection("posts").add(postMap).addOnCompleteListener((task) -> {
                if(task.isSuccessful()) {
                    //If successful, tell user successful post and redirect back to main page.
                    Toast.makeText(NewPostActivity.this, "Your post has been successfully posted!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    //handle exceptions later, placeholder toast here
                    Toast.makeText(NewPostActivity.this, "Your post is unable to be posted! Please make sure you are connected to the internet!", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

}
