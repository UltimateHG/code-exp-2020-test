package com.example.code_exp_2020_test;

import com.google.firebase.firestore.Exclude;

import javax.annotation.Nonnull;

public class NewsPostId {

    @Exclude
    public String NewsPostId;

    public <T extends NewsPostId> T withId(@Nonnull final String id) {
        this.NewsPostId = id;
        return (T) this;
    }

}
