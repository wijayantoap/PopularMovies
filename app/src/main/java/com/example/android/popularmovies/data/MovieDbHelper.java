package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.example.android.popularmovies.data.MovieDatabaseContract.MovieEntry;

import static com.example.android.popularmovies.data.MovieDatabaseContract.MovieEntry.MOVIE_TITLE;

/**
 * Created by Wijayanto A.P on 8/6/2017.
 */

public class MovieDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "movie.db";

    private static final int DATABASE_VERSION = 2;

    private final Context context;


    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MovieEntry.TABLE_NAME + "(" +
                        MovieEntry.MOVIE_ID + " INTEGER PRIMARY KEY, " +
                        MovieEntry.MOVIE_TITLE + " TEXT NOT NULL, " +
                        MovieEntry.MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                        MovieEntry.MOVIE_POSTER + " BLOB NOT NULL, " +
                        MovieEntry.MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                        MovieEntry.MOVIE_AVG + " REAL NOT NULL, " +
                        MovieEntry.MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                        MovieEntry.MOVIE_TRAILERS + " TEXT NOT NULL, " +
                        MovieEntry.MOVIE_REVIEWS + " TEXT NOT NULL" +
                        ")";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
