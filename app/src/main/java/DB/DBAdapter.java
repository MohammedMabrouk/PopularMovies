package DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import JavaBeans.Movie;
import JavaBeans.Review;
import JavaBeans.Trailer;

/**
 * Created by Mohammed on 11/7/2016.
 */
public class DBAdapter {
    DBHelper dbhelper;

    public DBAdapter(Context context) {
        dbhelper = new DBHelper(context);
    }

    // movies
    public long addMovie(Movie movie, ArrayList<Trailer> trailers, ArrayList<Review> reviews) {
        Log.d("wwe", "entered ...");
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        // insert movie
        values.put("movie_id", movie.getId());
        values.put("title", movie.getTitle());
        values.put("poster", movie.getPoster());
        values.put("overview", movie.getOverview());
        values.put("vote", movie.getVoteAverage());
        values.put("release_date", movie.getReleaseDate());


        // insert trailers
        if (!trailers.isEmpty()) {
            Log.d("wwe", "reviews : " + trailers.size());
            for(Trailer t : trailers){
                addTrailer(t, movie.getId());
            }
        }

        // insert reviews
        if (!reviews.isEmpty()) {
            Log.d("wwe", "reviews : " + reviews.size());
            for (Review r : reviews) {
               addReview(r, movie.getId());
            }
        }

        return db.insert(DBHelper.FAV_TABLE_TNAME, null, values);
    }

