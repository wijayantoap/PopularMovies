package com.example.android.popularmovies.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;

import java.util.List;

/**
 * Created by Wijayanto A.P on 8/6/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private List<TrailerItem> trailerItems;
    private Context context;
    public CardView cvTrailer;

    String keyValue;

    public TrailerAdapter(List<TrailerItem> trailerItems, Context context) {
        this.trailerItems = trailerItems;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_trailers, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TrailerItem trailerItem = trailerItems.get(position);

        holder.textTrailerTitle.setText(trailerItem.getTrailerTitle());
        holder.textTrailerKey.setText(trailerItem.getTrailerKey());

        keyValue = trailerItem.getTrailerKey();
    }

    @Override
    public int getItemCount() {
        return trailerItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView textTrailerTitle;
        public TextView textTrailerKey;

        public ViewHolder(View itemView) {
            super(itemView);

            cvTrailer = (CardView) itemView.findViewById(R.id.cardView);
            textTrailerTitle = (TextView) itemView.findViewById(R.id.trailerTitle);
            textTrailerKey = (TextView) itemView.findViewById(R.id.trailerKey);

            cvTrailer.setOnClickListener(this);
        }

        @Override
        public void onClick(View position) {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + keyValue));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + keyValue));
            try {
                context.startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                context.startActivity(webIntent);
            }
        }
    }
}
