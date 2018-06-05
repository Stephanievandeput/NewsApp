package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.LoaderManager.LoaderCallbacks;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderCallbacks<List<NewsArticle>> {

    //Variable for the log messages
    private static final String LOG_TAG = MainActivity.class.getName();

    //URL for the news article data from the Guardian dataset
    private static final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search";
    // Constant value for the newsArticle loader ID
    private static final int NEWS_LOADER_ID = 1;

    //Adapter for the list of news articles
    private NewsAdapter mAdapter;

    //TextView that is displayed when the list is empty
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find a reference to the ListView in the layout
        ListView newsListView = (ListView) findViewById(R.id.list);

        //Set a TextView for the empty state of the app
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        //Create a new adapter that takes an empty list of news articles as input
        mAdapter = new NewsAdapter(this, new ArrayList<NewsArticle>());

        //Set the adapter on the ListView, so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        //Set an item click listener on the ListView, which sends a webintent to open the website with the selected article
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Find the current news article that was clicked on
                NewsArticle currentNewsArticle = mAdapter.getItem(position);

                //Convert the String URL into a URI object
                Uri newsArticleUri = Uri.parse(currentNewsArticle.getWebsite());

                //Create a new intent to view the news article URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsArticleUri);

                //Send the intent to launch a new activity
                startActivity(websiteIntent);

            }
        });

        //Get a reference to the ConnectivityManager to check the state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //If there is a connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            //Get reference to the LoaderManager in order to interact with loader
            LoaderManager loaderManager = getLoaderManager();

            //Initialize new loader
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise display that there is no internet connection
            // Hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    // onCreateLoader instantiates and returns a new Loader for the given ID
    public Loader<List<NewsArticle>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        //Finds preferences for the Order By preference
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        //Break apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that was just parsed so query parameters can be added to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value
        uriBuilder.appendQueryParameter("section", "business");
        uriBuilder.appendQueryParameter("from-date", "2017-01-01");
        uriBuilder.appendQueryParameter("order-date", "published");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("api-key", "ec9ac2e5-63b6-4320-9e51-b3a9c0ba63f7");
        String urlFinal = uriBuilder.toString();
        return new NewsLoader(this, urlFinal);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsArticle>> loader, List<NewsArticle> news) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        //Set empty state text to display "No news articles found"
        mEmptyStateTextView.setText(R.string.no_news_articles);

        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsArticle>> loader) {
        // Loader reset, to clear existing data.
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
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}




