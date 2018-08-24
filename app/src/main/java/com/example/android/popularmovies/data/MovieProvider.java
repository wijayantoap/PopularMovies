package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.os.Build.VERSION_CODES.M;
import static com.example.android.popularmovies.data.MovieDatabaseContract.BASE_CONTENT_URI;
import static com.example.android.popularmovies.data.MovieDatabaseContract.CONTENT_AUTHORITY;
import com.example.android.popularmovies.data.MovieDatabaseContract.MovieEntry;

/**
 * Created by Wijayanto A.P on 8/7/2017.
 */

public class MovieProvider extends ContentProvider{

    public static final int CODE_MOVIE = 101;
    private static UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    public static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        final String authority = MovieDatabaseContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieDatabaseContract.PATH_MOVIE,CODE_MOVIE);


        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)){
            case CODE_MOVIE:{
                SQLiteDatabase db = mOpenHelper.getReadableDatabase();
                cursor = db.query(MovieEntry.TABLE_NAME,
                        projection,selection,selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri returnUri = null;

        switch (sUriMatcher.match(uri)){
            case CODE_MOVIE:{
                SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                long id = db.insert(MovieEntry.TABLE_NAME,null,values);
                if (id>0){
                    returnUri =  MovieEntry.buildMovieUri(id);
                }
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)){
            case CODE_MOVIE:{
                SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                return db.delete(MovieEntry.TABLE_NAME,selection,selectionArgs);
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
