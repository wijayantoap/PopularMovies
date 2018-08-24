package com.example.android.popularmovies.adapter;

import android.content.Context;
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

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<ReviewItem> reviewItems;
    private Context context;

    public ReviewAdapter(List<ReviewItem> reviewItems, Context context){
        this.reviewItems = reviewItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_reviews, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ReviewItem reviewItem = reviewItems.get(position);

        holder.textViewAuthor.setText(reviewItem.getAuthor());
        holder.textViewContent.setText(reviewItem.getContent());
    }

    @Override
    public int getItemCount() {
        return reviewItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewAuthor;
        public TextView textViewContent;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewAuthor = (TextView) itemView.findViewById(R.id.reviewAuthor);
            textViewContent = (TextView) itemView.findViewById(R.id.reviewContent);
        }
    }
}
