package com.example.android.newsapp;

import java.util.Date;

/**
 * Created by elizabethsotomayor on 7/11/18.
 * An {@link News} object contains information related to a single news article.
 */

public class News {

    /* Title of the news article */
    private final String newsTitle;

    /* Section where the article can be found */
    private final String newsSection;

    /* Date the article was published */
    private final Date newsDate;

    /* URL for the article */
    private final String newsUrl;

    /* Name of the author of the article */
    private final String newsAuthor;

    /**
     * Constructs a new {@link News} object.
     *
     * @param title   is the title of the article
     * @param section is the section where the article can be found
     */
    public News(String title, String section, Date date, String author, String url) {
        newsTitle = title;
        newsSection = section;
        newsDate = date;
        newsAuthor = author;
        newsUrl = url;
    }

    /**
     * Return the title of the article.
     */
    public String getTitle() {
        return newsTitle;
    }

    /**
     * Return the section of the article.
     */
    public String getSection() {
        return newsSection;
    }

    /**
     * Return the date the article was published.
     */
    public Date getDate() {
        return newsDate;
    }

    /**
     * Return the author of the article.
     */
    public String getAuthor() {
        return newsAuthor;
    }

    /**
     * Return the URL of the article.
     */
    public String getUrl() {
        return newsUrl;
    }
}