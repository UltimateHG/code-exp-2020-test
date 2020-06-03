package com.example.code_exp_2020_test;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {
    //fragment initialization
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

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

    public HomeFragment() {}

    /**
     * Create new instance of this fragment with param1 and param2
     */
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        getActivity().getActionBar().setTitle("Home");
    }

    @Override
    public void onStop() {
        super.onStop();
        registration.remove();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

            Query query = firebaseFirestore.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(3);
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

    //for loading more posts after reaching bottom on scroll
    public void loadMorePost() {
        //if user is authed
        if(firebaseAuth.getCurrentUser() != null) {
            //query after lastVisible
            Query nextQuery = firebaseFirestore.collection("posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
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
