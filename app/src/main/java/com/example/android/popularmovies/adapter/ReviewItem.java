package com.example.android.popularmovies.adapter;

/**
 * Created by Wijayanto A.P on 8/6/2017.
 */

public class ReviewItem {
    private String author;
    private String content;

    public ReviewItem(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
