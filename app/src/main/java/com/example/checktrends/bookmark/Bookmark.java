package com.example.checktrends.bookmark;

class Bookmark {
    private String id;
    private String title;
    private String url;
    private String accessTime;

    Bookmark(String id, String title, String url, String accessTime) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.accessTime = accessTime;
    }

    String getId() {
        return id;
    }

    String getTitle() {
        return title;
    }

    String getUrl() {
        return url;
    }

    String getAccessTime() {
        return accessTime;
    }
}
