package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by elizabethsotomayor on 7/11/18.
 */

public class QueryUtils {

    /* Tag for log messages*/
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /* Private constructor because there should not be a QueryUtils object */
    private QueryUtils() {
    }

    /**
     * Query the Guardian dataset and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {
        // Create a URL object
        URL url = createUrl(requestUrl);

        // Receive a JSON response after performing HTTP request
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link News}
        List<News> stories = extractFeatureFromNewsJson(jsonResponse);

        // Return the list of {@link News}
        return stories;
    }

    private static List<News> extractFeatureFromNewsJson(String newsJson) {
        //If the JSON string is null, return early
        if(TextUtils.isEmpty(newsJson)) {
            return null;
        }

        // Create an empty ArrayList that we can add news to
        List<News> stories = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way it is formatted,
        // a JSONException exception object will be thrown. Catch the exception to prevent the app from
        // crashing, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJson);

            // Extract the JSONObject named "response"
            JSONObject newsObject = baseJsonResponse.getJSONObject("response");

            // Extract the JSONArray with key "results",
            JSONArray results = newsObject.getJSONArray("results");

            // For each news item in the Array, create a {@link News} object
            for (int i = 0; i < results.length(); i++) {

                // Get a single news position within the list of news
                JSONObject currentNews = results.getJSONObject(i);

                // JSONArray for the key "tags" which contains the author name
                JSONArray tags = currentNews.getJSONArray("tags");

                // JSONObject from the JSONArray "tags" to get the author name
                JSONObject authorObject = tags.getJSONObject(0);

                // The title of the article
                String title = currentNews.getString("webTitle");

                // Section that the article belongs to
                String section = currentNews.getString("sectionName");

                // The date that the article was published
                String originalDate = currentNews.getString("webPublicationDate");

                // Format publication date
                Date publicationDate = null;
                try {
                    publicationDate = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).parse(originalDate);
                } catch (Exception e) {
                    Log.e("QueryUtils", "Problem parsing news date", e);
                }

                // URL where the article can be found
                String url = currentNews.getString("webUrl");

                // Name of the author of the article at position i
                String author = authorObject.getString("webTitle");

                News news = new News(title, section, publicationDate, author, url);
                stories.add(news);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the news JSON results.", e);
        }

        // Return the list of news stories
        return stories;
    }

    /**
     * Returns a new URL object from the String url provided.
     */
    private static URL createUrl(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch ( MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the URL provided and return a String as a response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String JsonResponse = "";

        // If url is null, return early
        if (url == null) {
            return JsonResponse;
        }

        HttpURLConnection urlConnect = null;
        InputStream inputStream = null;
        try {
            urlConnect = (HttpURLConnection) url.openConnection();
            urlConnect.setReadTimeout(10000);
            urlConnect.setConnectTimeout(15000);
            urlConnect.setRequestMethod("GET");
            urlConnect.connect();

            if (urlConnect.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnect.getInputStream();
                JsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnect.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving news JSON results.");
        } finally {
            if (urlConnect != null) {
                urlConnect.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return JsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder out = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                out.append(line);
                line = reader.readLine();
            }
        }
        return out.toString();
    }
}