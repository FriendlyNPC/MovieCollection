package com.school.comp3717.moviecollection.collection;

import android.os.Parcel;
import android.os.Parcelable;

import com.school.comp3717.moviecollection.MovieDbContract;
import com.school.comp3717.moviecollection.MovieDbHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Devan on 2015-03-12.
 */
public class MovieFilter implements Parcelable{
    public static final int     MAX_RUNTIME = 185;

    private SortOrder           sort;
    private List<String>        genres;
    private Boolean             isAscending;
    private String              maxRating;
    private int                 maxRuntime;
    private String                 minDate;
    private String                 maxDate;

    public MovieFilter(){
        genres = new ArrayList<String>();
        sort = SortOrder.TITLE;
        isAscending = true;
        maxRating = "No Preference";
        this.maxRuntime = MAX_RUNTIME;
        this.minDate = "";
        this.maxDate = "";
    }

    public void setSort(SortOrder sort) {
        this.sort = sort;
    }

    public void setSort(int sortOption) {
        switch (sortOption){
            case 0:
                this.sort = SortOrder.TITLE;
                break;
            case 1:
                this.sort = SortOrder.RUNTIME;
                break;
            case 2:
                this.sort = SortOrder.RELEASE_DATE;
                break;
            default:
                this.sort = SortOrder.TITLE;
                break;
        }
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public void setIsAscending(Boolean isAscending) {
        this.isAscending = isAscending;
    }

    public void setMaxRating(String maxRating) {
        switch (maxRating){
            case "NC-17":
            case "R":
            case "PG-13":
            case "PG":
            case "G":
                this.maxRating = maxRating;
                break;
            default:
                this.maxRating = MovieDbHelper.NO_PREF;
                break;
        }
    }

    public void setMaxRuntime(int maxRuntime) {
        this.maxRuntime = maxRuntime;
    }

    public void setMinDate(String minDate) {
        this.minDate = minDate;
    }

    public void setMaxDate(String maxDate) {
        this.maxDate = maxDate;
    }

    public String getSort() {
        return sort.toString();
    }

    public SortOrder getSortEnum(){
        return sort;
    }

    public String getGenres() {
        StringBuilder sb = new StringBuilder();

        if (genres.isEmpty() || genres.contains(MovieDbHelper.NO_PREF) ){
            return MovieDbHelper.NO_PREF;
        }else {
            String delim = MovieDbContract.MovieTable.GENRE + " LIKE ";
            for (String i : genres) {
                sb.append(delim).append("'%").append(i).append("%' ");
                delim = "OR " + MovieDbContract.MovieTable.GENRE + " LIKE ";
            }
        }

        return sb.toString();
    }

    public Boolean isAscending() {
        return isAscending;
    }

    public String isAscendingString(){
        return (isAscending) ? "ASC":"DESC";
    }

    public String getMaxRating() {
        return maxRating;
    }

    public int getMaxRuntime() {
        return maxRuntime;
    }

    public String getMinDate() {
        return minDate;
    }

    public String getMaxDate() {
        return maxDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sort.name());
        dest.writeStringList(genres);
        dest.writeByte((byte) (isAscending ? 1 : 0));
        dest.writeString(maxRating);
        dest.writeInt(maxRuntime);
        dest.writeString(minDate);
        dest.writeString(maxDate);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MovieFilter createFromParcel(Parcel in) {
            MovieFilter filter = new MovieFilter();
            filter.sort = SortOrder.valueOf(in.readString());
            in.readStringList(filter.genres);
            filter.isAscending = in.readByte() != 0;
            filter.maxRating = in.readString();
            filter.maxRuntime = in.readInt();
            filter.minDate = in.readString();
            filter.maxDate = in.readString();
            return filter;
        }
        public MovieFilter[] newArray(int size) {
            return new MovieFilter[size];
        }
    };

    public enum SortOrder{
        TITLE(MovieDbContract.MovieTable.TITLE),
        RUNTIME(MovieDbContract.MovieTable.RUNTIME),
        RELEASE_DATE(MovieDbContract.MovieTable.RELEASE_DATE);

        private final String name;
        private SortOrder(String s){
            name = s;
        }

        public boolean equalsName(String otherName){
            return (otherName == null) ? false : name.equals(otherName);
        }

        public String toString(){
            return name;
        }
    }
}
