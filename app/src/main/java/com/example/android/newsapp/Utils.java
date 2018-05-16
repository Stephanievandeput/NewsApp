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
import java.util.ArrayList;
import java.util.List;

public final class Utils {

    //Tag for the log messages
    private static final String LOG_TAG = Utils.class.getSimpleName();

    /*
     * This is a private constructor, because it is not needed to create a Utils object. The class is only
     * meant to hold static variables and methods. These can be accessed from the class name Utils.
     */
    private Utils() {
    }

    /*
     * Query the Guardian dataset and return a list of NewsArticle objects
     */
    public static List<NewsArticle> fetchNewsData(String requestUrl) {
        //Create URL object
        URL url = createUrl(requestUrl);

        //Perform HTTP request to the URL and receive JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request", e);
        }

        //Extract relevant fields from the JSON response and create a list of NewsArticle objects
        List<NewsArticle> news = extractFeatureFromJson(jsonResponse);

        //Return list of NewsArticle objects
        return news;
    }

    /*
     * Returns new URL object from the given String URL
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL", e);
        }
        return url;
    }

    /*
     * Make the HTTP request to the given URL and return a String as response
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        //If the URL is null, then return early
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
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
            Log.e(LOG_TAG, "Problem retrieving the news article JSON results", e);
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

    /*
     * Convert the InputStream into a String which contains the
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

    /*
     * Returns an ArrayList<NewsArticle> by parsing out information about the news articles from the input newsJSON String.
     */
    private static List<NewsArticle> extractFeatureFromJson(String newsJSON) {
        //if the JSON String is empty or null, then return early
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        //Create an empty ArrayList that the news articles can be added to
        List<NewsArticle> newsArticles = new ArrayList<>();

        /*
         * Try to parse the JSON response String. If there is a problem with it, a JSONException will
         * be thrown.
         * Catch the exception so the app does not crash, print he error message to the logs.
         */
        try {

            //Create a JSON object from the JSON response String
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            JSONObject response = baseJsonResponse.getJSONObject("response");

            //Extract the JSON array with the key called "response", which represents a list of news articles
            JSONArray newsArticlesArray = response.getJSONArray("results");

            //For each News Article, create a NewsArticle object

            for (int i = 0; i < newsArticlesArray.length(); i++) {

                //Get a single NewsArticle at position i within the list of news articles
                JSONObject currentNewsArticle = newsArticlesArray.getJSONObject(i);

                //Extract the value for the key called "webTitle"
                String title = "";
                if (currentNewsArticle.has("webTitle")) {
                    title = currentNewsArticle.getString("webTitle");
                }

                //Extract the value for the key called "sectionName"
                String section = "";
                if (currentNewsArticle.has("sectionName")) {
                    section = currentNewsArticle.getString("sectionName");
                }

                //Extract the value for the key called "webUrl"
                String url = "";
                if (currentNewsArticle.has("webUrl")) {
                    url = currentNewsArticle.getString("webUrl");
                }

                //Extract the value for the key called "webPublicationDate"
                String date = "";
                if (currentNewsArticle.has("webPublicationDate")) {
                    date = currentNewsArticle.getString("webPublicationDate");
                }

                //Get the contributor from the JSON Array in the currentNewArticle object
                JSONArray tags = currentNewsArticle.getJSONArray("tags");
                JSONObject currentAuthor = tags.getJSONObject(0);
                String author = "";
                if (currentAuthor.has("webTitle")) {
                    author = currentAuthor.getString("webTitle");
                }

                //Create a new NewsArticles object with the title, section and url from the JSON response
                NewsArticle news = new NewsArticle(title, section, url, date, author);

                //Add the new object to the list of news articles
                newsArticles.add(news);
            }

        } catch (JSONException e) {
            //If an error is thrown, catch the exception here. Print a log message with the exception message
            Log.e("Utils", "Problem parsing the news article JSON results, e");
        }
        //Return the list of news articles
        return newsArticles;
    }
}
