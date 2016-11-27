package app.com.example.mohammed.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.facebook.stetho.Stetho;

import JavaBeans.Movie;

public class MainActivity extends AppCompatActivity implements MovieListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    boolean isTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MoviesFragment mMoviesFragment = new MoviesFragment();
        // set the activity to be a listener of the fragment
        mMoviesFragment.setMovieListener(this);

        if (getSupportFragmentManager().findFragmentById(R.id.movies_layout) != null) {
            Log.d(LOG_TAG, "--> layout found");
            // delete
            getSupportFragmentManager().beginTransaction()
                    .remove(getSupportFragmentManager().findFragmentById(R.id.movies_layout)).commit();
            // then add
            getSupportFragmentManager().beginTransaction().add(R.id.movies_layout, mMoviesFragment, "").commit();

        }else{
            Log.d(LOG_TAG, "--> layout not found");
            getSupportFragmentManager().beginTransaction().add(R.id.movies_layout, mMoviesFragment, "").commit();
        }




        // check if two pane
        if (null != findViewById(R.id.details_layout)) {
            isTwoPane = true;
            Log.d(LOG_TAG, "tablet mode");
        } else {
            Log.d(LOG_TAG, "phone mode");
        }


        Stetho.initializeWithDefaults(this);

    }

    @Override
    public void setSelectedMovie(Movie m) {
        // case 1 pane
        if (!isTwoPane) {

            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("from_main_movie1", m);
            startActivity(intent);
        } else {
            // case 2 pane
            DetailsFragment mDetailsFragment = new DetailsFragment();
            Bundle extras = new Bundle();
            extras.putSerializable("from_main_movie2", m);
            mDetailsFragment.setArguments(extras);
            getSupportFragmentManager().beginTransaction().replace(R.id.details_layout, mDetailsFragment, "").commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isTwoPane){
            setTitle("Popular Movies");
        }
    }
}
