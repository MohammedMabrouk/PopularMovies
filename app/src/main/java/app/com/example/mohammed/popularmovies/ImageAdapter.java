package app.com.example.mohammed.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class ImageAdapter extends ArrayAdapter<Movie> {

    public ImageAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);
        if (null == convertView) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item, parent, false);
        }
        ImageView iv = (ImageView)convertView.findViewById(R.id.poster_img);

        Picasso.with(getContext())
                .load(movie.getPoster())
                .fit() // will explain later
                .into(iv);

        TextView name = (TextView)convertView.findViewById(R.id.movie_name);
        name.setText(movie.getTitle());

        TextView rate = (TextView)convertView.findViewById(R.id.rating);
        rate.setText(movie.getVoteAverage());

        return convertView;
    }
}