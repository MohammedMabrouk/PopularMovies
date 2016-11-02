package app.com.example.mohammed.popularmovies;

/**
 * Created by Mohammed on 10/30/2016.
 */

public class Review {
    private String id;
    private String author;
    private String content;

    Review(String id, String author, String content){
        this.id = id;
        this.author = author;
        this.content = content;
    }

    public String getId(){return id;}
    public String getAuthor(){return author;}
    public String getContent(){return content;}
}
