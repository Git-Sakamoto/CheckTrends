package com.example.checktrends.yahoonews;

class Bookmark {
    private String id;
    private String title;
    private String url;

    Bookmark(String id, String title, String url){
        this.id = id;
        this.title = title;
        this.url = url;
    }

    String getId(){
        return id;
    }

    String getTitle(){
        return title;
    }

    String getUrl(){
        return url;
    }

}
