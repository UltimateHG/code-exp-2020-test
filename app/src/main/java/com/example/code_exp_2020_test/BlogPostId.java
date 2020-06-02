package com.example.code_exp_2020_test;

import com.google.firebase.firestore.Exclude;

import javax.annotation.Nonnull;

public class BlogPostId {

    @Exclude
    public String BlogPostId;

    public <T extends BlogPostId> T withId(@Nonnull final String id) {
        this.BlogPostId = id;
        return (T) this;
    }

}
