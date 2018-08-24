package com.example.android.popularmovies.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;


import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MovieDatabaseContract;
import com.google.gson.annotations.SerializedName;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static android.provider.Settings.System.DATE_FORMAT;
import static com.example.android.popularmovies.R.string.reviews;

/**
 * Created by Wijayanto A.P on 7/6/2017.
 */

public class Movie implements Parcelable{
    final String TMDB_IMAGE_PATH = "http://image.tmdb.org/t/p/w185";

    private String title;

    private boolean isFavMovie = false;

    /* Using retrofit to retrieve the property from the JSON
       This was adapted from http://www.vogella.com/tutorials/Retrofit/article.html
     */
    @SerializedName("id")
    private String id;

    @SerializedName("poster_path")
    private String poster_path;

    @SerializedName("overview")
    private String description;

    @SerializedName("vote_average")
    private Double rating;

    @SerializedName("release_date")
    private String release_date;

    private Bitmap poster;


    public Movie() {}

    /* Passing data accross different components using Parcel
       This was adapted from https://dzone.com/articles/using-android-parcel
    */
    protected Movie(Parcel in) {
        id = in.readString();
        title = in.readString();
        poster_path = in.readString();
        description = in.readString();
        rating = (Double) in.readValue(Double.class.getClassLoader());
        release_date = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return TMDB_IMAGE_PATH + poster_path;
    }

    public void setPoster(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double ratingAverage) {
        rating = ratingAverage;
    }

    public String getDetailedRating() {
        return String.valueOf(getRating()) + "/10";
    }

    public String getReleaseDate() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        if(!release_date.equals("null")) {
            release_date = release_date;
        }
    }

    public String getDateFormat() {
        return DATE_FORMAT;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(poster_path);
        parcel.writeString(description);
        parcel.writeValue(rating);
        parcel.writeString(release_date);
    }

    public static class MovieResult {
        private List<Movie> results;

        public List<Movie> getResults() {
            return results;
        }
    }

    public static class MovieResultTop {
        private List<Movie> results;

        public List<Movie> getResults() {
            return results;
        }
    }

    boolean saveToBookmarks(Context context){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieDatabaseContract.MovieEntry.MOVIE_ID, this.id);
        contentValues.put(MovieDatabaseContract.MovieEntry.MOVIE_TITLE, this.title);
        contentValues.put(MovieDatabaseContract.MovieEntry.MOVIE_OVERVIEW, this.description);
        contentValues.put(MovieDatabaseContract.MovieEntry.MOVIE_POSTER_PATH, this.poster_path);
        contentValues.put(MovieDatabaseContract.MovieEntry.MOVIE_AVG, this.rating);
        contentValues.put(MovieDatabaseContract.MovieEntry.MOVIE_RELEASE_DATE, this.release_date);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.poster.compress(Bitmap.CompressFormat.JPEG,100,bos);
        byte[] bytes = bos.toByteArray();

        contentValues.put(MovieDatabaseContract.MovieEntry.MOVIE_POSTER,bytes);

        if (context.getContentResolver().insert(MovieDatabaseContract.MovieEntry.CONTENT_URI,contentValues)!=null){
            Toast.makeText(context, R.string.favourite,Toast.LENGTH_SHORT).show();
            return true;
        }else{
            Toast.makeText(context, R.string.fav_error,Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    boolean removeFromBookmarks(Context context){
        long deletedRows = context.getContentResolver().delete(MovieDatabaseContract.MovieEntry.CONTENT_URI,
                MovieDatabaseContract.MovieEntry.MOVIE_ID + "=?",new String[]{Long.toString(Long.parseLong(this.id))});
        if (deletedRows>0){
            Toast.makeText(context, R.string.favourite,Toast.LENGTH_SHORT).show();
            return true;
        }else {
            Toast.makeText(context, R.string.fav_error, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean isBookmarked(Context context){
        Cursor cursor = context.getContentResolver()
                .query(MovieDatabaseContract.MovieEntry.CONTENT_URI,
                        new String[]{MovieDatabaseContract.MovieEntry.MOVIE_ID},
                        MovieDatabaseContract.MovieEntry.MOVIE_ID + "=?",
                        new String[]{Long.toString(Long.parseLong(this.id))},null);
        if (cursor!=null) {
            boolean bookmarked = cursor.getCount() > 0;
            cursor.close();
            return bookmarked;
        }
        return false;
    }
}

