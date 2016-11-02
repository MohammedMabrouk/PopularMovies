package app.com.example.mohammed.popularmovies;

/**
 * Created by Mohammed on 10/21/2016.
 */

public class Movie {

    private String title;
    private String poster;
    private String overview;
    private String voteAverage;
    private String releaseDate;
    private String id;

    public Movie(String id, String title, String poster, String overview, String voteAverage, String releaseDate){
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    public String getId(){return id;}

    public String getTitle(){
        return title;
    }

    public String getPoster(){
        return poster;
    }

    public String getOverview(){
        return overview;
    }

    public String getVoteAverage(){
        return voteAverage;
    }

    public String getReleaseDate(){
        return releaseDate;
    }



}
