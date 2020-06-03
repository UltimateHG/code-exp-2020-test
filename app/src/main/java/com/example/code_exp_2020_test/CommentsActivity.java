package com.example.code_exp_2020_test;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CommentsActivity extends AppCompatActivity {
    //view stuff
    private Toolbar commentToolbar;
    private EditText comment_field;
    private ImageView comment_post_btn;
    private RecyclerView comment_list;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private List<Comments> commentsList;

    //firebase
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private ListenerRegistration registration;

    //blog and user ids
    private String blog_post_id;
    private String current_user_id;
    private String current_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        getSupportActionBar().setTitle("Comments");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();
        blog_post_id = getIntent().getStringExtra("blog_post_id");
        current_username = getIntent().getStringExtra("username");

        comment_field = findViewById(R.id.comment_field);
        comment_post_btn = findViewById(R.id.comment_post_btn);
        comment_list = findViewById(R.id.comment_list);

        //RecyclerView Firebase List
        commentsList = new ArrayList<>();
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsList);
        comment_list.setHasFixedSize(true);
        comment_list.setLayoutManager(new LinearLayoutManager(this));
        comment_list.setAdapter(commentsRecyclerAdapter);

        //snapshot listener for comments
        Query commentQuery = firebaseFirestore.collection("posts/"+blog_post_id+"/comments");
        registration = commentQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                try {
                    if(!queryDocumentSnapshots.isEmpty()) {
                        for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()) {
                            if(doc.getType() == DocumentChange.Type.ADDED) {
                                String commentId = doc.getDocument().getId();
                                Comments comments = doc.getDocument().toObject(Comments.class);
                                commentsList.add(comments);
                                commentsRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                } catch(NullPointerException e1) {
                    Log.d("error", e1.toString());
                }
            }
        });

        //handle onclick for comment button
        comment_post_btn.setOnClickListener((v) -> {
            String comment = comment_field.getText().toString();
            //put it to a map
            Map<String, Object> commentsMap = new HashMap<>();
            commentsMap.put("comment", comment);
            commentsMap.put("username", current_username);
            commentsMap.put("user_id", current_user_id);
            commentsMap.put("timestamp", FieldValue.serverTimestamp());

            firebaseFirestore.collection("posts/"+blog_post_id+"/comments").add(commentsMap).addOnCompleteListener((task) -> {
                if(!task.isSuccessful()) {
                    Toast.makeText(CommentsActivity.this,"Error posting comment: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    comment_field.setText("");
                }
            });

            firebaseFirestore.collection("posts").document(blog_post_id).update("commentCount", Integer.parseInt(getIntent().getStringExtra("commentCount"))+1);
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        registration.remove();
    }
}
