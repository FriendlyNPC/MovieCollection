package com.school.comp3717.moviecollection;

import android.os.Parcel;
import android.os.Parcelable;

import com.omertron.themoviedbapi.model.Genre;
import com.omertron.themoviedbapi.model.MovieDb;
import com.omertron.themoviedbapi.model.ProductionCompany;

import java.util.List;

public class Movie implements Parcelable {

    private static final int YEAR_LENGTH = 4;

    private int movieId;
    private String title;
    private int year;
    private String mpaaRating;
    private int runtime;
    private double criticScore;
    private double userScore;
    private String synopsis;
    private String posterUrl;
    private String genre;
    private String director;
    private String studio;
    private String lastWatched;
    private int watchCount;
    private int isLoaned;

    // Create a movie object from app database
    public Movie(int movieId,
                 String title,
                 int year,
                 String mpaaRating,
                 int runtime,
                 double criticScore,
                 double userScore,
                 String synopsis,
                 String posterUrl,
                 String genre,
                 String director,
                 String studio,
                 String lastWatched,
                 int watchCount,
                 int isLoaned) {
        this.movieId = movieId;
        this.title = title;
        this.year = year;
        this.mpaaRating = mpaaRating;
        this.runtime = runtime;
        this.criticScore = criticScore;
        this.userScore = userScore;
        this.synopsis = synopsis;
        this.posterUrl = posterUrl;
        this.genre = genre;
        this.director = director;
        this.studio = studio;
        this.lastWatched = lastWatched;
        this.watchCount = watchCount;
        this.isLoaned = isLoaned;
    }

    // Create a movie object from online database
    public Movie(int movieId,
                 String title,
                 int year,
                 String mpaaRating,
                 int runtime,
                 double criticScore,
                 String synopsis,
                 String posterUrl,
                 String genre,
                 String director,
                 String studio) {
        this.movieId = movieId;
        this.title = title;
        this.year = year;
        this.mpaaRating = mpaaRating;
        this.runtime = runtime;
        this.criticScore = criticScore;
        this.userScore = 0;
        this.synopsis = synopsis;
        this.posterUrl = posterUrl;
        this.genre = genre;
        this.director = director;
        this.studio = studio;
        this.lastWatched = null;
        this.watchCount = 0;
        this.isLoaned = 0;
    }

    // Create a movie object from online database using MovieDb wrapper
    public Movie(MovieDb source) {
        this.movieId = source.getId();
        this.title = source.getTitle();
        this.year = Integer.valueOf(source.getReleaseDate().substring(0, YEAR_LENGTH)); // TODO: Change to full release date (string)
        this.mpaaRating = ""; // TODO: Find a way to get rating
        this.runtime = source.getRuntime();
        this.criticScore = source.getVoteAverage(); // TODO: Change to "User Rating"
        this.userScore = 0; // TODO: Change to "My Rating"
        this.synopsis = source.getOverview();
        this.posterUrl = source.getPosterPath();
        this.genre = genreToString(source.getGenres());
        this.director = ""; // TODO: Find a way to get director
        this.studio = studioToString(source.getProductionCompanies());
        this.lastWatched = null;
        this.watchCount = 0;
        this.isLoaned = 0;
        // TODO: Add tag line, budget, popularity, revenue, vote count, original language?
    }

    // TODO: Decide whether we should store this in its own genre table
    private String genreToString(List<Genre> genres) {
        StringBuilder genreBuilder = new StringBuilder();
        for (Genre genre : genres)
        {
            genreBuilder.append(genre.getName());
            genreBuilder.append("\t");
        }
        return genreBuilder.toString();
    }

    private String studioToString(List<ProductionCompany> studios) {
        StringBuilder studioBuilder = new StringBuilder();
        for (ProductionCompany studio : studios)
        {
            studioBuilder.append(studio.getName());
            studioBuilder.append("\t");
        }
        return studioBuilder.toString();
    }


    // Required for Parcelable
    public int describeContents() {
        return 0;
    }

    // Write object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(movieId);
        out.writeString(title);
        out.writeInt(year);
        out.writeString(mpaaRating);
        out.writeInt(runtime);
        out.writeDouble(criticScore);
        out.writeDouble(userScore);
        out.writeString(synopsis);
        out.writeString(posterUrl);
        out.writeString(genre);
        out.writeString(director);
        out.writeString(studio);
        out.writeString(lastWatched);
        out.writeInt(watchCount);
        out.writeInt(isLoaned);
    }

    // This is used to regenerate the object; all Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    // Create movie object from Parcel
    private Movie(Parcel in) {
        this.movieId = in.readInt();
        this.title = in.readString();
        this.year = in.readInt();
        this.mpaaRating = in.readString();
        this.runtime = in.readInt();
        this.criticScore = in.readDouble();
        this.userScore = in.readDouble();
        this.synopsis = in.readString();
        this.posterUrl = in.readString();
        this.genre = in.readString();
        this.director = in.readString();
        this.studio = in.readString();
        this.lastWatched = in.readString();
        this.watchCount = in.readInt();
        this.isLoaned = in.readInt();
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMpaaRating() {
        return mpaaRating;
    }

    public void setMpaaRating(String mpaaRating) {
        this.mpaaRating = mpaaRating;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public double getCriticScore() {
        return criticScore;
    }

    public void setCriticScore(double criticScore) {
        this.criticScore = criticScore;
    }

    public double getUserScore() {
        return userScore;
    }

    public void setUserScore(double userScore) {
        this.userScore = userScore;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public String getLastWatched() {
        return lastWatched;
    }

    public void setLastWatched(String lastWatched) {
        this.lastWatched = lastWatched;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public void setWatchCount(int watchCount) {
        this.watchCount = watchCount;
    }

    public int isLoaned() {
        return isLoaned;
    }

    public void setLoaned(int isLoaned) {
        this.isLoaned = isLoaned;
    }
}