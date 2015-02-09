package com.school.comp3717.moviecollection;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.Artwork;
import com.omertron.themoviedbapi.model.MovieDb;
import com.omertron.themoviedbapi.model.PersonCrew;
import com.omertron.themoviedbapi.results.TmdbResultsList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetails extends Fragment {

    MovieDb movie;
    TextView title;
    TextView year;
    TextView synopsis;
    TextView director;
    ImageView poster;

    public MovieDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        poster = (ImageView) rootView.findViewById(R.id.moviePoster);
        title = (TextView) rootView.findViewById(R.id.movieTitle);
        year = (TextView) rootView.findViewById(R.id.movieYear);
        synopsis = (TextView) rootView.findViewById(R.id.synopsisBody);
        director = (TextView) rootView.findViewById(R.id.movieDirector);

        return rootView;
    }


    public void setMovie(MovieDb movie){
        this.movie = movie;
        setDetails();
    }

    private void setDetails(){
        title.setText(movie.getTitle());
        year.setText(movie.getReleaseDate());
        String directedBy = "Directed by";
        int numDirectors = 0;
        for(PersonCrew crew : movie.getCrew()){
            if (crew.getJob().equalsIgnoreCase("director")){
                directedBy += numDirectors == 0 ? " " : ", ";
                directedBy += crew.getName();
                numDirectors++;
            }
        }
        synopsis.setText(movie.getOverview());

    }

    public void setPoster(Artwork poster){

    }

    public class getPosterTask extends AsyncTask<Integer,Void,TmdbResultsList<Artwork>> {

        @Override
        protected TmdbResultsList<Artwork> doInBackground(Integer... params) {
            try {
                TheMovieDbApi movieDB = new TheMovieDbApi(getActivity().getResources().getString(R.string.apiKey));
                return movieDB.getMovieImages(params[0], null);
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

        @Override
        protected void onPostExecute(TmdbResultsList<Artwork> result) {

        }
    }

}
