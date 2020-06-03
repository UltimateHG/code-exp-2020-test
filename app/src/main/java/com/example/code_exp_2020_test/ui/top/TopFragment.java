package com.example.code_exp_2020_test.ui.top;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.code_exp_2020_test.BlogPost;
import com.example.code_exp_2020_test.BlogRecyclerAdapter;
import com.example.code_exp_2020_test.R;
import com.example.code_exp_2020_test.ui.home.HomeViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TopFragment extends Fragment {

    private HomeViewModel homeViewModel;

    //variables
    private RecyclerView blog_list_view;
    private List<BlogPost> blog_list;
    private BlogRecyclerAdapter blogRecyclerAdapter;
    private Boolean isFirstPageFirstLoad = true;

    //firebase stuff
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private DocumentSnapshot lastVisible;
    private ListenerRegistration registration;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        //define basic variables
        blog_list = new ArrayList<>();
        blog_list_view = view.findViewById(R.id.blog_list_view);

        firebaseAuth = FirebaseAuth.getInstance();

        //init variables
        blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list);
        blog_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        blog_list_view.setAdapter(blogRecyclerAdapter);
        blog_list_view.setHasFixedSize(true);

        //set recyclerview items
        if(firebaseAuth.getCurrentUser() != null) {
            firebaseFirestore = FirebaseFirestore.getInstance();

            blog_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);
                    if(reachedBottom){
                        loadMorePost();
                    }
                }
            });

            Query query = firebaseFirestore.collection("posts").orderBy("commentCount", Query.Direction.DESCENDING).limit(3);
            registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    try {
                        if(!queryDocumentSnapshots.isEmpty()) {
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
                }
            });
            /*query.addSnapshotListener(getActivity(), (queryDocumentSnapshots, e) -> {
               try {
                   if(!queryDocumentSnapshots.isEmpty()) {
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
            });*/
        }

        //inflate layout for this fragment
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        registration.remove();
    }

    //for loading more posts after reaching bottom on scroll
    public void loadMorePost() {
        //if user is authed
        if(firebaseAuth.getCurrentUser() != null) {
            //query after lastVisible
            Query nextQuery = firebaseFirestore.collection("posts")
                    .orderBy("commentCount", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(3);

            nextQuery.addSnapshotListener(getActivity(), (queryDocumentSnapshots, e) -> {
                if(!queryDocumentSnapshots.isEmpty()) {
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
}