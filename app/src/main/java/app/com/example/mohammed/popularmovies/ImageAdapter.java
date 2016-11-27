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

import JavaBeans.Movie;


public class ImageAdapter extends ArrayAdapter<Movie> {
    private static final String LOG_TAG = ImageAdapter.class.getSimpleName();

    public ImageAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);
        View v;
        if (null == convertView) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.grid_item, parent, false);
        }else{
            v = convertView;
        }
        ImageView iv = (ImageView)v.findViewById(R.id.poster_img);

        Picasso.with(getContext())
                .load(movie.getPoster())
                .error(R.drawable.default_img)
                .fit()
                .into(iv);

        TextView name = (TextView)v.findViewById(R.id.movie_name);
        name.setText(movie.getTitle());
        /*
        if(movie.isFavMovie()){
            name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_after_16, 0, 0, 0);
        }*/

        return v;
    }
}