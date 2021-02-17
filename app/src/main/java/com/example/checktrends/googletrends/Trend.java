package com.example.checktrends.googletrends;

import java.util.List;

class Trend {
    private String rank;
    private String trendTitle;
    private List<News> newsList;

    Trend(String rank,String trendTitle,List<News> newsList){
        this.rank = rank;
        this.trendTitle = trendTitle;
        this.newsList = newsList;
    }

    public String getRank() {
        return rank;
    }

    public String getTrendTitle() {
        return trendTitle;
    }

    public List<News> getNewsList() {
        return newsList;
    }
}
