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

public class ExploreFragment extends Fragment {

    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_explore, container, false);

        ScrollView scrollView = fragment.findViewById(R.id.scrollViewExplore);

        for(int i=0; i<news.size(); i++){
            createCard(news.get(i));
        }

        return fragment;
    }

    public void createCard(Pair<String,String> pair){
        // Initialize a new CardView
        CardView card = new CardView(mContext);

        // Set the CardView layoutParams
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(   //sets width and height of card, might change later
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        card.setLayoutParams(params);

        // Set CardView corner radius
        card.setRadius(9);

        // Set cardView content padding
        card.setContentPadding(15, 15, 15, 15);

        // Set a background color for CardView
        card.setCardBackgroundColor(Color.parseColor("#FFC6D6C3"));

        // Set the CardView maximum elevation
        card.setMaxCardElevation(15);

        // Set CardView elevation
        card.setCardElevation(9);

        // Initialize a new TextView to put in CardView
        TextView tv = new TextView(mContext);
        tv.setLayoutParams(params);
        tv.setText("CardView\nProgrammatically");
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        tv.setTextColor(Color.RED);

        // Put the TextView in CardView
        card.addView(tv);

        // Finally, add the CardView in root layout
        mRelativeLayout.addView(card);
    }
}
