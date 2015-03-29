package com.school.comp3717.moviecollection;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.MovieDb;
import com.omertron.themoviedbapi.results.TmdbResultsList;
import com.school.comp3717.moviecollection.tools.RecyclerAdapter;
import com.school.comp3717.moviecollection.tools.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    private static final int LIST_SIZE = 8;

    private MovieDbHelper    movieDbHelper;
    private RecyclerAdapter  adapterNP;
    private RecyclerAdapter  adapterMP;
    private RecyclerView     recyclerViewNP;
    private RecyclerView     recyclerViewMP;
    private TextView         headerNP;
    private TextView         headerMP;

    public Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View           rootView    = inflater.inflate(R.layout.fragment_home, container, false);
        RelativeLayout introLayout = (RelativeLayout) rootView.findViewById(R.id.introductionWrapper);
        movieDbHelper              = new MovieDbHelper(getActivity());

        // Local Lists
        ArrayList<Movie> recentlyAdded  = getRecentlyAdded();
        ArrayList<Movie> justWatched    = getJustWatched();
        RecyclerView     recyclerViewRA = (RecyclerView) rootView.findViewById(R.id.recyclerViewRA);
        RecyclerView     recyclerViewJW = (RecyclerView) rootView.findViewById(R.id.recyclerViewJW);
        RecyclerAdapter  adapterRA      = new RecyclerAdapter(recentlyAdded);
        RecyclerAdapter  adapterJW      = new RecyclerAdapter(justWatched);
        TextView         headerRA       = (TextView) rootView.findViewById(R.id.recentlyAddedHeader);
        TextView         headerJW       = (TextView) rootView.findViewById(R.id.justWatchedHeader);

        // Online Lists
        ArrayList<Movie> nowPlaying     = new ArrayList<>();
        ArrayList<Movie> mostPopular    = new ArrayList<>();
        recyclerViewNP                  = (RecyclerView) rootView.findViewById(R.id.recyclerViewNR);
        recyclerViewMP                  = (RecyclerView) rootView.findViewById(R.id.recyclerViewMP);
        adapterNP                       = new RecyclerAdapter(nowPlaying);
        adapterMP                       = new RecyclerAdapter(mostPopular);
        headerNP                        = (TextView) rootView.findViewById(R.id.newReleasesHeader);
        headerMP                        = (TextView) rootView.findViewById(R.id.mostPopularHeader);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
        recyclerViewNP.setLayoutManager(layoutManager1);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity());
        recyclerViewMP.setLayoutManager(layoutManager2);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getActivity());
        recyclerViewRA.setLayoutManager(layoutManager3);
        LinearLayoutManager layoutManager4 = new LinearLayoutManager(getActivity());
        recyclerViewJW.setLayoutManager(layoutManager4);

        setRecyclerView(recentlyAdded, recyclerViewRA, adapterRA, headerRA, false);
        setRecyclerView(justWatched,   recyclerViewJW, adapterJW, headerJW, false);
        setRecyclerView(nowPlaying,    recyclerViewNP, adapterNP, headerNP, true);
        setRecyclerView(mostPopular,   recyclerViewMP, adapterMP, headerMP, true);

        if (recyclerViewJW.getVisibility() == View.GONE &&
            recyclerViewRA.getVisibility() == View.GONE) {
            introLayout.setVisibility(View.VISIBLE);
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.home_header);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        QueryNowPlayingTask nowPlayingTask = new QueryNowPlayingTask();
        nowPlayingTask.execute("en");
        QueryPopularMoviesTask popularMoviesTask = new QueryPopularMoviesTask();
        popularMoviesTask.execute("en");
    }

    private void setRecyclerView(ArrayList<Movie> movies,
                                 RecyclerView recyclerView,
                                 RecyclerAdapter adapter,
                                 TextView header,
                                 boolean online) {
        if (movies.isEmpty() && !online) {
            header.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            addRecyclerViewListener(movies, getActivity(), recyclerView);
        }
    }

    private void addRecyclerViewListener(final ArrayList<Movie> movies,
                                         Context context,
                                         RecyclerView recyclerView) {
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.setMovie(movies.get(position));
                    }
                })
        );
    }

    private ArrayList<Movie> getRecentlyAdded() {
        ArrayList<Movie> recentlyAdded = movieDbHelper.getRecentlyAdded();
        int myListSize = recentlyAdded.size();
        if (myListSize > LIST_SIZE) {
            recentlyAdded.subList(LIST_SIZE, myListSize).clear();
        }
        return recentlyAdded;
    }

    private ArrayList<Movie> getJustWatched() {
        ArrayList<Movie> justWatched = movieDbHelper.getJustWatched();
        int myListSize = justWatched.size();
        if (myListSize > LIST_SIZE) {
            justWatched.subList(LIST_SIZE, myListSize).clear();
        }
        return justWatched;
    }

    private class QueryNowPlayingTask extends AsyncTask<String, Void, TmdbResultsList<MovieDb>> {
        protected TmdbResultsList<MovieDb> doInBackground(String... language) {
            try {
                TheMovieDbApi movieDB = new TheMovieDbApi(getActivity().getResources().getString(R.string.apiKey));
                return movieDB.getNowPlayingMovies(language[0], 0);
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
            if (result != null) {
                List<MovieDb> tempResults = result.getResults();
                ArrayList<MovieDb> movieResults = (ArrayList<MovieDb>)tempResults;
                int myListSize = movieResults.size();
                if (myListSize > LIST_SIZE) {
                    movieResults.subList(LIST_SIZE, myListSize).clear();
                }
                GetNowPlayingInfoTask infoTask = new GetNowPlayingInfoTask();
                infoTask.execute(movieResults);
            }
        }
    }

    private class GetNowPlayingInfoTask extends AsyncTask<ArrayList<MovieDb>, Void, ArrayList<Movie>> {
        protected ArrayList<Movie> doInBackground(ArrayList<MovieDb>... movies) {
            try {
                ArrayList<Movie> results = new ArrayList<>();
                for(MovieDb movie : movies[0]) {
                    // If movie in local DB, populate Movie Details with local DB data
                    if (movieDbHelper.checkMovieExists(movie.getId())) {
                        results.add(movieDbHelper.getMovieById(movie.getId()));
                    } else {
                        // If not in local DB, get info from online DB
                        TheMovieDbApi api = new TheMovieDbApi(getActivity().getResources().getString(R.string.apiKey));
                        // Need to explicitly request releases and cast info
                        results.add(new Movie(api.getMovieInfo(movie.getId(), null, "releases,casts")));
                    }
                }
                return results;
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

        protected void onPostExecute(ArrayList<Movie> results) {
            super.onPostExecute(results);
            if (results != null) {
                headerNP.setVisibility(View.VISIBLE);
                recyclerViewNP.setVisibility(View.VISIBLE);
                for(Movie movie : results) {
                    adapterNP.add(movie);
                }
            }
        }
    }

    private class QueryPopularMoviesTask extends AsyncTask<String, Void, TmdbResultsList<MovieDb>> {
        protected TmdbResultsList<MovieDb> doInBackground(String... language) {
            try {
                TheMovieDbApi movieDB = new TheMovieDbApi(getActivity().getResources().getString(R.string.apiKey));
                return movieDB.getPopularMovieList(language[0], 0);
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
            if (result != null) {
                List<MovieDb> tempResults = result.getResults();
                ArrayList<MovieDb> movieResults = (ArrayList<MovieDb>)tempResults;
                int myListSize = movieResults.size();
                if (myListSize > LIST_SIZE) {
                    movieResults.subList(LIST_SIZE, myListSize).clear();
                }
                GetMostPopularInfoTask infoTask = new GetMostPopularInfoTask();
                infoTask.execute(movieResults);
            }
        }
    }

    private class GetMostPopularInfoTask extends AsyncTask<ArrayList<MovieDb>, Void, ArrayList<Movie>> {
        protected ArrayList<Movie> doInBackground(ArrayList<MovieDb>... movies) {
            try {
                ArrayList<Movie> results = new ArrayList<>();
                for(MovieDb movie : movies[0]) {
                    // If movie in local DB, populate Movie Details with local DB data
                    if (movieDbHelper.checkMovieExists(movie.getId())) {
                        results.add(movieDbHelper.getMovieById(movie.getId()));
                    } else {
                        // If not in local DB, get info from online DB
                        TheMovieDbApi api = new TheMovieDbApi(getActivity().getResources().getString(R.string.apiKey));
                        // Need to explicitly request releases and cast info
                        results.add(new Movie(api.getMovieInfo(movie.getId(), null, "releases,casts")));
                    }
                }
                return results;
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

        protected void onPostExecute(ArrayList<Movie> results) {
            super.onPostExecute(results);
            if (results != null) {
                headerMP.setVisibility(View.VISIBLE);
                recyclerViewMP.setVisibility(View.VISIBLE);
                for(Movie movie : results) {
                    adapterMP.add(movie);
                }
            }
        }
    }
}
