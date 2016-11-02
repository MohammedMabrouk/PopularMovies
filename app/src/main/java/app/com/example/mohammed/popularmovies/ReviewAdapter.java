package app.com.example.mohammed.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mohammed on 11/1/2016.
 */

public class ReviewAdapter extends ArrayAdapter<Review> {

    public ReviewAdapter(Activity context, List<Review> reviews) {
        super(context, 0, reviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Review review = getItem(position);
        if (null == convertView) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_item, parent, false);
        }

        TextView author = (TextView)convertView.findViewById(R.id.review_author);
        author.setText(review.getAuthor());

        TextView content = (TextView)convertView.findViewById(R.id.review_content);
        content.setText(review.getContent());

        return convertView;
    }
}