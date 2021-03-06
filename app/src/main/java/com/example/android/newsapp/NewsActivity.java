package com.example.android.newsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsActivity extends AppCompatActivity implements LoaderCallbacks<List<News>> {
    @BindView(R.id.list) ListView newsListView;
    @BindView(R.id.empty_view) TextView EmptyStateTextView;
    @BindView(R.id.loading_indicator) View loadingIndicator;

    /* Adapter for the list of news stories */
    private NewsAdapter mAdapter;

    /* URL to query The Guardian's dataset for news article information */
    private static final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search?&api-key=3bd58974-10b7-4bcb-830a-2c076bf926bd&show-tags=contributor";

    /* Constant value for the news loader ID */
    private static final int NEWS_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Inject the Views using the ButterKnife library
        ButterKnife.bind(this);

        // Create a new adapter that takes an empty list of News as input
        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which will send an intent to a web browser to
        // open the article.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current news article that was clicked on
                News currentNews = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentNews.getUrl());

                // Create a new intent to view the news URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        newsListView.setEmptyView(EmptyStateTextView);

        // Get a reference to the ConnectivityManager to check the state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch the data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager to interact with loaders
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise display an error message
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection message
            EmptyStateTextView.setText(R.string.no_connection);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String search = sharedPreferences.getString(
                getString(R.string.search_key_settings),
                getString(R.string.settings_Search_default));

        String orderBy = sharedPreferences.getString(
                getString(R.string.order_by_key_settings),
                getString(R.string.order_by_default_settings)
        );

        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        if(!search.equals("")) {
            uriBuilder.appendQueryParameter("q", search);
            orderBy = getString(R.string.order_by_relevance_value_settings);
        }
        if (search.equals("") && orderBy.equals(getString(R.string.order_by_relevance_value_settings))) {
            orderBy = getString(R.string.order_by_newest_value_settings);
        }

        uriBuilder.appendQueryParameter("order-by", orderBy);

        // Create a new loader for the URL
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> stories) {
        // Hide the loading indicator once the data has been loaded
        loadingIndicator.setVisibility(View.GONE);

        // Set the empty state text to display "No news found"
        EmptyStateTextView.setText(R.string.no_news);

        // Clear the adapter of previous news data
        mAdapter.clear();

        // If there is a valid list of {@link News}, add them to the adapter's
        // data set. This will update the ListView.
        if (stories != null && !stories.isEmpty()) {
            mAdapter.addAll(stories);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset to clear existing data
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.settings_action) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}