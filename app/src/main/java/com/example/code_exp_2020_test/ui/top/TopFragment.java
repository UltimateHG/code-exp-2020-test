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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
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

        blog_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean reachedBottom = !recyclerView.canScrollVertically(1);
                if(reachedBottom){
                    loadMorePost();
                }
            }
        });

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
        Query query = firebaseFirestore.collection("posts").orderBy("commentCount", Query.Direction.DESCENDING).limit(3);
        query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            try {
                if(queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    //if first page and first load, set top document to latest one
                    if(isFirstPageFirstLoad) {
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                        blog_list.clear();
                    }

                    for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()) {
                        //updating stuff for when document change occurs
                        if(doc.getType() == DocumentChange.Type.ADDED) {
                            String blogPostId = doc.getDocument().getId();
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                            if(isFirstPageFirstLoad) blog_list.add(blogPost);
                            else blog_list.add(0, blogPost);

                            blogRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                    //loaded once alr, set this to false
                    isFirstPageFirstLoad = false;
                }
            } catch(NullPointerException e1) {
                Log.d("error", e1.toString());
            }
        });
    }
    //for loading more posts after reaching bottom on scroll
    public void loadMorePost() {
        //query after lastVisible
        Query nextQuery = firebaseFirestore.collection("posts")
                .orderBy("commentCount", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);

        nextQuery.addSnapshotListener((EventListener<QuerySnapshot>) (queryDocumentSnapshots, e) -> {
            if(queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);

                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()) {
                    if(doc.getType() == DocumentChange.Type.ADDED) {
                        String blogPostId = doc.getDocument().getId();
                        BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                        blog_list.add(blogPost);

                        blogRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}