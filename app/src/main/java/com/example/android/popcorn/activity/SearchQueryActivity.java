package com.example.android.popcorn.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by alfredchang on 2017-10-02.
 */

public class SearchQueryActivity extends AppCompatActivity {

    private final String LOG_TAG = SearchQueryActivity.class.getSimpleName();
    protected static final String SEARCH_KEY = "query";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());

        Log.v(LOG_TAG, "In search activity!");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.v(LOG_TAG, "Search query string: " + query);

            searchMovieInNewActivity(query);
        }
    }

    private void searchMovieInNewActivity(String query) {
        Intent searchResultsIntent = new Intent(this, SearchResultsActivity.class);
        searchResultsIntent.putExtra(SEARCH_KEY, query);
        startActivity(searchResultsIntent);
    }

}