package com.example.android.popularmovies.adapter;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Wijayanto A.P on 8/5/2017.
 */

public class Review implements Parcelable{

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    private String id;
    private String author;
    private String content;
    private String url;

    public Review(Parcel source) {
        id = source.readString();
        author = source.readString();
        content = source.readString();
        url = source.readString();
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.id);
        parcel.writeString(this.author);
        parcel.writeString(this.content);
        parcel.writeString(this.url);

    }

    public static class ReviewResult {
        private List<Review> results;

        public List<Review> getResults() {
            return results;
        }
    }
}
