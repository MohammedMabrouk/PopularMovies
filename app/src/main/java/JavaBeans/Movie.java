package JavaBeans;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Mohammed on 10/21/2016.
 */

public class Movie implements Serializable {

    private String title;
    private String poster;
    private String backdrop;
    private String overview;
    private String voteAverage;
    private String releaseDate;
    private String id;
    private boolean favMovie;
    private ArrayList<String> genres;


    public Movie(){
        title = "";
        poster = "";
        backdrop = "";
        overview = "";
        voteAverage = "";
        releaseDate = "";
        id = "";
        favMovie = false;
        genres = new ArrayList<>();
    }
    public Movie(String id, String title, String poster, String backdrop, String overview, String voteAverage, String releaseDate, boolean favMovie){
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.backdrop = backdrop;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.favMovie = favMovie;
    }

    public String getId(){return id;}

    public String getTitle(){
        return title;
    }

    public String getPoster(){
        return poster;
    }
    public String getBackdrop(){
        return backdrop;
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

    public boolean isFavMovie(){
        return favMovie;
    }

    public void setFavMovie(Boolean b){
        favMovie = b;
    }
    public void setVoteAverage(String s){voteAverage = s;}

    public void setGenres(ArrayList<String> genres){
        this.genres = genres;
    }

    public ArrayList<String> getGenres(){
        return this.genres;
    }


}
