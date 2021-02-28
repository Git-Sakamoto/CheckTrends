package com.example.checktrends.wikipedia;

class WikipediaContent {
    private String[]events;
    private String[]birthdays;
    private String[]anniversaries;

    WikipediaContent(String[]events,String[]birthdays,String[]anniversaries){
        this.events = events;
        this.birthdays = birthdays;
        this.anniversaries = anniversaries;
    }

    public String[] getEvents() {
        return events;
    }

    public String[] getBirthdays() {
        return birthdays;
    }

    public String[] getAnniversaries() {
        return anniversaries;
    }
}
