package com.example.code_exp_2020_test;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    //list of posts and context
    public List<BlogPost> blog_list;
    public Context context;

    //firebase stuff
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public BlogRecyclerAdapter(List<BlogPost> blog_list) {
        this.blog_list = blog_list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //create view, set viewholder params
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //not recyclable
        holder.setIsRecyclable(false);
        //initialize variables
        final String blogPostId = blog_list.get(position).BlogPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        //set title, body, user id and username
        String title = blog_list.get(position).getTitle();
        String body = blog_list.get(position).getBody();
        holder.setTitleText(title);
        holder.setBodyText(body);

        String username = blog_list.get(position).getUsername();
        holder.setUsername(username);

        //Get timestamp and format it
        try {
            long ms = blog_list.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("dd/MM/yyyy", new Date(ms)).toString();
            holder.setTime(dateString);
        } catch (Exception e) {
            Toast.makeText(context,"Exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //Set comment count
        firebaseFirestore.collection("posts/"+blogPostId+"/comments").addSnapshotListener((queryDocumentSnapshots, e) ->  {
           if(!queryDocumentSnapshots.isEmpty()) {
               int commentCount = queryDocumentSnapshots.size();
               holder.updateCommentCount(commentCount);
           } else {
               holder.updateCommentCount(0);
           }
        });
    }

    //for getting how many items in list
    @Override
    public int getItemCount() {return blog_list.size();}

    //Custom viewholder class with helper functions
    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView blogTitle;
        private TextView blogBody;
        private TextView blogDate;
        private TextView blogUsername;
        private ImageView blogCommentBtn;
        private TextView blogCommentCount;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            blogCommentBtn = view.findViewById(R.id.blog_comment_icon);
        }

        public void setTitleText(String titleText) {
            blogTitle = view.findViewById(R.id.blog_title);
            blogTitle.setText(titleText);
        }

        public void setBodyText(String bodyText) {
            blogBody = view.findViewById(R.id.blog_body);
            blogBody.setText(bodyText);
        }

        public void setTime(String date) {
            blogDate = view.findViewById(R.id.blog_date);
            blogDate.setText(date);
        }

        public void setUsername(String username) {
            blogUsername = view.findViewById(R.id.blog_user_name);
            blogUsername.setText(username);
        }

        public void updateCommentCount(int count) {
            blogCommentCount = view.findViewById(R.id.blog_comment_count);
            blogCommentCount.setText(Integer.toString(count));
        }

    }
}
