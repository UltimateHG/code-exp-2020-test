package com.example.code_exp_2020_test;

import androidx.core.view.NestedScrollingChild;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

//internet
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import java.io.IOException;

public class NewsFragment extends Fragment {
    //fragment initialization
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private Context mContext;
    private List<NewsPost> newsList;
    protected NewsRecyclerAdapter newsRecyclerAdapter;
    private RecyclerView news_list_view;

    /**
     * Create new instance of this fragment with param1 and param2
     */
    public static NewsFragment newInstance(String param1, String param2) {
        NewsFragment fragment = new NewsFragment();
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

        pullData();


        //where news is the Pair thing
        //smth smth create an list
        /*
        newsList.add(new NewsPost("Title", "youtube.com/").withId("1"));
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
        /*
        newsList.add(new NewsPost("Title", "Hyperlink7").withId("17"));
         */
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


    //pull data from moh
    void pullData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Log.e("logging","running");
                try {
                    Log.e("logging","trying0");
                    Document doc = Jsoup.connect("https://www.moh.gov.sg/covid-19").get();
                    Elements els = doc.getElementsByClass("sfContentBlock");
                    Log.e("logging",els.size()+"");
                    for(int i=0;i<els.size();i++){
                        try{
                            Log.e("logging","trying1");
                            Elements children0=els.get(i).children();
                            //child0 should be a h3 that says "Latest Updates"
                            String child0Tag=children0.get(0).tagName();
                            //children0.get(1) should be the div that contains the table
                            Elements children1=children0.get(1).children();
                            String child1Tag=children1.get(0).tagName();
                            Log.e("hello", child0Tag + " " + child1Tag + " " + i + " " + children0.get(1).tagName());
                            if(child0Tag=="h3"&&child1Tag=="tbody"){
                                Element tBody=children1.get(0);
                                //Element tBody=table.child(0);
                                Elements tableRows=tBody.children();
                                Log.e("djidjei", "" + tableRows.size());

                                Element row0=tableRows.get(0);
                                Log.d("names",row0.child(0).child(0).child(0).child(0).text()+","+row0.child(1).child(0).child(0).child(0).text());
                                if(row0.child(0).child(0).child(0).child(0).text()!=null
                                        &&row0.child(1).child(0).child(0).child(0).text()!=null){
                                    Log.e("Something", "I don't care about nothin else");
                                    for(int j=1;j<tableRows.size();j++){
                                        Element tableRow=tableRows.get(j);
                                        String title=tableRow.child(1).child(0).child(0).text();
                                        Element hrefFinder=tableRow.child(1);
                                        String link=hrefFinder.absUrl("href");
                                        while(link==null || link=="" || link.length()<3){
                                            hrefFinder=hrefFinder.child(0);
                                            link = hrefFinder.absUrl("href");
                                        }
                                        Log.e(""+j, ""+j);
                                        NewsPost np = new NewsPost(title,link);
                                        if(np.link==null){
                                            System.out.println("I'm dying");
                                        } else{

                                            System.out.println(link);
                                            System.out.println(np.getLink());
                                        }
                                        newsList.add(np);
                                        //Log.e("" + j, newsList.get(j-1).getLink());
                                        //Log.e("big oof", title+ " " + j);
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
                        for(int i=0; i<newsList.size();i++){
                            System.out.println(newsList.get(i).title);
                            System.out.println(newsList.get(i).link);
                        }
                        newsRecyclerAdapter.notifyDataSetChanged();
                    }
                });
            }
        }.start();


    }
/*
    void pullData2(){
        try {
            Log.e("logging","trying0");
            Document doc = Jsoup.connect("https://www.moh.gov.sg/covid-19").get();
            Elements els = doc.getElementsByClass("sfContentBlock");
            Log.e("logging",els.size()+"");
            for(int i=0;i<els.size();i++){
                try{
                    Log.e("logging","trying1");
                    Elements children0=els.get(i).children();
                    //child0 should be a h3 that says "Latest Updates"
                    String child0Tag=children0.get(0).tagName();
                    //children0.get(1) should be the div that contains the table
                    Elements children1=children0.get(1).children();
                    String child1Tag=children1.get(0).tagName();
                    Log.e("hello", child0Tag + " " + child1Tag + " " + i + " " + children0.get(1).tagName());
                    if(child0Tag=="h3"&&child1Tag=="tbody"){
                        Element tBody=children1.get(0);
                        //Element tBody=table.child(0);
                        Elements tableRows=tBody.children();
                        Log.e("djidjei", "" + tableRows.size());

                        Element row0=tableRows.get(0);
                        Log.d("names",row0.child(0).child(0).child(0).child(0).text()+","+row0.child(1).child(0).child(0).child(0).text());
                        if(row0.child(0).child(0).child(0).child(0).text()!=null
                                &&row0.child(1).child(0).child(0).child(0).text()!=null){
                            Log.e("Something", "I don't care about nothin else");
                            for(int j=1;j<tableRows.size();j++){
                                Element tableRow=tableRows.get(j);
                                String title=tableRow.child(1).child(0).child(0).text();
                                String link=tableRow.child(1).child(0).child(0).absUrl("href");
                                Log.wtf(title,link);
                                newsList.add(new NewsPost(title,link));
                                Log.e("big oof", title+ " " + j);
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

    }*/
}
