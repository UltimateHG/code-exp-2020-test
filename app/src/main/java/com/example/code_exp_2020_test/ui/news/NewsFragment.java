package com.example.code_exp_2020_test.ui.news;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.code_exp_2020_test.NewsPost;
import com.example.code_exp_2020_test.NewsRecyclerAdapter;
import com.example.code_exp_2020_test.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//internet

public class NewsFragment extends Fragment {

    private List<NewsPost> newsList = new ArrayList<>();
    protected NewsRecyclerAdapter newsRecyclerAdapter;
    private RecyclerView news_list_view;

    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        news_list_view = view.findViewById(R.id.news_list_view);

        //init variables
        newsRecyclerAdapter = new NewsRecyclerAdapter(newsList);
        news_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        news_list_view.setAdapter(newsRecyclerAdapter);
        news_list_view.setHasFixedSize(true);

        pullData();

        newsRecyclerAdapter.notifyDataSetChanged();

        swipeRefreshLayout = view.findViewById(R.id.swiperefresh_news);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            pullData();
            swipeRefreshLayout.setRefreshing(false);
        });

        return view;
    }


    //pull data from moh
    void pullData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Document doc = Jsoup.connect("https://www.moh.gov.sg/covid-19").get();
                    Elements els = doc.getElementsByClass("sfContentBlock");
                    for(int i=0;i<els.size();i++){
                        try{
                            Elements children0=els.get(i).children();
                            //child0 should be a h3 that says "Latest Updates"
                            String child0Tag=children0.get(0).tagName();
                            //children0.get(1) should be the div that contains the table
                            Elements children1=children0.get(1).children();
                            String child1Tag=children1.get(0).tagName();
                            if(child0Tag.equals("h3") && child1Tag.equals("tbody")){
                                Element tBody=children1.get(0);
                                //Element tBody=table.child(0);
                                Elements tableRows=tBody.children();

                                Element row0=tableRows.get(0);
                                if(row0.child(0).child(0).child(0).child(0).text()!=null
                                        &&row0.child(1).child(0).child(0).child(0).text()!=null){
                                    for(int j=1;j<tableRows.size();j++){
                                        Element tableRow=tableRows.get(j);
                                        String title=tableRow.child(1).child(0).child(0).text();
                                        Element hrefFinder=tableRow.child(1);
                                        String link=hrefFinder.absUrl("href");
                                        while(link==null || link=="" || link.length()<3){
                                            hrefFinder=hrefFinder.child(0);
                                            link = hrefFinder.absUrl("href");
                                        }
                                        NewsPost np = new NewsPost(title,link);
                                        newsList.add(np);

                                    }
                                }
                                //j is 1 as first tableRow is "Date" and "Title"

                            }
                        }
                        catch(IndexOutOfBoundsException e){
                            Log.e("logging","IndexOutOfBoundsException");
                            //e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    Log.e("logging","IOException");
                    //e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        newsRecyclerAdapter.notifyDataSetChanged();
                    }
                });
            }
        }.start();


    }
}
