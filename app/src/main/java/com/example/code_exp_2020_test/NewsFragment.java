package com.example.code_exp_2020_test;

import androidx.fragment.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {
    //fragment initialization
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private Context mContext;
    private List<NewsPost> newsList;
    private NewsRecyclerAdapter newsRecyclerAdapter;
    private RecyclerView news_list_view;

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
    }


    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);


        newsList = new ArrayList<>();
        news_list_view = view.findViewById(R.id.news_list_view);

        //init variables
        newsRecyclerAdapter = new NewsRecyclerAdapter(newsList);
        news_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        news_list_view.setAdapter(newsRecyclerAdapter);
        news_list_view.setHasFixedSize(true);


        //where news is the Pair thing
        //smth smth create an list
        newsList.add(new NewsPost("Title", "<a href=\"\"https://google.com/\">Link text</a>").withId("1"));
        newsList.add(new NewsPost("Title", "Hyperlink2").withId("12"));
        newsList.add(new NewsPost("Title", "Hyperlink3").withId("13"));
        newsList.add(new NewsPost("Title", "Hyperlink4").withId("14"));
        newsList.add(new NewsPost("Title", "Hyperlink5").withId("15"));
        newsList.add(new NewsPost("Title", "Hyperlink6").withId("16"));
        newsList.add(new NewsPost("Title", "Hyperlink7").withId("17"));
        newsList.add(new NewsPost("Title", "Hyperlink").withId("1"));
        newsList.add(new NewsPost("Title", "Hyperlink2").withId("12"));
        newsList.add(new NewsPost("Title", "Hyperlink3").withId("13"));
        newsList.add(new NewsPost("Title", "Hyperlink4").withId("14"));
        newsList.add(new NewsPost("Title", "Hyperlink5").withId("15"));
        newsList.add(new NewsPost("Title", "Hyperlink6").withId("16"));
        newsList.add(new NewsPost("Title", "Hyperlink7").withId("17"));
        newsList.add(new NewsPost("Title", "Hyperlink").withId("1"));
        newsList.add(new NewsPost("Title", "Hyperlink2").withId("12"));
        newsList.add(new NewsPost("Title", "Hyperlink3").withId("13"));
        newsList.add(new NewsPost("Title", "Hyperlink4").withId("14"));
        newsList.add(new NewsPost("Title", "Hyperlink5").withId("15"));
        newsList.add(new NewsPost("Title", "Hyperlink6").withId("16"));
        newsList.add(new NewsPost("Title", "Hyperlink7").withId("17"));
        newsRecyclerAdapter.notifyDataSetChanged();
        Pair<String,String> news;
/*
        for(Pair<String,String> temp : news){

            //add a newsPost object with constructors (Title, Link)
            newsList.add(new NewsPost());
        }
*/
        return view;
    }

}
