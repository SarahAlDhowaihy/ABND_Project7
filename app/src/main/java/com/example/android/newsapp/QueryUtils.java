package com.example.android.newsapp;

/**
 * Created by sarahaldowihy on 9/12/2017 AD.
 */

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Helper methods related to requesting and receiving news data from Url.
 */
public class QueryUtils {
    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 1500;

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the URL and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link News}
        List<News> newses = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link News}
        return newses;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT /* milliseconds */);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */
    public static List<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        //Create an empty ArrayList that we can start adding news to
        List<News> newses = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            //Extract the JSONObject associated with the key called "response"
            JSONObject response = baseJsonResponse.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results"
            JSONArray resultsArray = response.getJSONArray("results");
            //For each results on resultsArray , create an {@link News} object
            for (int i = 0; i < resultsArray.length(); i++) {
                // Get a single news at position i within the list of results
                JSONObject currentNews = resultsArray.getJSONObject(i);

                //Extract the value for the key called "webTitle"
                String title = currentNews.getString("webTitle");
                //Extract the value for the key called "sectionName"
                String section = currentNews.getString("sectionName");
                //Extract the value for the key called "webPublicationDate"
                String date = currentNews.getString("webPublicationDate");
                // Extract the value for the key called "webUrl"
                String url = currentNews.getString("webUrl");
                //Extract the array for the key called "tags"
                JSONArray tags = currentNews.getJSONArray("tags");
                String author = getAuthorName(tags);
                // Create a new {@link News} object with the title, section, date,
                // and url from the JSON response.
                News news = new News(title, author, section, date, url);

                //Add the new {@link News} to the list of newses.
                newses.add(news);
            }


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the News JSON results", e);
        }

        /**
         * Sort the List By the date
         */

        Collections.sort(newses, new Comparator<News>() {
            @Override
            public int compare(News news2, News news1) {

                return news1.getDate().compareTo(news2.getDate());
            }
        });


        return newses;
    }

    private static String getAuthorName(JSONArray tags) throws JSONException {
        String author = "";
        //check if there any authors
        if (tags.length() == 0)
            return author;
        //Extract the object from the tags array
        JSONObject tagsJSONObject = tags.getJSONObject(0);
        //Extract the author name from the tags array
        author = tagsJSONObject.getString("webTitle");
        return author;
    }
}
