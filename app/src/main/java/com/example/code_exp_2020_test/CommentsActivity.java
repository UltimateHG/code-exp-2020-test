package com.example.code_exp_2020_test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
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
    private ImageButton comment_post_fake_btn;
    private ImageButton comment_post_real_btn;
    private RecyclerView comment_list;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private List<Comments> commentsList;

    //firebase
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private ListenerRegistration registration;

    //blog and user ids
    private String blog_post_id;
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
        commentQuery.addSnapshotListener(this, (queryDocumentSnapshots, e) -> {
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
        });

        //if user is not logged in
        if(firebaseAuth.getCurrentUser() == null || firebaseAuth.getCurrentUser().getEmail()==null) {
            comment_post_fake_btn.setEnabled(false);
            comment_post_real_btn.setEnabled(false);
            comment_field.setFocusable(false);
            comment_field.setFocusableInTouchMode(false);
            comment_field.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(MotionEvent.ACTION_UP == motionEvent.getAction()) {
                        sendToLogin();
                    }
                    return false;
                }
            });
        }

        //posting comment and declare post to be real
        comment_post_real_btn.setOnClickListener((v) -> {
            String comment = comment_field.getText().toString();
            //if comment field is empty, do not continue
            if(comment == null || comment.isEmpty() || comment.equals("") || comment.trim().equals("")) {
                Toast.makeText(this, "Enter something into the comment field first!", Toast.LENGTH_SHORT).show();
            } else {
                //put it to a map
                Map<String, Object> commentsMap = new HashMap<>();
                commentsMap.put("comment", comment);
                commentsMap.put("username", firebaseAuth.getCurrentUser().getDisplayName());
                commentsMap.put("user_id", firebaseAuth.getCurrentUser().getUid());
                commentsMap.put("timestamp", FieldValue.serverTimestamp());
                commentsMap.put("status", "real");

                firebaseFirestore.collection("posts/" + blog_post_id + "/comments").add(commentsMap).addOnCompleteListener((task) -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(CommentsActivity.this, "Error posting comment: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        comment_field.setText("");
                    }
                });

                firebaseFirestore.collection("posts").document(blog_post_id).update("commentCount", Integer.parseInt(getIntent().getStringExtra("commentCount")) + 1);

                if (firebaseAuth.getCurrentUser().getUid().equals("oiaGsEC4bjeCgRfZEmMruNEL7kZ2")) {
                    firebaseFirestore.collection("posts").document(blog_post_id).update("status", "real");
                }
            }
        });

        //posting comment and declare post to be fake
        comment_post_fake_btn.setOnClickListener((v) -> {
            String comment = comment_field.getText().toString();
            //if comment field is empty, do not continue
            if(comment == null || comment.isEmpty() || comment.equals("") || comment.trim().equals("")) {
                Toast.makeText(this, "Enter something into the comment field first!", Toast.LENGTH_SHORT).show();
            } else {
                //put it to a map
                Map<String, Object> commentsMap = new HashMap<>();
                commentsMap.put("comment", comment);
                commentsMap.put("username", firebaseAuth.getCurrentUser().getDisplayName());
                commentsMap.put("user_id", firebaseAuth.getCurrentUser().getUid());
                commentsMap.put("timestamp", FieldValue.serverTimestamp());
                commentsMap.put("status", "fake");

                firebaseFirestore.collection("posts/" + blog_post_id + "/comments").add(commentsMap).addOnCompleteListener((task) -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(CommentsActivity.this, "Error posting comment: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        comment_field.setText("");
                    }
                });

                firebaseFirestore.collection("posts").document(blog_post_id).update("commentCount", Integer.parseInt(getIntent().getStringExtra("commentCount")) + 1);

                if (firebaseAuth.getCurrentUser().getUid().equals("oiaGsEC4bjeCgRfZEmMruNEL7kZ2")) {
                    firebaseFirestore.collection("posts").document(blog_post_id).update("status", "fake");
                }
            }
        });

        checkUserCommented();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private static final int LOGIN_ACTIVITY_REQUEST_CODE = 0;

    private void sendToLogin() {
        startActivityForResult(new Intent(CommentsActivity.this, LoginActivity.class), LOGIN_ACTIVITY_REQUEST_CODE);
    }

    private void updateUserLogin() {
        //if user not logged in
        if (firebaseAuth.getCurrentUser() == null || firebaseAuth.getCurrentUser().getEmail() == null) {
            comment_post_fake_btn.setEnabled(false);
            comment_post_real_btn.setEnabled(false);
            comment_field.setFocusable(false);
            comment_field.setFocusableInTouchMode(false);
            comment_field.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                        sendToLogin();
                    }
                    return false;
                }
            });
        }

        //if user logged in
        if(firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().getEmail() != null) {
            comment_post_fake_btn.setEnabled(true);
            comment_post_real_btn.setEnabled(true);
            comment_field.setFocusableInTouchMode(true);
            comment_field.setFocusable(true);
            //set ontouchlistener to do nothing
            comment_field.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return false;
                }
            });
        }

        checkUserCommented();
    }

    private void checkUserCommented() {
        if(firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().getEmail() != null) {
            List<Comments> commentList = new ArrayList<>();
            //load all posts into blogPostList
            firebaseFirestore.collection("posts/"+blog_post_id+"/comments").orderBy("timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for(QueryDocumentSnapshot doc: task.getResult()) {
                            Comments comments = doc.toObject(Comments.class);
                            commentList.add(comments);
                        }
                        String current_user_id = firebaseAuth.getCurrentUser().getUid();
                        for(Comments comment: commentList) {
                            if(!comment.getUser_id().equals(current_user_id)) {
                                continue;
                            }
                            comment_field.setFocusable(false);
                            comment_field.setFocusableInTouchMode(false);
                        }
                    }
                }
            });
        }
    }

    // This method is called when the second activity finishes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that it is the SecondActivity with an OK result
        if (requestCode == LOGIN_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Login successful.", Toast.LENGTH_SHORT).show();
                updateUserLogin();
            }
        }
    }
}
