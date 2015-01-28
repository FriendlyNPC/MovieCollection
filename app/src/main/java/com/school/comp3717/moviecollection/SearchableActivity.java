package com.school.comp3717.moviecollection;

import android.app.SearchManager;
import android.content.Intent;
import android.os.*;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.model.MovieDb;
import com.omertron.themoviedbapi.results.TmdbResultsList;
import com.omertron.themoviedbapi.tools.ApiUrl;
import com.omertron.themoviedbapi.TheMovieDbApi;


public class SearchableActivity extends ActionBarActivity {
    private String apiKey = "48a5ee87fadc129033207cea80b86b81";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d("Searchable","Attempting search with value: \"" + query + "\"");
            Toast toast = Toast.makeText(getApplicationContext(),query,Toast.LENGTH_LONG);
            toast.show();

            String[] taskParams = {apiKey,query};
            new InitDbApi().execute(taskParams);

        }
    }

    public void logResults(TmdbResultsList<MovieDb> results){
        for(MovieDb movie: results.getResults()){
            Log.d("Searchable", "Found title: " + movie.getTitle());
        }
        this.finish();
    }

    private class InitDbApi extends AsyncTask<String, Void, TmdbResultsList<MovieDb>> {
        protected TmdbResultsList<MovieDb> doInBackground(String... params) {
            try {
                TheMovieDbApi movieDB = new TheMovieDbApi(params[0]);
                return movieDB.searchMovie(params[1],0,null,false,0);

            } catch (MovieDbException e) {
                Log.e("Searchable", "MovieDB  (MovieDbExcepiton) error");
                String msg = (e.getMessage() == null) ? "MovieDB search failed!" : e.getMessage();
                Log.e("Searchable", msg);
                return null;
            } catch (Exception e) {
                Log.e("Searchable", "MovieDB (Exception) error");
                String msg = (e.getMessage() == null) ? "MovieDB search failed!" : e.getMessage();
                Log.e("Searchable", "exception", e);
                return null;
            }
        }

        protected void onPostExecute(TmdbResultsList<MovieDb> result) {
            logResults(result);
        }
    }
}


