package com.example.checktrends.googletrends;

class News {
    String title;
    String timeAgo;
    String source;
    String newsUrl;
    String imageUrl;

    public News(String title,String timeAgo,String source,String newsUrl,String imageUrl){
        this.title = title;
        this.timeAgo = timeAgo;
        this.source = source;
        this.newsUrl = newsUrl;
        this.imageUrl = imageUrl;
    }

    String getTitle(){
        return title;
    }

    String getTimeAgo() {
        return timeAgo;
    }

    String getSource() {
        return source;
    }

    String getNewsUrl() {
        return newsUrl;
    }

    String getImageUrl() {
        return imageUrl;
    }
}
