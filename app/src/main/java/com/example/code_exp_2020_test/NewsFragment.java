package com.example.code_exp_2020_test;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {

    private Context mContext;
    private List<NewsPost> newsList;
    private NewsRecyclerAdapter newsRecyclerAdapter;
    private RecyclerView news_list_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_news, container, false);

        //init variables
        newsRecyclerAdapter = new NewsRecyclerAdapter(newsList);
        news_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        news_list_view.setAdapter(newsRecyclerAdapter);
        news_list_view.setHasFixedSize(true);


        //where news is the Pair thing
        //smth smth create an list

        newsList = new ArrayList<>();
        Pair<String,String> news;

        for(Pair<String,String> temp : news){

        }

        return fragment;
    }


}
