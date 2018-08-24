package com.example.android.popularmovies.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.popularmovies.apiservice.MoviesApiService;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.adapter.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.example.android.popularmovies.activity.MainActivity.MoviesAdapter.isNetworkStatusAvailable;
import static com.example.android.popularmovies.R.id.recyclerView;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private MoviesAdapter mAdapter;

    GridLayoutManager gridLayoutManager;
    int columns;

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    private Parcelable mListState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Applying Adapter to RecyclerView
           Using GridLayoutManager to specify the column
        */

        mRecyclerView = (RecyclerView) findViewById(recyclerView);
        gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new MoviesAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        List<Movie> movies = new ArrayList<>();


        /* Check whether network is available or not
           Display toast if no internet connection
        */
        if(isNetworkStatusAvailable (getApplicationContext())) {
            mAdapter.setMovieList(movies);
        } else {
            Toast.makeText(getApplicationContext(), "internet is not available", Toast.LENGTH_SHORT).show();

        }
        setPopular();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mBundleRecyclerViewState = new Bundle();
        mListState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, mListState);


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        columns = getResources().getInteger(R.integer.gallery_columns);

        if (mBundleRecyclerViewState != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mListState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
                    mRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);

                }
            }, 50);
        }

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            gridLayoutManager.setSpanCount(columns);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            gridLayoutManager.setSpanCount(columns);

        }
        mRecyclerView.setLayoutManager(gridLayoutManager);
    }



    /*
    // Calculate the the number of possible columns
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        return noOfColumns;
    }
    */

    /*
        Before compiling
        Rename "API_KEY" inside setPopular() and setTopRated() using your own API key
     */

    private void setPopular() {
        // Holds the configuration for the interface
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.themoviedb.org/3")
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        // Insert API key in "API_KEY"
                        request.addEncodedQueryParam("api_key", "1a2d6ba1b37a90c495a8ff25f95afab3");
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        MoviesApiService service = restAdapter.create(MoviesApiService.class);
        service.getPopularMovies(new Callback<Movie.MovieResult>() {
            @Override
            public void success(Movie.MovieResult movieResult, Response response) {
                mAdapter.setMovieList(movieResult.getResults());
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }

        });
    }

    private void setTopRated() {
        // Holds the configuration for the interface
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.themoviedb.org/3/")
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        // Insert API key in "API_KEY"
                        request.addEncodedQueryParam("api_key", "1a2d6ba1b37a90c495a8ff25f95afab3");
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        MoviesApiService service = restAdapter.create(MoviesApiService.class);
        service.getTopRatedMovies(new Callback<Movie.MovieResultTop>() {
            @Override
            public void success(Movie.MovieResultTop movieResult, Response response) {
                mAdapter.setMovieList(movieResult.getResults());
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.top_rated:
                // update the screen to sort the movie by top rated
                setTopRated();
                return true;
            case R.id.most_popular:
                // update the screen to sort the movie by most popular
                setPopular();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView imageView;
        public MovieViewHolder(View itemView)
        {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    // render the movie poster using adapter
    public static class MoviesAdapter extends RecyclerView.Adapter<MovieViewHolder>
    {
        private List<Movie> mMovieList;
        private LayoutInflater mInflater;
        private Context mContext;

        public MoviesAdapter(Context context)
        {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(context);
            this.mMovieList = new ArrayList<>();
        }

        // inflates into row_movies
        @Override
        public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = mInflater.inflate(R.layout.row_movies, parent, false);
            final MovieViewHolder viewHolder = new MovieViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = viewHolder.getAdapterPosition();
                    Intent intent = new Intent(mContext, MovieDetail.class);
                    intent.putExtra(MovieDetail.EXTRA_MOVIE, mMovieList.get(position));
                    mContext.startActivity(intent);
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MovieViewHolder holder, int position)
        {
            Movie movie = mMovieList.get(position);

            // Using picasso to load the image from the internet
            Picasso.with(mContext)
                    .load(movie.getPoster())
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount()
        {
            return (mMovieList == null) ? 0 : mMovieList.size();
        }

        public void setMovieList(List<Movie> movieList)
        {
            this.mMovieList.clear();
            this.mMovieList.addAll(movieList);
            notifyDataSetChanged();
        }

        public static boolean isNetworkStatusAvailable (Context context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null)
            {
                NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
                if(netInfos != null)
                    if(netInfos.isConnected())
                        return true;
            }
            return false;
        }



    }
}
