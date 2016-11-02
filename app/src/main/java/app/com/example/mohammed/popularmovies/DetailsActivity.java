package app.com.example.mohammed.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import app.com.example.mohammed.popularmovies.databinding.ActivityDetailsBinding;

public class DetailsActivity extends AppCompatActivity {
    private final String LOG_TAG = DetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_details);

        ActivityDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        // get intent data
        Intent intent = this.getIntent();
        String id = intent.getStringExtra("id");
        String title = intent.getStringExtra("movie_name");
        String poster = intent.getStringExtra("poster");
        String date = intent.getStringExtra("date");
        String overview = intent.getStringExtra("overview");
        String rate = intent.getStringExtra("rate");

        Movie movie = new Movie(id, title, poster, overview, rate + "/10", date);
        binding.setMovie(movie);

        ImageView view = (ImageView)findViewById(R.id.poster);

        Picasso.with(this).load(poster).into(view);

    }


}
