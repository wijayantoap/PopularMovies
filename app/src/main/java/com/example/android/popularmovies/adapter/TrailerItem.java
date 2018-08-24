package com.example.android.popularmovies.adapter;

/**
 * Created by Wijayanto A.P on 8/6/2017.
 */

public class TrailerItem {
    private String trailerTitle;
    private String trailerKey;

    public TrailerItem(String trailerTitle, String trailerKey) {
        this.trailerTitle = trailerTitle;
        this.trailerKey = trailerKey;
    }

    public String getTrailerTitle() {
        return trailerTitle;
    }

    public String getTrailerKey() {
        return trailerKey;
    }
}
