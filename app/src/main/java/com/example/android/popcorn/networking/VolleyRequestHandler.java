package com.example.android.popcorn.networking;

import com.android.volley.VolleyError;
import com.example.android.popcorn.model.Movie;

/**
 * Created by alfredchang on 2017-12-07.
 */

public interface VolleyRequestHandler {

    void onSuccessId(String response);
    void onSuccessDetails(String response, Movie movie);
    void onSuccessRecommendedDetails(String response, Movie movie);
    void onFail(VolleyError error);

}
