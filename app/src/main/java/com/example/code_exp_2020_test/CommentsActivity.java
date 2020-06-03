package com.example.code_exp_2020_test;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
    //post stuff
    private TextView comment_post_user_name;
    private TextView comment_post_title;
    private TextView comment_post_status;
    private TextView comment_post_body;
    private TextView comment_post_date;

    //view stuff
    private Toolbar commentToolbar;
    private EditText comment_field;
    private ImageView comment_post_fake_btn;
    private ImageView comment_post_real_btn;
    private RecyclerView comment_list;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private List<Comments> commentsList;

    //firebase
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private ListenerRegistration registration;

    //blog and user ids
    private String blog_post_id;
    private String blog_post_user_id;
    private String blog_post_username;
    private String blog_post_status;
    private String blog_post_title;
    private String blog_post_date;
    private String blog_post_body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        getSupportActionBar().setTitle("Comments");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        blog_post_user_id = firebaseAuth.getCurrentUser().getUid();
        blog_post_id = getIntent().getStringExtra("blog_post_id");
        blog_post_username = getIntent().getStringExtra("username");
        blog_post_status = getIntent().getStringExtra("status");
        blog_post_date = getIntent().getStringExtra("date");
        blog_post_title = getIntent().getStringExtra("title");
        blog_post_body = getIntent().getStringExtra("body");

        comment_post_user_name = findViewById(R.id.comment_post_user_name);
        comment_post_user_name.setText(blog_post_username);
        comment_post_status = findViewById(R.id.comment_post_status);
        comment_post_status.setText(blog_post_status);
        comment_post_date = findViewById(R.id.comment_post_date);
        comment_post_date.setText(blog_post_date);
        comment_post_title = findViewById(R.id.comment_post_title);
        comment_post_title.setText(blog_post_title);
        comment_post_body = findViewById(R.id.comment_post_body);
        comment_post_body.setText(blog_post_body);

        comment_field = findViewById(R.id.comment_field);
        comment_post_fake_btn = findViewById(R.id.comment_post_fake_btn);
        comment_post_real_btn = findViewById(R.id.comment_post_real_btn);
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

        comment_post_real_btn.setOnClickListener((v) -> {
            String comment = comment_field.getText().toString();
            //put it to a map
            Map<String, Object> commentsMap = new HashMap<>();
            commentsMap.put("comment", comment);
            commentsMap.put("username", firebaseAuth.getCurrentUser().getDisplayName());
            commentsMap.put("user_id", firebaseAuth.getCurrentUser().getUid());
            commentsMap.put("timestamp", FieldValue.serverTimestamp());
            commentsMap.put("status", "real");

            firebaseFirestore.collection("posts/"+blog_post_id+"/comments").add(commentsMap).addOnCompleteListener((task) -> {
                if(!task.isSuccessful()) {
                    Toast.makeText(CommentsActivity.this,"Error posting comment: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    comment_field.setText("");
                }
            });

            firebaseFirestore.collection("posts").document(blog_post_id).update("commentCount", Integer.parseInt(getIntent().getStringExtra("commentCount"))+1);

            if (firebaseAuth.getCurrentUser().getUid().equals("oiaGsEC4bjeCgRfZEmMruNEL7kZ2")) {
                firebaseFirestore.collection("posts").document(blog_post_id).update("status", "real");
            }
        });

        //handle onclick for comment button
        comment_post_fake_btn.setOnClickListener((v) -> {
            String comment = comment_field.getText().toString();
            //put it to a map
            Map<String, Object> commentsMap = new HashMap<>();
            commentsMap.put("comment", comment);
            commentsMap.put("username", firebaseAuth.getCurrentUser().getDisplayName());
            commentsMap.put("user_id", firebaseAuth.getCurrentUser().getUid());
            commentsMap.put("timestamp", FieldValue.serverTimestamp());
            commentsMap.put("status", "fake");

            firebaseFirestore.collection("posts/"+blog_post_id+"/comments").add(commentsMap).addOnCompleteListener((task) -> {
                if(!task.isSuccessful()) {
                    Toast.makeText(CommentsActivity.this,"Error posting comment: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    comment_field.setText("");
                }
            });

            firebaseFirestore.collection("posts").document(blog_post_id).update("commentCount", Integer.parseInt(getIntent().getStringExtra("commentCount"))+1);

            if (firebaseAuth.getCurrentUser().getUid().equals("oiaGsEC4bjeCgRfZEmMruNEL7kZ2")) {
                firebaseFirestore.collection("posts").document(blog_post_id).update("status", "fake");
            }

        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        registration.remove();
    }
}
