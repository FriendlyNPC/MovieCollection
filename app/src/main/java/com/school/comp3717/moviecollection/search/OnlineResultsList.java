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
import com.school.comp3717.moviecollection.MovieDbHelper;
import com.school.comp3717.moviecollection.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OnlineResultsList extends Fragment {
    private static final int YEAR_LENGTH = 4;

    private ProgressBar onlineProgressBar;
    private TextView onlineResults;
    private ListView onlineItems;
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


        QueryOnlineMoviesTask onlineQueryTask = new QueryOnlineMoviesTask();
        onlineQueryTask.execute(query);
    }

    public void setOnlineResults(TmdbResultsList<MovieDb> results, final List<Integer> inDb){
        if(getActivity() == null){return;} //stop the crash on rapid back-button presses
        if(results.getTotalResults() > 0){
            OnlineSearchItemArrayAdapter adapter = new OnlineSearchItemArrayAdapter(getActivity(),
                    results.getResults(), inDb);
            onlineItems.setAdapter(adapter);
            onlineItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("OnlineItemClick", "Online item clicked at position : " + position);
                    OnlineSearchItemArrayAdapter adapter = (OnlineSearchItemArrayAdapter)parent.getAdapter();
                    MovieDb selected = adapter.getMovie(position);
                    Log.d("OnlineItemClick", "Movie selected : " + selected.getTitle());
                    onlineItems.setVisibility(View.GONE);
                    GetMovieInfoTask getInfo = new GetMovieInfoTask();
                    getInfo.execute(selected);
                }
            });
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
        private final List<Integer> inDb;

        public OnlineSearchItemArrayAdapter(Context context, List<MovieDb> objects, List<Integer> inDb) {
            super(context, R.layout.search_result_online_item, objects);
            this.context = context;
            this.movies = objects;
            this.inDb = inDb;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.search_result_online_item,parent,false);
            TextView movieTitle = (TextView) rowView.findViewById(R.id.resultOnlineItemTitle);
            TextView movieYear = (TextView) rowView.findViewById(R.id.resultOnlineItemYear);

            movieTitle.setText(movies.get(position).getTitle());
            movieTitle.setText(movies.get(position).getTitle());
            String release = movies.get(position).getReleaseDate();
            if (release == null || release.trim().isEmpty()){
                movieYear.setText("");
            } else{
                movieYear.setText("(" + release.substring(0, YEAR_LENGTH) + ")");
            }

            final ImageButton quickAdd = (ImageButton) rowView.findViewById(R.id.quickAddButton);

            quickAdd.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    MovieInfoForQuickAddTask quickAddTask = new MovieInfoForQuickAddTask(quickAdd);
                    quickAddTask.execute(getMovie(position));
                }
            });

            boolean exists = inDb.contains(movies.get(position).getId());

            if (exists) {
                quickAdd.setVisibility(View.GONE);
            }

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
                Log.e("Search", "MovieDB  (MovieDbException) error");
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

            onlineProgressBar.setVisibility(View.GONE);
            if (result == null){
                setOnlineError();
            }else {
                MovieDbHelper dbHelper = new MovieDbHelper(getActivity());
                List<Integer> inDb = dbHelper.getMovieIDsExist(result.getResults());
                setOnlineResults(result,inDb);
            }
        }

    }

    private class GetMovieInfoTask extends AsyncTask<MovieDb, Void, Movie> {
        protected Movie doInBackground(MovieDb... query) {
            try {
                Movie movie = null;
                MovieDbHelper dbHelper = new MovieDbHelper(getActivity());
                // If movie in local DB, populate Movie Details with local DB data
                if (dbHelper.checkMovieExists(query[0].getId())) {
                    return dbHelper.getMovieById(query[0].getId());
                } else {
                    // If not in local DB, get info from online DB
                    TheMovieDbApi api = new TheMovieDbApi(getActivity().getResources().getString(R.string.apiKey));
                    // Need to explicitly request releases and cast info
                    movie = new Movie(api.getMovieInfo(query[0].getId(), null, "releases,casts"));
                    return movie;
                }
            } catch (MovieDbException e) {
                Log.e("Search", "MovieDB  (MovieDbException) error");
                String msg = (e.getMessage() == null) ? "MovieDB get movie info failed!" : e.getMessage();
                Log.e("Search", msg);
                return null;
            } catch (Exception e) {
                Log.e("Search", "MovieDB (Exception) error");
                String msg = (e.getMessage() == null) ? "MovieDB get movie info failed!" : e.getMessage();
                Log.e("Search", msg);
                return null;
            }
        }

        protected void onPostExecute(Movie result) {
            super.onPostExecute(result);
            if (result != null) {
                MainActivity act = (MainActivity) getActivity();
                act.setMovie(result);
            } else {
                // TODO: Show an error
                Toast.makeText(getActivity(), "Cannot fetch movie info", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class MovieInfoForQuickAddTask extends AsyncTask<MovieDb, Void, Movie> {
        ImageButton button;

        public MovieInfoForQuickAddTask(ImageButton button){
            super();
            this.button = button;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            button.setVisibility(View.GONE);
        }

        protected Movie doInBackground(MovieDb... query) {
            try {
                Movie movie = null;
                TheMovieDbApi api = new TheMovieDbApi(getActivity().getResources().getString(R.string.apiKey));
                // Request more movie info; need to explicitly request releases and cast info
                movie = new Movie(api.getMovieInfo(query[0].getId(), null, "releases,casts"));
                return movie;
            } catch (MovieDbException e) {
                Log.e("Search", "MovieDB  (MovieDbException) error");
                String msg = (e.getMessage() == null) ? "MovieDB get movie info failed!" : e.getMessage();
                Log.e("Search", msg);
                return null;
            } catch (Exception e) {
                Log.e("Search", "MovieDB (Exception) error");
                String msg = (e.getMessage() == null) ? "MovieDB get movie info failed!" : e.getMessage();
                Log.e("Search", msg);
                return null;
            }
        }

        protected void onPostExecute(Movie result) {
            super.onPostExecute(result);
            if (result != null) {

                MovieDbHelper dbHelper = new MovieDbHelper(getActivity());

                dbHelper.addMovieToCollection(result);

                Toast.makeText(
                        getActivity(),
                        result.getTitle() + " added to your collection!",
                        Toast.LENGTH_LONG).show();
            } else {
                button.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Cannot fetch movie info", Toast.LENGTH_LONG).show();
            }
        }
    }
}
