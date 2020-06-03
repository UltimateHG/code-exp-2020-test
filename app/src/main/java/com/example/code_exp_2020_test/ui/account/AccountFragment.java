package com.example.code_exp_2020_test.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.code_exp_2020_test.BlogPost;
import com.example.code_exp_2020_test.BlogRecyclerAdapter;
import com.example.code_exp_2020_test.ChangePasswordActivity;
import com.example.code_exp_2020_test.LoginActivity;
import com.example.code_exp_2020_test.NavActivity;
import com.example.code_exp_2020_test.NavViewModel;
import com.example.code_exp_2020_test.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import static android.app.Activity.RESULT_OK;

public class AccountFragment extends Fragment {
    Button accountLogoutButton;
    Button accountChangePasswordButton;
    TextView accountUsernameText;
    TextView accountPointsText;
    FirebaseAuth mAuth;

    private SwipeRefreshLayout swipeRefreshLayout;
    private List<BlogPost> blog_list;
    private BlogRecyclerAdapter blogRecyclerAdapter;

    private boolean hasPosts = false;

    //firebase stuff
    private FirebaseFirestore firebaseFirestore;

    private AccountViewModel accountViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            return null;
        }

        accountUsernameText = (TextView)root.findViewById(R.id.accountUsernameText);
        accountPointsText = (TextView)root.findViewById(R.id.accountPointsText);

        accountUsernameText.setText(mAuth.getCurrentUser().getDisplayName());
        accountPointsText.setText("2 points");

        /**
         * POST HISTORY STUFF
         */
        blog_list = new ArrayList<>();
        RecyclerView blog_list_view = root.findViewById(R.id.yourPostsList);

        blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list);
        blog_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        blog_list_view.setAdapter(blogRecyclerAdapter);
        blog_list_view.setHasFixedSize(true);

        firebaseFirestore = FirebaseFirestore.getInstance();

        ((TextView)root.findViewById(R.id.acc_fragment_placeHolder)).setText("");

        loadPost(root);

        accountLogoutButton = (Button)root.findViewById(R.id.accountLogoutButton);
        accountLogoutButton.setOnClickListener(v -> {
            try {
                mAuth.signOut();
                Toast.makeText(getActivity(), "Logout successful.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), NavActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
            catch (Exception e){
                Toast.makeText(getActivity(), "Logout failed. Try again later", Toast.LENGTH_SHORT).show();
            }
        });

        //change profile details
        accountChangePasswordButton = (Button)root.findViewById(R.id.accountChangePasswordButton);
        //create on click listener
        accountChangePasswordButton.setOnClickListener(view -> {
            Log.d("Change","Pressed");
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });
        return root;
    }

    private void loadPost(View root) {
        List<BlogPost> blogPostList = new ArrayList<>();
        //load all posts into blogPostList
        firebaseFirestore.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot doc: task.getResult()) {
                        String blogPostId = doc.getId();
                        BlogPost blogPost = doc.toObject(BlogPost.class).withId(blogPostId);
                        blogPostList.add(blogPost);
                    }
                    String current_user_id = mAuth.getCurrentUser().getUid();
                    for(BlogPost post: blogPostList) {
                        if(post.getUser_id().equals(current_user_id)) {
                            blog_list.add(post);
                            blogRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                    if(blog_list.isEmpty()) ((TextView)root.findViewById(R.id.acc_fragment_placeHolder)).setText("You have no posts :(");
                }
            }
        });
    }
}