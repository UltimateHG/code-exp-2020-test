package com.example.code_exp_2020_test;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.ViewHolder> {

    //list of posts and context
    public List<NewsPost> news_list;
    public Context context;

    public NewsRecyclerAdapter(List<NewsPost> news_list) {
        this.news_list = news_list;
    }

    @Override
    public NewsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //create view, set viewholder params
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    public void onBindViewHolder(final NewsRecyclerAdapter.ViewHolder holder, int position) {
        //not recyclable
        holder.setIsRecyclable(false);
        //initialize variables
        final String blogPostId = news_list.get(position).NewsPostId;


        //set title and link
        String title = news_list.get(position).getTitle();
        String body = news_list.get(position).getLink();
        holder.setTitleText(title);
        holder.setLinkText(body);


    }


    @Override
    public int getItemCount() {
        try{return news_list.size();}
        catch (NullPointerException e){
            return 0; //lol good programming kappa
        }
    }

    //Custom viewholder class with helper functions
    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView newsTitle;
        private TextView newsBody;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setTitleText(String titleText) {
            newsTitle = view.findViewById(R.id.news_title);
            newsTitle.setText(titleText);
        }

        public void setLinkText(String bodyText) {
            newsBody = view.findViewById(R.id.news_link);
            newsBody.setText(bodyText);

            //supposedly allows hyperlink, might need to edit string
            newsBody.setMovementMethod(LinkMovementMethod.getInstance());
        }

    }
}
