package com.example.code_exp_2020_test.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.code_exp_2020_test.BlogPost;
import com.example.code_exp_2020_test.BlogRecyclerAdapter;
import com.example.code_exp_2020_test.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private List<BlogPost> blog_list;
    private BlogRecyclerAdapter blogRecyclerAdapter;
    private Boolean isFirstPageFirstLoad = true;

    //firebase stuff
    private FirebaseFirestore firebaseFirestore;
    private DocumentSnapshot lastVisible;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        //define basic variables
        blog_list = new ArrayList<>();
        //variables
        RecyclerView blog_list_view = view.findViewById(R.id.blog_list_view);

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

        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadPost();
            swipeRefreshLayout.setRefreshing(false);
        });

        //inflate layout for this fragment
        return view;
    }

    private void loadPost() {
        isFirstPageFirstLoad = true;
        Query query = firebaseFirestore.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(3);
        query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            try {
                if(queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    //if first page and first load, set top document to latest one
                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                    blog_list.clear();

                    for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()) {
                        //updating stuff for when document change occurs
                        if(doc.getType() == DocumentChange.Type.ADDED) {
                            String blogPostId = doc.getDocument().getId();
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                            blog_list.add(blogPost);

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
    private void loadMorePost() {
        //query after lastVisible
        Query nextQuery = firebaseFirestore.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);
        nextQuery.addSnapshotListener((EventListener<QuerySnapshot>) (queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w("MyDebug", "Listen failed.", e);
                return;
            }

            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
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