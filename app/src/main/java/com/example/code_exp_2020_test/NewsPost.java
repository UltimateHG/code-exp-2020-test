package com.example.code_exp_2020_test;

public class NewsPost extends NewsPostId{

    public String title, link;

    /*public NewsPost(){

    }*/

    //generate thingy
    public NewsPost(String t, String l){
        this.title = t;
        this.link = l;
    }

    //getters and setters
    public String getTitle(){
        return title;
    }
    public String getLink(){
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
