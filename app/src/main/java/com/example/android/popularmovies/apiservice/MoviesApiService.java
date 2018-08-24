package com.example.android.popularmovies.apiservice;

import com.example.android.popularmovies.adapter.Movie;
import com.example.android.popularmovies.adapter.Review;

import retrofit.Callback;
import retrofit.http.GET;

import static android.R.attr.id;

/**
 * Created by Wijayanto A.P on 7/6/2017.
 */

// Java interface to represent TMDB API
public interface MoviesApiService {
    @GET("/movie/popular")
    void getPopularMovies(Callback<Movie.MovieResult> cb);

    @GET("/movie/top_rated")
    void getTopRatedMovies(Callback<Movie.MovieResultTop> cb);
}