    public long addTrailer(Trailer trailer, String movie_id) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBHelper.TRAILER_TABLE_NAME, trailer.getName());
        values.put(DBHelper.TRAILER_TABLE_MOVIE_ID, movie_id);
        values.put(DBHelper.TRAILER_TABLE_SITE, trailer.getSite());
        values.put(DBHelper.TRAILER_TABLE_SIZE, trailer.getSize());
        values.put(DBHelper.TRAILER_TABLE_URL, trailer.getUrl());

        return db.insert(DBHelper.TRAILER_TABLE_TNAME, null, values);
    }

    public long addReview(Review review, String movie_id) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBHelper.REVIEW_TABLE_MOVIE_ID, movie_id);
        values.put(DBHelper.REVIEW_TABLE_REVIEW_ID, review.getId());
        values.put(DBHelper.REVIEW_TABLE_AUTHOR, review.getAuthor());
        values.put(DBHelper.REVIEW_TABLE_CONTENT, review.getContent());

        return db.insert(DBHelper.REVIEW_TABLE_TNAME, null, values);
    }

    public Movie getMovie(String movie_id) {
        SQLiteDatabase db = dbhelper.getReadableDatabase();

        // columns to select
        String[] projection =
                {DBHelper.FAV_TABLE_MOVIE_ID,
                        DBHelper.FAV_TABLE_TITLE,
                        DBHelper.FAV_TABLE_POSTER,
                        DBHelper.FAV_TABLE_OVERVIEW,
                        DBHelper.FAV_TABLE_VOTE,
                        DBHelper.FAV_TABLE_RELEASE_DATE
                };

        // Filter results WHERE "movie_id" = 'id'
        String selection = DBHelper.FAV_TABLE_MOVIE_ID + " = ?";
        String[] selectionArgs = {movie_id};

        Cursor c = db.query(
                DBHelper.FAV_TABLE_TNAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (c != null && c.moveToFirst()) {
            int index1 = c.getColumnIndex(DBHelper.FAV_TABLE_MOVIE_ID);
            int index2 = c.getColumnIndex(DBHelper.FAV_TABLE_TITLE);
            int index3 = c.getColumnIndex(DBHelper.FAV_TABLE_POSTER);
            int index4 = c.getColumnIndex(DBHelper.FAV_TABLE_OVERVIEW);
            int index5 = c.getColumnIndex(DBHelper.FAV_TABLE_VOTE);
            int index6 = c.getColumnIndex(DBHelper.FAV_TABLE_RELEASE_DATE);
            c.moveToFirst();
            Movie m = new Movie(
                    c.getString(index1),
                    c.getString(index2),
                    c.getString(index3),
                    c.getString(index4),
                    c.getString(index5),
                    c.getString(index6),
                    false
            );
            c.close();
            return m;
        }


        return null;
    }

    public ArrayList<Review> getReviews(String movie_id) {
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        ArrayList<Review> result = new ArrayList<>();
        // columns to select
        String[] projection =
                {DBHelper.REVIEW_TABLE_REVIEW_ID,
                        DBHelper.REVIEW_TABLE_MOVIE_ID,
                        DBHelper.REVIEW_TABLE_AUTHOR,
                        DBHelper.REVIEW_TABLE_CONTENT
                };

        // Filter results WHERE "movie_id" = 'id'
        String selection = DBHelper.REVIEW_TABLE_MOVIE_ID + " = ?";
        String[] selectionArgs = {movie_id};

        Cursor c = db.query(
                DBHelper.REVIEW_TABLE_TNAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        int counter = 0;
        int index1 = c.getColumnIndex(DBHelper.REVIEW_TABLE_REVIEW_ID);
        int index2 = c.getColumnIndex(DBHelper.REVIEW_TABLE_AUTHOR);
        int index3 = c.getColumnIndex(DBHelper.REVIEW_TABLE_CONTENT);

        while (c.moveToNext()) {
            counter++;
            Review r = new Review(
                    c.getString(index1),
                    c.getString(index2),
                    c.getString(index3)
            );

            result.add(r);
        }
        c.close();


        return result;
    }
    public ArrayList<Trailer> getTrailers(String movie_id) {
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        ArrayList<Trailer> result = new ArrayList<>();
        // columns to select
        String[] projection =
                {DBHelper.TRAILER_TABLE_NAME,
                        DBHelper.TRAILER_TABLE_MOVIE_ID,
                        DBHelper.TRAILER_TABLE_SITE,
                        DBHelper.TRAILER_TABLE_SIZE,
                        DBHelper.TRAILER_TABLE_URL
                };

        // Filter results WHERE "movie_id" = 'id'
        String selection = DBHelper.TRAILER_TABLE_MOVIE_ID + " = ?";
        String[] selectionArgs = {movie_id};

        Cursor c = db.query(
                DBHelper.TRAILER_TABLE_TNAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        int counter = 0;
        int index1 = c.getColumnIndex(DBHelper.TRAILER_TABLE_NAME);
        int index2 = c.getColumnIndex(DBHelper.TRAILER_TABLE_SITE);
        int index3 = c.getColumnIndex(DBHelper.TRAILER_TABLE_SIZE);
        int index4 = c.getColumnIndex(DBHelper.TRAILER_TABLE_URL);

        while (c.moveToNext()) {
            counter++;
            Trailer t = new Trailer(
                    c.getString(index1),
                    c.getString(index2),
                    c.getString(index3),
                    c.getString(index4)
            );

            result.add(t);
        }
        c.close();


        return result;
    }


    public ArrayList<Movie> getAllMovies() {
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        ArrayList<Movie> result = new ArrayList<>();

        // columns to select
        String[] projection =
                {DBHelper.FAV_TABLE_MOVIE_ID,
                        DBHelper.FAV_TABLE_TITLE,
                        DBHelper.FAV_TABLE_POSTER,
                        DBHelper.FAV_TABLE_OVERVIEW,
                        DBHelper.FAV_TABLE_VOTE,
                        DBHelper.FAV_TABLE_RELEASE_DATE
                };


        Cursor c = db.query(
                DBHelper.FAV_TABLE_TNAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        int counter = 0;
        int index1 = c.getColumnIndex(DBHelper.FAV_TABLE_MOVIE_ID);
        int index2 = c.getColumnIndex(DBHelper.FAV_TABLE_TITLE);
        int index3 = c.getColumnIndex(DBHelper.FAV_TABLE_POSTER);
        int index4 = c.getColumnIndex(DBHelper.FAV_TABLE_OVERVIEW);
        int index5 = c.getColumnIndex(DBHelper.FAV_TABLE_VOTE);
        int index6 = c.getColumnIndex(DBHelper.FAV_TABLE_RELEASE_DATE);
        while (c.moveToNext()) {
            counter++;

            Movie m = new Movie(
                    c.getString(index1),
                    c.getString(index2),
                    c.getString(index3),
                    c.getString(index4),
                    c.getString(index5),
                    c.getString(index6),
                    true
            );
            result.add(m);
        }
        c.close();


        return (counter == 0) ? null : result;
    }

    public int deleteMovie(String movie_id) {
        int x = 0 ;
        SQLiteDatabase db = dbhelper.getReadableDatabase();

        // Define 'where' part of query.
        String selection = DBHelper.FAV_TABLE_MOVIE_ID + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {movie_id};
        // Issue SQL statement.
        x += db.delete(DBHelper.FAV_TABLE_TNAME, selection, selectionArgs);
        x += db.delete(DBHelper.REVIEW_TABLE_TNAME, selection, selectionArgs);
        x += db.delete(DBHelper.TRAILER_TABLE_TNAME, selection, selectionArgs);
        return x;
    }

    class DBHelper extends SQLiteOpenHelper {
        // The DB
        private static final String DATABASE_NAME = "PopularMovies";
        private static final int DATABASE_VERSION = 2;
        // favMovies table
        private static final String FAV_TABLE_TNAME = "favMovie";
        //private static final String FAV_TABLE_ID = "_id";
        private static final String FAV_TABLE_MOVIE_ID = "movie_id";
        private static final String FAV_TABLE_TITLE = "title";
        private static final String FAV_TABLE_POSTER = "poster";
        private static final String FAV_TABLE_OVERVIEW = "overview";
        private static final String FAV_TABLE_VOTE = "vote";
        private static final String FAV_TABLE_RELEASE_DATE = "release_date";
        private static final String FAV_TABLE_CREATE = "CREATE TABLE " + FAV_TABLE_TNAME +
                "("+ FAV_TABLE_MOVIE_ID + " VARCHAR(255), " + FAV_TABLE_TITLE + " VARCHAR(255), " + FAV_TABLE_POSTER + " VARCHAR(255), " +
                FAV_TABLE_OVERVIEW + " TEXT, " + FAV_TABLE_VOTE + " VARCHAR(255), " + FAV_TABLE_RELEASE_DATE + " VARCHAR(255));";
        // review table
        private static final String REVIEW_TABLE_TNAME = "review";
        //private static final String REVIEW_TABLE_ID = "_id";
        private static final String REVIEW_TABLE_REVIEW_ID = "review_id";
        private static final String REVIEW_TABLE_MOVIE_ID = "movie_id";
        private static final String REVIEW_TABLE_AUTHOR = "author";
        private static final String REVIEW_TABLE_CONTENT = "content";
        public static final String REVIEW_TABLE_CREATE = "CREATE TABLE " + REVIEW_TABLE_TNAME +
                "(" + REVIEW_TABLE_REVIEW_ID + " VARCHAR(255), " + REVIEW_TABLE_MOVIE_ID + " INTEGER, " +
                REVIEW_TABLE_AUTHOR + " VARCHAR(255), " + REVIEW_TABLE_CONTENT + " TEXT);";
        // trailer table
        private static final String TRAILER_TABLE_TNAME = "trailer";
        //private static final String TRAILER_TABLE_ID = "_id";
        //private static final String TRAILER_TABLE_TRAILER_ID = "trailer_id";
        private static final String TRAILER_TABLE_MOVIE_ID = "movie_id";
        private static final String TRAILER_TABLE_NAME = "name";
        private static final String TRAILER_TABLE_SITE = "site";
        private static final String TRAILER_TABLE_SIZE = "size";
        private static final String TRAILER_TABLE_URL = "url";
        private static final String TRAILER_TABLE_CREATE = "CREATE TABLE " + TRAILER_TABLE_TNAME +
                "(" + TRAILER_TABLE_NAME + " VARCHAR(255), " + TRAILER_TABLE_MOVIE_ID + " VARCHAR(255), "+
                TRAILER_TABLE_SITE + " VARCHAR(255), " + TRAILER_TABLE_SIZE + " VARCHAR(255), " + TRAILER_TABLE_URL + " VARCHAR(255));";


        private final String LOG_TAG = DBHelper.class.getSimpleName();
        private Context context;

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
            Log.d(LOG_TAG, "constructor called");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL(FAV_TABLE_CREATE);
                db.execSQL(TRAILER_TABLE_CREATE);
                db.execSQL(REVIEW_TABLE_CREATE);
                Log.d(LOG_TAG, "tables created");
            } catch (Exception e) {
                Log.d(LOG_TAG, "" + e);
            }


        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // if any changes in schema
            db.execSQL("DROP TABLE IF EXISTS " + FAV_TABLE_TNAME);
            db.execSQL("DROP TABLE IF EXISTS " + REVIEW_TABLE_TNAME);
            db.execSQL("DROP TABLE IF EXISTS " + TRAILER_TABLE_TNAME);
            onCreate(db);
            Log.d(LOG_TAG, "on upgrade");
        }
    }
}
