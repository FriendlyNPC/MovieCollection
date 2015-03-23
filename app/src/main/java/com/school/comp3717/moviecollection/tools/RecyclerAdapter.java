package com.school.comp3717.moviecollection.tools;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.school.comp3717.moviecollection.Movie;
import com.school.comp3717.moviecollection.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private static final String TAG = "RecyclerAdapter";

    private ArrayList<Movie> movies = new ArrayList<>();

    public RecyclerAdapter(ArrayList<Movie> movies) {
        this.movies      = movies;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_layout, null);
        // Create ViewHolder
        return new ViewHolder(itemLayoutView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // Get data from your movie at this position
        // Replace the contents of the view with that movie
        Movie movie = movies.get(position);
        viewHolder.imgViewIcon.setImageResource(R.drawable.default_poster);
        // TODO: Cache the posters and only make API call if not in cache
        if (movie.getPosterUrl() != null) {
            DownloadPoster.getPoster(viewHolder.imgViewIcon.getContext(),
                                     movie.getPosterUrl(),
                                     viewHolder.imgViewIcon);

            //ImageLoader.getInstance().displayImage("http://image.tmdb.org/t/p/w185" + movie.getPosterUrl(), viewHolder.imgViewIcon);
            /*new DownloadPosterTask(viewHolder.imgViewIcon.getContext(), viewHolder.imgViewIcon)
                    .execute("http://image.tmdb.org/t/p/w185" + movie.getPosterUrl());*/
            Log.d(TAG, "Grabbed poster for " + movie.getTitle());
        }
        viewHolder.itemView.setTag(movie);
    }

    // Inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageButton imgViewIcon;
        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            imgViewIcon = (ImageButton) itemLayoutView.findViewById(R.id.poster);
        }
    }

    // Return the size of your movie (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void add(Movie movie) {
        movies.add(movie);
        notifyDataSetChanged();
    }

}