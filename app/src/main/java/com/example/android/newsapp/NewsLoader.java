package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loads a list of news articles by using an AsyncTask to perform a
 * network request to a URL.
 */
public class NewsLoader extends AsyncTaskLoader<List<News>> {

    /* Tag for log messages */
    private static final String LOG_TAG = NewsLoader.class.getName();

    /* Query URL */
    private String mUrl;

    /**
     * Construct a new {@link NewsLoader}.
     */
    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This will run on a background thread.
     */
    @Override
    public List<News> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform a network request, parse the response, and extract a list of news articles.
        List<News> stories = QueryUtils.fetchNewsData(mUrl);
        return stories;
    }
}