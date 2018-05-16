package com.example.android.newsapp;

//This class will create objects which represent news articles displayed in a list in the app
public class NewsArticle {

    //Title of the News Article
    private String mTitle;

    //Section the News Article was published in
    private String mSection;

    //Website of the News Article
    private String mUrl;

    //Date of publication of the News Article
    private String mDate;

    //Author of the article
    private String mAuthor;

    //Constructs a new NewsArticle Object
    public NewsArticle(String title, String section, String url, String date, String author) {
        mTitle = title;
        mSection = section;
        mUrl = url;
        mDate = date;
        mAuthor = author;
    }

    //Returns the title of the News Article
    public String getTitle() {
        return mTitle;
    }

    //Returns the section of the News Article
    public String getSection() {
        return mSection;
    }

    //Returns the website of the News Article to read the whole article
    public String getWebsite() {
        return mUrl;
    }

    //Returns the date of publication of the News Article
    public String getDate() {
        return mDate;
    }

    //Returns the author of the News Article
    public String getAuthor() {
        return mAuthor;
    }
}
