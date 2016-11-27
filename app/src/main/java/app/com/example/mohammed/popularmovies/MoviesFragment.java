package app.com.example.mohammed.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import DB.DBAdapter;
import JavaBeans.Movie;

public class MoviesFragment extends Fragment {
    // tablet support
    private MovieListener mMovieListener;

    void setMovieListener(MovieListener mMovieListener) {
        this.mMovieListener = mMovieListener;
    }


    private final String LOG_TAG = MoviesFragment.class.getSimpleName();
    private RequestQueue myRequestQueue;
    private ImageAdapter myAdapter;
    // all movies result
    public ArrayList<Movie> result = new ArrayList<>();
    // gridView of this fragment
    private GridView gridview;
    // favorite movies data
    private SharedPreferences sharedpreferences;
    // screen : pop or top_rated
    private int page;
    // fav movies
    DBAdapter dbAdapter;

    private View mRootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG + "  findme", "onCreate()");
        // create options menu
        setHasOptionsMenu(true);
        myRequestQueue = VolleySingleton.getInstance(getActivity().getApplicationContext()).getRequestQueue();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        TextView sort_type_view = (TextView) getActivity().findViewById(R.id.tv_main_sort_type);
        if (id == R.id.popular) {
            updateMovies("popular");
            sort_type_view.setText("Popular");
            page = 1;
        } else if (id == R.id.top_rated) {
            updateMovies("top_rated");
            sort_type_view.setText("Top Rated");
            page = 2;
        } else if (id == R.id.favorite) {
            Intent intent = new Intent(getActivity(), FavoritesActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (page == 1) {
            Log.d(LOG_TAG, "page : 1");
            updateMovies("popular");
        } else {
            Log.d(LOG_TAG, "page : 2");
            updateMovies("top_rated");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_movies, container, false);
        gridview = (GridView) mRootView.findViewById(R.id.gridview);
        dbAdapter = new DBAdapter(getActivity());

        updateMovies("popular");
        page = 1;

        // add listener to each image on grid
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                mMovieListener.setSelectedMovie(result.get(position));
            }
        });

        return mRootView;
    }

    private void updateMovies(String reuest_type) {
        Context myContext = getActivity().getApplicationContext();
        String URL = "http://api.themoviedb.org/3/movie/" + reuest_type + "?api_key="
                + VolleySingleton.getInstance(myContext).getApi_key();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG, " response : " + response);
                        try {
                            JSONObject resultJson = new JSONObject(response);
                            JSONArray moviesArrayJson = resultJson.getJSONArray("results");
                            //result = new ArrayList<>();
                            result.clear();

                            for (int i = 0; i < moviesArrayJson.length(); i++) {

                                JSONObject J = moviesArrayJson.getJSONObject(i);
                                result.add(new Movie(
                                        J.getString("id"),
                                        J.getString("original_title"),
                                        "http://image.tmdb.org/t/p/w185/" + J.getString("poster_path"),
                                        J.getString("overview"),
                                        J.getString("vote_average"),
                                        J.getString("release_date"),
                                        false
                                ));
                            }
                            if (getActivity() != null) {
                                myAdapter = new ImageAdapter(getActivity(), result);
                                gridview.setAdapter(myAdapter);
                            }

                            ProgressBar spinner;
                            spinner = (ProgressBar)mRootView.findViewById(R.id.progressBar);
                            spinner.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            Log.d(LOG_TAG, "error : " + e);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, "ERROR :" + error);
                ProgressBar spinner;
                spinner = (ProgressBar)mRootView.findViewById(R.id.progressBar);
                spinner.setVisibility(View.GONE);
                addNetworkErrorMsg();
            }
        });
        VolleySingleton.getInstance(myContext).addToRequestQueue(stringRequest);

    }

    public void addNetworkErrorMsg() {
        try {
            TextView sort_type_view = (TextView) getActivity().findViewById(R.id.tv_main_sort_type);
            sort_type_view.setText("no internet connection.");
            sort_type_view.setGravity(Gravity.CENTER_VERTICAL);
        } catch (Exception e) {
            Log.d(LOG_TAG, "" + e);
        }
    }


}