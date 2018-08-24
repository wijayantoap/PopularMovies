package com.example.android.popularmovies.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.adapter.Movie;
import com.example.android.popularmovies.adapter.ReviewAdapter;
import com.example.android.popularmovies.adapter.ReviewItem;
import com.example.android.popularmovies.adapter.TrailerAdapter;
import com.example.android.popularmovies.adapter.TrailerItem;
import com.example.android.popularmovies.data.MovieDatabaseContract;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.popularmovies.R.id.idMovie;


public class MovieDetail extends AppCompatActivity {
    public static final String EXTRA_MOVIE = "movie";
    private SQLiteDatabase mDb;

    String idMovieId;

    private Movie mMovie;
    ImageView poster;
    TextView id;
    TextView title;
    TextView description;
    TextView rating;
    TextView release_date;
    ImageButton favourite;
    ToggleButton buttonReview;
    ToggleButton buttonTrailer;
    ScrollView mScrollView;

    private RecyclerView mRecyclerView;
    private ReviewAdapter mAdapter;
    private List<ReviewItem> reviewItems;

    private RecyclerView mRecyclerView2;
    private TrailerAdapter mAdapter2;
    private List<TrailerItem> trailerItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mScrollView = (ScrollView) findViewById(R.id.mScrollView);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewReview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewItems = new ArrayList<>();

        mRecyclerView2 = (RecyclerView) findViewById(R.id.recyclerViewTrailer);
        mRecyclerView2.setLayoutManager(new LinearLayoutManager(this));
        trailerItems = new ArrayList<>();

        buttonTrailer = (ToggleButton) findViewById(R.id.btnTrailer);
        buttonTrailer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    loadRecyclerViewData2();
                    buttonTrailer.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.color.colorWhite));
                } else {
                    clearTrailer();
                    buttonTrailer.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.color.colorBlack));
                }
            }
        });

        buttonReview = (ToggleButton) findViewById(R.id.btnReview);
        buttonReview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    loadRecyclerViewData();
                    buttonReview.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.color.colorWhite));
                } else {
                    clearReview();
                    buttonReview.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.color.colorBlack));
                }
            }
        });

        Bundle args = new Bundle();
        if (mMovie.isBookmarked(this)){
            favourite.setImageResource(android.R.drawable.btn_star_big_on);
            args.putBoolean("local",true);

        }else {
            favourite.setImageResource(android.R.drawable.btn_star_big_off);
            args.putBoolean("local",false);
        }



        if (getIntent().hasExtra(EXTRA_MOVIE)) {
            mMovie = getIntent().getParcelableExtra(EXTRA_MOVIE);
        } else {
            throw new IllegalArgumentException("Detail activity must receive a movie parcelable");
        }
        id = (TextView) findViewById(idMovie);
        title = (TextView) findViewById(R.id.movie_title);
        description = (TextView) findViewById(R.id.synopsis);
        poster = (ImageView) findViewById(R.id.image_thumbnail);
        rating = (TextView) findViewById(R.id.user_rating);
        release_date = (TextView) findViewById(R.id.release_date);

        id.setText(mMovie.getId());
        title.setText(mMovie.getTitle());
        description.setText(mMovie.getDescription());
        Picasso.with(this)
                .load(mMovie.getPoster())
                .into(poster);
        rating.setText(mMovie.getDetailedRating());
        release_date.setText(mMovie.getReleaseDate());

        idMovieId = mMovie.getId();
        // poster.setImageBitmap(mMovie.getPoster());

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("DETAIL_SCROLL_POSITION",
                new int[]{ mScrollView.getScrollX(), mScrollView.getScrollY()});
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position = savedInstanceState.getIntArray("DETAIL_SCROLL_POSITION");
        if(position != null)
            mScrollView.post(new Runnable() {
                public void run() {
                    mScrollView.scrollTo(position[0], position[1]);
                }
            });
    }

    private void loadRecyclerViewData() {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(idMovieId)
                .appendPath("reviews")
                .appendQueryParameter("api_key", "1a2d6ba1b37a90c495a8ff25f95afab3");
        String myUrl = builder.build().toString();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                myUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        JSONArray array = jsonObject.getJSONArray("results");

                        for (int i = 0; i<array.length(); i++) {
                            JSONObject o = array.getJSONObject(i);
                            ReviewItem list = new ReviewItem(
                                    o.getString("author"),
                                    o.getString("content")
                            );
                            reviewItems.add(list);
                        }

                        mAdapter = new ReviewAdapter(reviewItems, getApplicationContext());
                        mRecyclerView.setAdapter(mAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void clearReview() {
        int size = this.reviewItems.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.reviewItems.remove(0);
            }

            mAdapter.notifyDataSetChanged();
        }
    }

    private void loadRecyclerViewData2() {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(idMovieId)
                .appendPath("videos")
                .appendQueryParameter("api_key", "1a2d6ba1b37a90c495a8ff25f95afab3");
        String myUrl2 = builder.build().toString();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                myUrl2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray array = jsonObject.getJSONArray("results");

                            for (int i = 0; i<array.length(); i++) {
                                JSONObject o = array.getJSONObject(i);
                                TrailerItem list = new TrailerItem(
                                        o.getString("name"),
                                        o.getString("key")
                                );
                                trailerItems.add(list);
                            }

                            mAdapter2 = new TrailerAdapter(trailerItems, getApplicationContext());
                            mRecyclerView2.setAdapter(mAdapter2);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void clearTrailer() {
        int size = this.trailerItems.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.trailerItems.remove(0);
            }

            mAdapter2.notifyDataSetChanged();
        }
    }



}
