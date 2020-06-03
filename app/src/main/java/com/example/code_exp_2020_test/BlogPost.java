package com.example.code_exp_2020_test;

import java.util.Date;

public class BlogPost extends BlogPostId {

    //variables
    public String user_id, title, body, username;
    public Date timestamp;

    public BlogPost() {}

    public BlogPost(String user_id, String username, String title, String body, Date timestamp) {
        this.user_id = user_id;
        this.title = title;
        this.body = body;
        this.timestamp = timestamp;
        this.username = username;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
