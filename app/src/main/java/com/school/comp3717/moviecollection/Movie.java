package com.school.comp3717.moviecollection;

public class Movie {

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

    // Creating a movie object from app database
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

    // Creating a movie object from online database
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