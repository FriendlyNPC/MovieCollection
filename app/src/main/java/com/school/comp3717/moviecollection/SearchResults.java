package com.school.comp3717.moviecollection;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.MovieDb;
import com.omertron.themoviedbapi.results.TmdbResultsList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResults extends Fragment {
    private final String apiKey = "48a5ee87fadc129033207cea80b86b81";
    private ProgressBar onlineProgressBar;
    private ProgressBar collectionProgressBar;
    private TextView onlineResults;
    private TextView collectionResults;

    public SearchResults() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_results, container, false);
        onlineProgressBar = (ProgressBar) rootView.findViewById(R.id.onlineProgressBar);
        onlineResults = (TextView) rootView.findViewById(R.id.onlineResultsStatusText);
        collectionResults = (TextView) rootView.findViewById(R.id.collectionResultsStatus);
        collectionProgressBar = (ProgressBar) rootView.findViewById(R.id.collectionProgressBar);
        onlineProgressBar.setVisibility(View.GONE);
        onlineResults.setVisibility(View.GONE);
        collectionResults.setVisibility(View.GONE);
        collectionProgressBar.setVisibility(View.GONE);
        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    public void setOnlineResults(TmdbResultsList<MovieDb> results){
        LinearLayout layout = (LinearLayout)getView().findViewById(R.id.onlineItems);
        layout.removeAllViewsInLayout();
        if(results.getTotalResults() > 0){
            for(MovieDb movie : results.getResults()){
                TextView movieEntry = new TextView(getActivity());
                movieEntry.setTextColor(getResources().getColor(R.color.grey_800));
                movieEntry.setText(movie.getTitle());
                layout.addView(movieEntry);
            }
        } else {
            onlineResults.setText(R.string.no_results);
            onlineResults.setVisibility(View.VISIBLE);
        }
    }

    public void setDBResults(){
        collectionResults.setText("DB not set up yet ;_;");
        collectionResults.setVisibility(View.VISIBLE);
    }

    public void setOnlineError(){
        onlineResults.setText(R.string.no_connection);
        onlineResults.setVisibility(View.VISIBLE);
    }

    public void query(String query){
        String[] taskParams = {apiKey,query};
        onlineProgressBar.setVisibility(View.VISIBLE);
        onlineResults.setVisibility(View.GONE);

        collectionResults.setVisibility(View.GONE);
        collectionProgressBar.setVisibility(View.GONE);
        //query DB
        //TODO: add db query
        setDBResults();

        //query online
        new InitDbApi().execute(taskParams);
    }

    private class InitDbApi extends AsyncTask<String, Void, TmdbResultsList<MovieDb>> {
        protected TmdbResultsList<MovieDb> doInBackground(String... params) {
            try {
                TheMovieDbApi movieDB = new TheMovieDbApi(params[0]);
                return movieDB.searchMovie(params[1],0,null,false,0);
            } catch (MovieDbException e) {
                Log.e("Search", "MovieDB  (MovieDbExcepiton) error");
                String msg = (e.getMessage() == null) ? "MovieDB search failed!" : e.getMessage();
                Log.e("Search", msg);
                return null;
            } catch (Exception e) {
                Log.e("Search", "MovieDB (Exception) error");
                String msg = (e.getMessage() == null) ? "MovieDB search failed!" : e.getMessage();
                Log.e("Search", msg);
                return null;
            }
        }

        protected void onPostExecute(TmdbResultsList<MovieDb> result) {
            onlineProgressBar.setVisibility(View.GONE);
            if (result == null){
                setOnlineError();
            }else {
                logResults(result);
                setOnlineResults(result);
            }
        }

    }

    public void logResults(TmdbResultsList<MovieDb> results){
        for(MovieDb movie: results.getResults()){
            Log.d("Search", "Found title: " + movie.getTitle());
        }
    }
}
