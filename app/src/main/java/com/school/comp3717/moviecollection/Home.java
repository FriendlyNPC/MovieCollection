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
import android.widget.TextView;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.Discover;
import com.omertron.themoviedbapi.model.MovieDb;
import com.omertron.themoviedbapi.results.TmdbResultsList;
import com.school.comp3717.moviecollection.tools.RecyclerAdapter;
import com.school.comp3717.moviecollection.tools.RecyclerItemClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    private static final int              LIST_SIZE   = 8;
    private static final int              WEEK        = 7;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");

    private MovieDbHelper    movieDbHelper;
    private RecyclerAdapter  adapterNR;
    private RecyclerAdapter  adapterMP;
    private RecyclerView     recyclerViewNR;
    private RecyclerView     recyclerViewMP;
    private TextView         headerNR;
    private TextView         headerMP;

    public Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        movieDbHelper = new MovieDbHelper(getActivity());

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
        ArrayList<Movie> newReleases    = new ArrayList<>();
        ArrayList<Movie> mostPopular    = new ArrayList<>();
        recyclerViewNR                  = (RecyclerView) rootView.findViewById(R.id.recyclerViewNR);
        recyclerViewMP                  = (RecyclerView) rootView.findViewById(R.id.recyclerViewMP);
        adapterNR                       = new RecyclerAdapter(newReleases);
        adapterMP                       = new RecyclerAdapter(mostPopular);
        headerNR                        = (TextView) rootView.findViewById(R.id.newReleasesHeader);
        headerMP                        = (TextView) rootView.findViewById(R.id.mostPopularHeader);

        setRecyclerView(recentlyAdded, recyclerViewRA, adapterRA, headerRA, false);
        setRecyclerView(justWatched,   recyclerViewJW, adapterJW, headerJW, false);
        setRecyclerView(newReleases,   recyclerViewNR, adapterNR, headerNR, true);
        setRecyclerView(mostPopular,   recyclerViewMP, adapterMP, headerMP, true);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        QueryNewReleasesTask newReleasesTask = new QueryNewReleasesTask();
        newReleasesTask.execute(getDiscoverObject());
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

    private Discover getDiscoverObject() {
        Date     today          = new Date();
        Calendar calendar       = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DATE, -WEEK); // subtract a week
        Date     oneWeekAgo     = calendar.getTime();

        int      page           = 0;
        String   language       = "en";
        String   sortBy         = "popularity.desc";
        boolean  includeAdult   = false;
        int      year           = Integer.valueOf(YEAR_FORMAT.format(today));
        int      minVoteCount   = 0;
        float    minVoteAverage = 0;
        String   withGenres     = null;
        String   minReleaseDate = DATE_FORMAT.format(oneWeekAgo);
        String   maxReleaseDate = DATE_FORMAT.format(today);
        String   certCountry    = null;
        String   certLte        = null;
        String   withCompanies  = null;


        return new Discover().page(page)
                             .language(language)
                             .sortBy(sortBy)
                             .includeAdult(includeAdult)
                             .year(year)
                             .primaryReleaseYear(year)
                             .voteCountGte(minVoteCount)
                             .voteAverageGte(minVoteAverage)
                             .withGenres(withGenres)
                             .releaseDateGte(minReleaseDate)
                             .releaseDateLte(maxReleaseDate)
                             .certificationCountry(certCountry)
                             .certificationLte(certLte)
                             .withCompanies(withCompanies);
    }

    private class QueryNewReleasesTask extends AsyncTask<Discover, Void, TmdbResultsList<MovieDb>> {
        protected TmdbResultsList<MovieDb> doInBackground(Discover... discover) {
            try {
                TheMovieDbApi movieDB = new TheMovieDbApi(getActivity().getResources().getString(R.string.apiKey));
                return movieDB.getDiscover(discover[0]);
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
                GetNewReleaseInfoTask infoTask = new GetNewReleaseInfoTask();
                infoTask.execute(movieResults);
            }
        }
    }

    private class GetNewReleaseInfoTask extends AsyncTask<ArrayList<MovieDb>, Void, ArrayList<Movie>> {
        protected ArrayList<Movie> doInBackground(ArrayList<MovieDb>... movies) {
            try {
                ArrayList<Movie> results = new ArrayList<>();
                //ArrayList<MovieDb> movies = movieList[0];
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
                headerNR.setVisibility(View.VISIBLE);
                recyclerViewNR.setVisibility(View.VISIBLE);
                for(Movie movie : results) {
                    adapterNR.add(movie);
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
                //ArrayList<MovieDb> movies = movieList[0];
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
