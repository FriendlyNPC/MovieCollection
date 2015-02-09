package com.school.comp3717.moviecollection;


import android.content.Context;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.MovieDb;
import com.omertron.themoviedbapi.results.TmdbResultsList;

import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResults extends Fragment {
    private ProgressBar onlineProgressBar;
    private ProgressBar collectionProgressBar;
    private TextView onlineResults;
    private TextView collectionResults;
    private ListView collectionItems;
    private ListView onlineItems;

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
        collectionItems = (ListView) rootView.findViewById(R.id.collectionItemList);
        onlineItems = (ListView) rootView.findViewById(R.id.onlineItemList);
        onlineProgressBar.setVisibility(View.GONE);
        onlineResults.setVisibility(View.GONE);
        collectionResults.setVisibility(View.GONE);
        collectionProgressBar.setVisibility(View.GONE);
        collectionItems.setVisibility(View.GONE);
        onlineItems.setVisibility(View.GONE);
        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    public void setOnlineResults(TmdbResultsList<MovieDb> results){
        if(results.getTotalResults() > 0){
            SearchItemArrayAdapter adapter = new SearchItemArrayAdapter(getActivity(),
                    results.getResults());

            onlineItems.setAdapter(adapter);
            onlineItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("OnlineItem", "Online item clicked at position : " + position);
                    //TODO: get onItemClick working
                    /*
                    SearchItemArrayAdapter adapter = (SearchItemArrayAdapter)parent.getAdapter();
                    MovieDb selected = adapter.getMovie(position);
                    MainActivity act = (MainActivity)getActivity();
                    act.setMovie(selected);*/
                }
            });
            onlineItems.setVisibility(View.VISIBLE);
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
        String[] taskParams = {query};
        onlineProgressBar.setVisibility(View.VISIBLE);
        onlineResults.setVisibility(View.GONE);

        collectionResults.setVisibility(View.GONE);
        collectionProgressBar.setVisibility(View.GONE);

        onlineItems.setVisibility(View.GONE);
        collectionItems.setVisibility(View.GONE);

        //query DB
        //TODO: add db query
        setDBResults();

        //query online
        new InitDbApi().execute(taskParams);
    }

    private class InitDbApi extends AsyncTask<String, Void, TmdbResultsList<MovieDb>> {
        protected TmdbResultsList<MovieDb> doInBackground(String... params) {
            try {
                TheMovieDbApi movieDB = new TheMovieDbApi(getActivity().getResources().getString(R.string.apiKey));
                return movieDB.searchMovie(params[0],0,null,false,0);
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

    private class SearchItemArrayAdapter extends ArrayAdapter<MovieDb> {
        private final Context context;
        private final List<MovieDb> movies;

        public SearchItemArrayAdapter(Context context, List<MovieDb> objects) {
            super(context, R.layout.search_result_online_item, objects);
            this.context = context;
            this.movies = objects;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.search_result_online_item,parent,false);
            TextView movieTitle = (TextView) rowView.findViewById(R.id.resultOnlineItemTitle);
            TextView movieYear = (TextView) rowView.findViewById(R.id.resultOnlineItemYear);

            movieTitle.setText(movies.get(position).getTitle());
            movieYear.setText(movies.get(position).getReleaseDate());

            Button quickAdd = (Button) rowView.findViewById(R.id.quickAddButton);

            quickAdd.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //TODO: Do actual DB add here
                    Toast.makeText(
                        getActivity(),
                        movies.get(position).getTitle() + " added to your collection!",
                        Toast.LENGTH_LONG).show();
                }
            });
            return rowView;
        }

        public MovieDb getMovie(int position){
            return movies.get(position);
        }
    }
}
