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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.omertron.themoviedbapi.model.MovieDb;
import com.school.comp3717.moviecollection.MainActivity;
import com.school.comp3717.moviecollection.Movie;
import com.school.comp3717.moviecollection.MovieDbHelper;
import com.school.comp3717.moviecollection.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionResultsList extends Fragment {
    private static final int YEAR_LENGTH = 4;

    private ProgressBar collectionProgressBar;
    private TextView collectionResults;
    private ListView collectionItems;
    private String query;
    public CollectionResultsList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_collection_results_list, container, false);

        collectionResults = (TextView) rootView.findViewById(R.id.collectionResultsStatus);
        collectionProgressBar = (ProgressBar) rootView.findViewById(R.id.collectionProgressBar);
        collectionItems = (ListView) rootView.findViewById(R.id.collectionItemList);
        collectionResults.setVisibility(View.GONE);
        collectionProgressBar.setVisibility(View.GONE);
        collectionItems.setVisibility(View.GONE);

        String query = this.getArguments().getString("QUERY");
        Log.d("CollectionSearch", "Query: " + query);
        doQuery(query);

        return rootView;
    }

    public void doQuery(String query){
        //TODO: do query stuff here

        collectionResults.setVisibility(View.GONE);
        collectionItems.setVisibility(View.GONE);
        collectionProgressBar.setVisibility(View.VISIBLE);

        QueryCollectionMoviesTask collectionMoviesTask = new QueryCollectionMoviesTask();
        collectionMoviesTask.execute(query);
    }

    public void setCollectionResults(List<Movie> results){
        if(getActivity() == null){return;} //stop the crash on rapid back-button presses
        if (results == null || results.isEmpty()){
            collectionResults.setText(R.string.no_results);
            collectionResults.setVisibility(View.VISIBLE);
        }else{
            CollectionSearchItemArrayAdapter adapter = new CollectionSearchItemArrayAdapter(getActivity(),
                    results);
            collectionItems.setAdapter(adapter);
            collectionItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("CollectionItemClick", "Collection item clicked at position : " + position);
                    CollectionSearchItemArrayAdapter adapter = (CollectionSearchItemArrayAdapter)parent.getAdapter();
                    Movie selected = adapter.getMovie(position);
                    Log.d("CollectionItemClick", "Movie selected : " + selected.getTitle());
                    collectionItems.setVisibility(View.GONE);
                    MainActivity mainActivity = (MainActivity)getActivity();
                    mainActivity.setMovie(selected);
                }
            });
            collectionItems.setVisibility(View.VISIBLE);
        }

    }

    public void setCollectionError(){
        collectionResults.setText(R.string.db_error);
        collectionResults.setVisibility(View.VISIBLE);
    }

    private class CollectionSearchItemArrayAdapter extends ArrayAdapter<Movie> {
        private final Context context;
        private final List<Movie> movies;

        public CollectionSearchItemArrayAdapter(Context context, List<Movie> objects) {
            super(context, R.layout.search_result_collection_item, objects);
            this.context = context;
            this.movies = objects;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.search_result_collection_item,parent,false);
            TextView movieTitle = (TextView) rowView.findViewById(R.id.resultCollectionItemTitle);
            TextView movieYear = (TextView) rowView.findViewById(R.id.resultCollectionItemYear);

            movieTitle.setText(movies.get(position).getTitle());
            String release = movies.get(position).getReleaseDate();
            if (release == null || release.trim().isEmpty()){
                movieYear.setText("");
            } else{
                movieYear.setText("(" + release.substring(0, YEAR_LENGTH) + ")");
            }

            return rowView;
        }

        public Movie getMovie(int position){
            return movies.get(position);
        }
    }

    private class QueryCollectionMoviesTask extends AsyncTask<String, Void, List<Movie>> {
        protected List<Movie> doInBackground(String... query) {
            try {
                MovieDbHelper dbHelper = new MovieDbHelper(getActivity());
                return dbHelper.searchMovie(query[0]);
            } catch (Exception e) {
                String msg = (e.getMessage() == null) ? "MovieDB search failed!" : e.getMessage();
                Log.e("Search", msg);
                return null;
            }
        }

        protected void onPostExecute(List<Movie> result) {

            collectionProgressBar.setVisibility(View.GONE);
            if (result == null){
                setCollectionError();
            }else {
                setCollectionResults(result);
            }
        }

    }
}
