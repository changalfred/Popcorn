package com.example.android.popcorn.networking;

import android.net.Uri;

import com.example.android.popcorn.BuildConfig;
import com.example.android.popcorn.MovieKeywords;

/**
 * This class contains methods that will be used to parse json data.
 */
public class JsonDataFetcher {

    public static void fetchJsonData() {

    }

    private String createUrl() {
        return Uri.parse(MovieKeywords.MOVIE_BASE_URL).buildUpon().appendPath(MovieKeywords.POPULAR)
                .appendQueryParameter(MovieKeywords.TMDB_API_KEY, BuildConfig.MOVIE_DP_API_KEY)
                .appendQueryParameter(MovieKeywords.TMDB_LANGUAGE, MovieKeywords.LANGUAGE)
                .appendQueryParameter(MovieKeywords.TMDB_PAGE, MovieKeywords.PAGE)
                .build().toString();
    }
}
