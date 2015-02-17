package com.school.comp3717.moviecollection;

import android.os.Parcel;
import android.os.Parcelable;

import com.omertron.themoviedbapi.model.Genre;
import com.omertron.themoviedbapi.model.MovieDb;
import com.omertron.themoviedbapi.model.PersonCrew;
import com.omertron.themoviedbapi.model.ProductionCompany;
import com.omertron.themoviedbapi.model.ReleaseInfo;

import java.util.List;

public class Movie implements Parcelable {

    private int movieId;
    private String title;
    private String releaseDate;
    private String filmRating;
    private int runtime;
    private double voteAverage;
    private int voteCount;
    private String tagLine;
    private String synopsis;
    private String posterUrl;
    private String genre;
    private String director;
    private String studio;
    private double popularity;
    private long budget;
    private long revenue;
    private int myRating;
    private String myReview;
    private String lastWatched;
    private int watchCount;
    private int isLoaned;

    // Create a movie object from app database
    public Movie(int movieId,
                 String title,
                 String releaseDate,
                 String filmRating,
                 int runtime,
                 double voteAverage,
                 int voteCount,
                 String tagLine,
                 String synopsis,
                 String posterUrl,
                 String genre,
                 String director,
                 String studio,
                 double popularity,
                 long budget,
                 long revenue,
                 int myRating,
                 String myReview,
                 String lastWatched,
                 int watchCount,
                 int isLoaned) {
        this.movieId = movieId;
        this.title = title;
        this.releaseDate = releaseDate;
        this.filmRating = filmRating;
        this.runtime = runtime;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.tagLine = tagLine;
        this.synopsis = synopsis;
        this.posterUrl = posterUrl;
        this.genre = genre;
        this.director = director;
        this.studio = studio;
        this.popularity = popularity;
        this.budget = budget;
        this.revenue = revenue;
        this.myRating = myRating;
        this.myReview = myReview;
        this.lastWatched = lastWatched;
        this.watchCount = watchCount;
        this.isLoaned = isLoaned;
    }

    // TODO: Troubleshoot getReleaseRating, genreToString, getPersonFromCrew, studioToString
    // Create a movie object from online database using MovieDb wrapper
    public Movie(MovieDb source) {
        String country = "US";
        String job = "director";

        this.movieId = source.getId();
        this.title = source.getTitle();
        this.releaseDate = source.getReleaseDate();
        this.filmRating = ""; //getReleaseRating(source.getReleases(), country);
        this.runtime = source.getRuntime();
        this.voteAverage = source.getVoteAverage();
        this.voteCount = source.getVoteCount();
        this.tagLine = source.getTagline();
        this.synopsis = source.getOverview();
        this.posterUrl = source.getPosterPath();
        this.genre = "";//genreToString(source.getGenres());
        this.director = "John Doe"; //getPersonFromCrew(source.getCrew(), job);
        this.studio = ""; //studioToString(source.getProductionCompanies());
        this.popularity = source.getPopularity();
        this.budget = source.getBudget();
        this.revenue = source.getRevenue();

        this.myRating = 0;
        this.myReview = null;
        this.lastWatched = null;
        this.watchCount = 0;
        this.isLoaned = 0;
    }

    // TODO: Decide whether we should store this in its own genre table
    // Returns genres in a tab-delimited string
    private String genreToString(List<Genre> genres) {
        StringBuilder genreBuilder = new StringBuilder();
        for (Genre genre : genres) {
            genreBuilder.append(genre.getName());
            genreBuilder.append("\t");
        }
        return genreBuilder.toString();
    }

    // Returns studios in a tab-delimited string
    private String studioToString(List<ProductionCompany> studios) {
        StringBuilder studioBuilder = new StringBuilder();
        for (ProductionCompany studio : studios) {
            studioBuilder.append(studio.getName());
            studioBuilder.append("\t");
        }
        return studioBuilder.toString();
    }

    // Returns country's parental rating
    private String getReleaseRating(List<ReleaseInfo> releases, String country) {
        String rating = null;
        for (ReleaseInfo release : releases) {
            if (release.getCountry().equalsIgnoreCase(country)) {
                rating = release.getCertification();
            }
        }
        return rating;
    }

    // Returns person(s) in a given job in a tab-delimited string
    private String getPersonFromCrew(List<PersonCrew> crew, String job) {
        StringBuilder jobBuilder = new StringBuilder();
        for (PersonCrew person : crew) {
            if (person.getJob().equalsIgnoreCase(job)) {
                jobBuilder.append(person.getName());
                jobBuilder.append("\t");
            }
        }
        return jobBuilder.toString();
    }

    // Required for Parcelable
    public int describeContents() {
        return 0;
    }

    // Write object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(movieId);
        out.writeString(title);
        out.writeString(releaseDate);
        out.writeString(filmRating);
        out.writeInt(runtime);
        out.writeDouble(voteAverage);
        out.writeInt(voteCount);
        out.writeString(tagLine);
        out.writeString(synopsis);
        out.writeString(posterUrl);
        out.writeString(genre);
        out.writeString(director);
        out.writeString(studio);
        out.writeDouble(popularity);
        out.writeLong(budget);
        out.writeLong(revenue);
        out.writeInt(myRating);
        out.writeString(myReview);
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
        this.releaseDate = in.readString();
        this.filmRating = in.readString();
        this.runtime = in.readInt();
        this.voteAverage = in.readDouble();
        this.voteCount = in.readInt();
        this.tagLine = in.readString();
        this.synopsis = in.readString();
        this.posterUrl = in.readString();
        this.genre = in.readString();
        this.director = in.readString();
        this.studio = in.readString();
        this.popularity = in.readDouble();
        this.budget = in.readLong();
        this.revenue = in.readLong();
        this.myRating = in.readInt();
        this.myReview = in.readString();
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

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getFilmRating() {
        return filmRating;
    }

    public void setFilmRating(String filmRating) {
        this.filmRating = filmRating;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public String getTagLine() {
        return tagLine;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
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

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public long getBudget() {
        return budget;
    }

    public void setBudget(long budget) {
        this.budget = budget;
    }

    public long getRevenue() {
        return revenue;
    }

    public void setRevenue(long revenue) {
        this.revenue = revenue;
    }

    public int getMyRating() {
        return myRating;
    }

    public void setMyRating(int myRating) {
        this.myRating = myRating;
    }

    public String getMyReview() {
        return myReview;
    }

    public void setMyReview(String myReview) {
        this.myReview = myReview;
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