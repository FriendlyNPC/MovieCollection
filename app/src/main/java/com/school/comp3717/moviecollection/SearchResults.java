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
    private ProgressBar progressBar;
    private TextView onlineResults;
    private String query;
    public SearchResults() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_results, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.searchProgressBar);
        onlineResults = (TextView) rootView.findViewById(R.id.onlineResultsStatusText);
        progressBar.setVisibility(View.GONE);
        onlineResults.setVisibility(View.GONE);
        Bundle args = getArguments();
        query = args.getString("query");
        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        Query();
    }

    public void setOnlineResults(TmdbResultsList<MovieDb> results){

        LinearLayout layout = (LinearLayout)getView().findViewById(R.id.onlineItems);
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

    public void setOnlineError(){
        onlineResults.setText(R.string.no_connection);
        onlineResults.setVisibility(View.VISIBLE);
    }

    public void Query(){
        String[] taskParams = {apiKey,query};
        progressBar.setVisibility(View.VISIBLE);
        new InitDbApi().execute(taskParams);
    }

    private class InitDbApi extends AsyncTask<String, Void, TmdbResultsList<MovieDb>> {
        protected TmdbResultsList<MovieDb> doInBackground(String... params) {
            try {
                TheMovieDbApi movieDB = new TheMovieDbApi(params[0]);
                return movieDB.searchMovie(params[1],0,null,true,0);
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
            progressBar.setVisibility(View.GONE);
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
