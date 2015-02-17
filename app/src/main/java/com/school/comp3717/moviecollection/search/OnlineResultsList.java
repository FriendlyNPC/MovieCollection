package com.school.comp3717.moviecollection.search;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.MovieDb;
import com.omertron.themoviedbapi.results.TmdbResultsList;
import com.school.comp3717.moviecollection.MainActivity;
import com.school.comp3717.moviecollection.Movie;
import com.school.comp3717.moviecollection.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OnlineResultsList extends Fragment {
    private ProgressBar onlineProgressBar;
    private TextView onlineResults;
    private ListView onlineItems;
    private QueryOnlineMoviesTask onlineQueryTask;
    public OnlineResultsList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_online_results_list, container, false);

        onlineProgressBar = (ProgressBar) rootView.findViewById(R.id.onlineProgressBar);
        onlineResults = (TextView) rootView.findViewById(R.id.onlineResultsStatusText);
        onlineItems = (ListView) rootView.findViewById(R.id.onlineItemList);
        onlineProgressBar.setVisibility(View.VISIBLE);
        onlineResults.setVisibility(View.GONE);
        onlineItems.setVisibility(View.GONE);
        String query = this.getArguments().getString("QUERY");
        Log.d("OnlineSearch", "Query: " + query);
        doQuery(query);
        return rootView;
    }

    public void doQuery(String query){
        Log.d("OnlineSearch", "Online Search Query: " + query);
        onlineProgressBar.setVisibility(View.VISIBLE);
        onlineResults.setVisibility(View.GONE);
        onlineItems.setVisibility(View.GONE);
        //send background query

        if(onlineQueryTask != null){
            onlineQueryTask.cancel(true);
        }else {
            onlineQueryTask = new QueryOnlineMoviesTask();
            onlineQueryTask.execute(query);
        }
    }

    public void setOnlineResults(TmdbResultsList<MovieDb> results){
        if(getActivity() == null){return;} //stop the crash on rapid back-button presses
        if(results.getTotalResults() > 0){
            OnlineSearchItemArrayAdapter adapter = new OnlineSearchItemArrayAdapter(getActivity(),
                    results.getResults());
            onlineItems.setAdapter(adapter);
            onlineItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("OnlineItemClick", "Online item clicked at position : " + position);
                    OnlineSearchItemArrayAdapter adapter = (OnlineSearchItemArrayAdapter)parent.getAdapter();
                    MovieDb selected = adapter.getMovie(position);
                    Log.d("OnlineItemClick", "Movie selected : " + selected.getTitle());
                    onlineItems.setVisibility(View.GONE);
                    MainActivity act = (MainActivity)getActivity();
                    Movie movie = new Movie(selected);
                    act.setMovie(movie);
                }
            });
            getFragmentManager().executePendingTransactions();
            onlineItems.setVisibility(View.VISIBLE);
        } else {
            onlineResults.setText(R.string.no_results);
            onlineResults.setVisibility(View.VISIBLE);
        }
    }


    public void setOnlineError(){
        onlineResults.setText(R.string.no_connection);
        onlineResults.setVisibility(View.VISIBLE);
    }

    public void logResults(TmdbResultsList<MovieDb> results){
        for(MovieDb movie: results.getResults()){
            Log.d("OnlineSearch", "Found title: " + movie.getTitle());
        }
    }

    private class OnlineSearchItemArrayAdapter extends ArrayAdapter<MovieDb> {
        private final Context context;
        private final List<MovieDb> movies;

        public OnlineSearchItemArrayAdapter(Context context, List<MovieDb> objects) {
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

            ImageButton quickAdd = (ImageButton) rowView.findViewById(R.id.quickAddButton);

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

    private class QueryOnlineMoviesTask extends AsyncTask<String, Void, TmdbResultsList<MovieDb>> {
        protected TmdbResultsList<MovieDb> doInBackground(String... query) {
            try {
                TheMovieDbApi movieDB = new TheMovieDbApi(getActivity().getResources().getString(R.string.apiKey));
                return movieDB.searchMovie(query[0],0,null,false,0);
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
            super.onPostExecute(result);
            onlineQueryTask = null;
            onlineProgressBar.setVisibility(View.GONE);
            if (result == null){
                setOnlineError();
            }else {
                //logResults(result);
                setOnlineResults(result);
            }
        }

        @Override
        protected void onCancelled(){
            super.onCancelled();
            onlineQueryTask.cancel(true);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        onlineQueryTask = null;
    }
}
