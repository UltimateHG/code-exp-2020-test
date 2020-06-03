package com.example.code_exp_2020_test;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.graphics.Typeface.BOLD;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    public List<Comments> commentsList;
    public Context context;

    public FirebaseUser user;

    public CommentsRecyclerAdapter(List<Comments> commentsList) {
        this.commentsList = commentsList;
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public CommentsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
        context = parent.getContext();
        return new CommentsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentsRecyclerAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        String comment = commentsList.get(position).getComment();
        holder.setComment(comment);
        holder.setUsername(commentsList.get(position).getUsername());
        holder.setStatus(commentsList.get(position).getStatus());
        holder.setUserID(commentsList.get(position).getUser_id()); //THIS MUST BE CALLED LAST
    }

    @Override
    public int getItemCount() {
        if(commentsList != null) return commentsList.size();
        else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView comment;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setComment(String message) {
            comment = view.findViewById(R.id.comment_message);
            comment.setText(message);
        }

        public void setUsername(String username) {
            comment = view.findViewById(R.id.comment_username);
            comment.setText(username);
        }

        public void setStatus(String status) {
            comment = view.findViewById(R.id.comment_status);
            switch (status){
                case "real":
                    comment.setTextColor(Color.GREEN);
                    comment.setText("thinks it's " + status);
                    break;
                case "fake":
                    comment.setTextColor(Color.RED);
                    comment.setText("thinks it's " + status);
                    break;
                default:
                    comment.setTextColor(Color.GRAY);
                    comment.setText("thinks it's confusing...");
            }
        }

        public void setUserID(String userID){
            if (userID.equals("oiaGsEC4bjeCgRfZEmMruNEL7kZ2")) {
                comment = view.findViewById(R.id.comment_message);
                comment.setTypeface(null, BOLD);
                comment = view.findViewById(R.id.comment_username);
                comment.setText(comment.getText() + " (admin)");
                comment.setTypeface(null, BOLD);
            }
        }
    }
}
