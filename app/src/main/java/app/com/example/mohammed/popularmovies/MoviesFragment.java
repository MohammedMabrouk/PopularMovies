package app.com.example.mohammed.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
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

public class MoviesFragment extends Fragment {

    private final String LOG_TAG = MoviesFragment.class.getSimpleName();
    public RequestQueue myRequestQueue;

    private ImageAdapter myAdapter;
    // all movies result
    public ArrayList<Movie> result = new ArrayList<>();
    // gridView of this fragment
    private GridView gridview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create options menu
        setHasOptionsMenu(true);
        myRequestQueue = VolleySingleton.getInstance(getActivity().getApplicationContext()).getRequestQueue();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        TextView sort_type_view = (TextView) getActivity().findViewById(R.id.tv_main_sort_type);
        if(id == R.id.popular){
            updateMovies("popular");
            sort_type_view.setText("Popular");
        }else{
            updateMovies("top_rated");
            sort_type_view.setText("Top Rated");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, " onCreateView()");
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_placeholder, container, false);
        gridview = (GridView) rootView.findViewById(R.id.gridview);

        updateMovies("popular");

        // add listener to each image on grid
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // get the clicked movie info
                String movie_id = result.get(position).getId();
                String movie_name = result.get(position).getTitle();
                String poster = result.get(position).getPoster();
                String date = result.get(position).getReleaseDate();
                String overview = result.get(position).getOverview();
                String rate = result.get(position).getVoteAverage();

                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("id", movie_id);
                intent.putExtra("movie_name", movie_name);
                intent.putExtra("poster", poster);
                intent.putExtra("date", date);
                intent.putExtra("overview", overview);
                intent.putExtra("rate", rate);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void updateMovies(String reuest_type){
        Context myContext = getActivity().getApplicationContext();
        String URL = "http://api.themoviedb.org/3/movie/" + reuest_type + "?api_key="
                + VolleySingleton.getInstance(myContext).getApi_key();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG , " response : " + response);
                        try {
                            JSONObject resultJson = new JSONObject(response);
                            JSONArray moviesArrayJson = resultJson.getJSONArray("results");
                            //result = new ArrayList<>();
                            result.clear();

                            for(int i = 0 ; i < moviesArrayJson.length() ; i++){
                                JSONObject J = moviesArrayJson.getJSONObject(i);
                                result.add(new Movie(
                                        J.getString("id"),
                                        J.getString("original_title"),
                                        "http://image.tmdb.org/t/p/w185/" + J.getString("poster_path"),
                                        J.getString("overview"),
                                        J.getString("vote_average"),
                                        J.getString("release_date")
                                ));
                            }
                            myAdapter = new ImageAdapter(getActivity(), result);
                            gridview.setAdapter(myAdapter);

                        } catch (JSONException e) {
                            Log.d(LOG_TAG, "error : " + e);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, "ERROR :" + error);
                TextView sort_type_view = (TextView) getActivity().findViewById(R.id.tv_main_sort_type);
                sort_type_view.setText("NO INTERNET");
            }
        });
        VolleySingleton.getInstance(myContext).addToRequestQueue(stringRequest);

    }


}