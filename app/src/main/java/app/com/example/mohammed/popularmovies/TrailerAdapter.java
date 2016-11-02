package app.com.example.mohammed.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Mohammed on 11/1/2016.
 */

public class TrailerAdapter extends ArrayAdapter {
    private String[] urls;

    public TrailerAdapter(Activity context, String[] urls) {
        super(context, 0, urls);
        this.urls = urls;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String url  = urls[position];
        if (null == convertView) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_item, parent, false);
        }

        TextView author = (TextView)convertView.findViewById(R.id.review_author);
        author.setText(url);

        TextView content = (TextView)convertView.findViewById(R.id.review_content);
        content.setText(url);

        return convertView;
    }
}