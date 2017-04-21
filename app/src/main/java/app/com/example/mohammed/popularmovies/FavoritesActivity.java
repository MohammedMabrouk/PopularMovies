package app.com.example.mohammed.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import JavaBeans.Movie;

public class FavoritesActivity extends AppCompatActivity implements MovieListener{

    boolean isTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);


        FavoritesFragment mFavoriteFragment = new FavoritesFragment();
        // set the activity to be a listener of the fragment
        mFavoriteFragment.setMovieListener(this);

        if (getSupportFragmentManager().findFragmentById(R.id.movies_layout) != null) {
            Log.d("testing", "--> layout found");
            // delete
            getSupportFragmentManager().beginTransaction()
                    .remove(getSupportFragmentManager().findFragmentById(R.id.movies_layout)).commit();
            // then add
            getSupportFragmentManager().beginTransaction().add(R.id.movies_layout, mFavoriteFragment, "").commit();

        }else{
            Log.d("testing", "--> layout not found");
            getSupportFragmentManager().beginTransaction().add(R.id.movies_layout, mFavoriteFragment, "").commit();
        }


        // check if two pane
        if(null != findViewById(R.id.details_layout)) {
            isTwoPane = true;
            Log.d("testing", "tablet mode");
        } else {
            Log.d("testing", "phone mode");
        }


    }

    @Override
    public void setSelectedMovie(Movie m) {
        // case 1 pane
        if (!isTwoPane) {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("from_fav_movie1", m);
            startActivity(intent);
        } else {
            // case 2 pane
            DetailsFragment mDetailsFragment = new DetailsFragment();
            Bundle extras = new Bundle();
            extras.putSerializable("from_fav_movie2", m);
            mDetailsFragment.setArguments(extras);
            getSupportFragmentManager().beginTransaction().replace(R.id.details_layout, mDetailsFragment, "").commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isTwoPane){
            setTitle("Favorite Movies");
        }
    }
}
