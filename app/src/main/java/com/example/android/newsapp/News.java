package com.example.android.newsapp;

/**
 * Created by sarahaldowihy on 9/12/2017 AD.
 */

public class News {
    private String mTitle;
    private String mAuthor;
    private String mSection;
    private String mDate;
    private String mUrl;

    public News(String mTitle, String mAuthor, String mSection, String mDate, String mUrl) {
        this.mTitle = mTitle;
        this.mAuthor = mAuthor;
        this.mSection = mSection;
        this.mDate = mDate;
        this.mUrl = mUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getSection() {
        return mSection;
    }

    public String getDate() {
        return mDate;
    }

    public String getUrl() {
        return mUrl;
    }
}
