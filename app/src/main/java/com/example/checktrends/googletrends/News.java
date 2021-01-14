package com.example.checktrends.googletrends;

//トレンドに対する関連ニュースの情報を格納するクラス
class News {
    private String title; //ニュース名
    private String timeAgo; //何時間前にニュースが配信されたのか
    private String source; //ニュースの配信サイト名
    private String newsUrl; //ニュースページのURL
    private String imageUrl; //ニュースページのサムネイル

    News(String title,String timeAgo,String source,String newsUrl,String imageUrl){
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
