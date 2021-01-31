package com.example.checktrends.bookmark;

public class Bookmark {
    private String id;
    private String title;
    private String url;
    private String accessTime;

    public Bookmark(String id, String title, String url, String accessTime) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.accessTime = accessTime;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getAccessTime() {
        return accessTime;
    }
}
