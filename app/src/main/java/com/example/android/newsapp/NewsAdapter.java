package com.example.android.newsapp;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by elizabethsotomayor on 7/11/18.
 */

public class NewsAdapter extends ArrayAdapter<News> {
    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.name_section) TextView sectionView;
    @BindView(R.id.author_name) TextView authorView;

    /**
     * Construct a new {@link NewsAdapter}.
     *
     * @param context of the app
     * @param stories is the list of news stories, the data source of the adapter
     */
    public NewsAdapter(Context context, List<News> stories) {
        super(context, 0, stories);
    }

    /**
     * Returns a list item view that displays information about the news story at the given position
     * in the list of articles.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
            ButterKnife.bind(this, listItemView);
        }

        // Find the news article at the given position in the list of news articles.
        News currentNews = getItem(position);

        // Set titleView to current title
        titleView.setText(currentNews.getTitle());

        // Set sectionView to current section
        sectionView.setText(currentNews.getSection());

        // Display author name of the current article in authorView
        if(currentNews.getAuthor() != "") {
            authorView.setText(currentNews.getAuthor());

            // Set the author name as visible if it is available
            authorView.setVisibility(View.VISIBLE);
        } else {
            // Set author view as gone if it is not available
            authorView.setVisibility(View.GONE);
        }

        // Find the TextView with view ID date
        TextView dateView = null;
        TextView timeView = null;
        if(currentNews.getDate() != null) {
            dateView = listItemView.findViewById(R.id.date);
            // Format the date string (i.e. "Nov 7, 2012")
            String formattedDate = formatDate(currentNews.getDate()).concat(",");
            // Display the date of the current news in the TextView
            dateView.setText(formattedDate);

            // Find the TextView with ID time
            timeView = listItemView.findViewById(R.id.time);
            // Format the time string (i.e. "2:50PM")
            String formattedTime = formatTime(currentNews.getDate());
            // Display the formatted time in the TextView
            timeView.setText(formattedTime);

            dateView.setVisibility(View.VISIBLE);
            timeView.setVisibility(View.VISIBLE);
        } else {
            dateView.setVisibility(View.GONE);
            timeView.setVisibility(View.GONE);
        }

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

    /**
     * Return the formatted date string (i.e. "Nov 7, 2012") from a Date object
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return a formatted date String from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }
}