package com.example.code_exp_2020_test;

import java.util.Date;

public class Comments {
    //variables
    private String comment, username, user_id;
    private Date timestamp;

    public Comments() {}

    public Comments(String comment, String username, String user_id, Date timestamp, String status) {
        this.comment = comment;
        this.username = username;
        this.timestamp = timestamp;
        this.user_id = user_id;
    }

    public String getComment() {return comment;}

    public void setComment(String comment) {this.comment = comment;};

    public String getUser_id() {return user_id;}

    public void setUser_id(String user_id) {this.user_id = user_id;}

    public String getUsername() {return username;}

    public void setUsername(String username) {this.username = username;}

    public Date getTimestamp() {return timestamp;}

    public void setTimestamp(Date timestamp) {this.timestamp = timestamp;}
}
