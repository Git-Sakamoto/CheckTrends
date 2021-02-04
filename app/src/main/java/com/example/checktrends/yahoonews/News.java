package com.example.checktrends.yahoonews;

public class News {
    private String title;
    private String url;
    private String jpgUrl;

    public News(String title, String url, String jpgUrl){
        this.title = title;
        this.url = url;
        this.jpgUrl = jpgUrl;
    }

    public String getTitle(){
        return title;
    }

    public String getUrl(){
        return url;
    }

    public String getJpgUrl(){
        return jpgUrl;
    }
}
