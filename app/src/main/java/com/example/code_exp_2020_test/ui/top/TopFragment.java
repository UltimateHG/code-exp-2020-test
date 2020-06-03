package com.example.code_exp_2020_test.ui.top;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.code_exp_2020_test.BlogPost;
import com.example.code_exp_2020_test.BlogRecyclerAdapter;
import com.example.code_exp_2020_test.LoginActivity;
import com.example.code_exp_2020_test.NavViewModel;
import com.example.code_exp_2020_test.NewPostActivity;
import com.example.code_exp_2020_test.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TopFragment extends Fragment {

    private NavViewModel navViewModel;

    //variables
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<BlogPost> blog_list;
    private BlogRecyclerAdapter blogRecyclerAdapter;
    private Boolean isFirstPageFirstLoad = true;

    //firebase stuff
    private FirebaseFirestore firebaseFirestore;
    private DocumentSnapshot lastVisible;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        navViewModel = new ViewModelProvider(requireActivity()).get(NavViewModel.class);
        View view = inflater.inflate(R.layout.fragment_top, container, false);
        setupViews(view, container);

        return view;
    }

    private void setupViews(View view, ViewGroup container) {
        //define basic variables
        blog_list = new ArrayList<>();
        RecyclerView blog_list_view = view.findViewById(R.id.blog_list_view_top);

        //init variables
        blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list);
        blog_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        blog_list_view.setAdapter(blogRecyclerAdapter);
        blog_list_view.setHasFixedSize(true);

        firebaseFirestore = FirebaseFirestore.getInstance();

        loadPost();

        //Handle add post button
        FloatingActionButton addPostBtn = view.findViewById(R.id.add_post_btn);
        addPostBtn.setOnClickListener((v) -> {
            if (navViewModel.getFirebaseUser().getValue() != null) {
                Intent newPostIntent = new Intent(requireActivity(), NewPostActivity.class);
                startActivity(newPostIntent);
            } else {
                startActivity(new Intent(requireActivity(), LoginActivity.class));
            }
        });

        swipeRefreshLayout = view.findViewById(R.id.swiperefresh_top);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadPost();
            swipeRefreshLayout.setRefreshing(false);
        });
    }


    private void loadPost() {
        isFirstPageFirstLoad = true;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        Date fromdate = cal.getTime();

        Query query = firebaseFirestore.collection("posts").orderBy("commentCount", Query.Direction.DESCENDING).orderBy("timestamp", Query.Direction.DESCENDING);
        query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            try {
                if(queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    int tIndex = 0;
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (((Timestamp) documentSnapshot.get("timestamp")).toDate().compareTo(fromdate) < 0) {
                            break;
                        }
                        tIndex++;
                    }
                    //if first page and first load, set top document to latest one
                    if (isFirstPageFirstLoad) {
                        lastVisible = queryDocumentSnapshots.getDocuments().get(tIndex - 1);
                        blog_list.clear();
                    }

                    if (tIndex != 0) {
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            //updating stuff for when document change occurs
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String blogPostId = doc.getDocument().getId();
                                BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                                if (isFirstPageFirstLoad) blog_list.add(blogPost);
                                else blog_list.add(0, blogPost);

                                blogRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                        //loaded once alr, set this to false
                        isFirstPageFirstLoad = false;
                    }
                }
            } catch(NullPointerException e1) {
                Log.d("error", e1.toString());
            }
        });
    }
}