package app.com.example.mohammed.popularmovies;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
    private String MyPREFERENCES = "mypref";

    ///////////////////// page navigation //////////////////
    // page number
    public int pageNumber;
    public int totalPages;
    public ImageView leftArrow, rightArrow;
    public TextView pageNumberTextView;
    public ProgressBar loadingProgressBar;
    TextView sort_type_view;
    //////////////////// search ////////////////////////
    public String search_query = null;

    // fav movies
    DBAdapter dbAdapter;

    private View mRootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG + "  findme", "onCreate()");
        pageNumber = 1;
        // create options menu
        setHasOptionsMenu(true);

        myRequestQueue = VolleySingleton.getInstance(getActivity().getApplicationContext()).getRequestQueue();
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        sort_type_view = (TextView) getActivity().findViewById(R.id.tv_main_sort_type);
        leftArrow = (ImageView) getActivity().findViewById(R.id.left_arrow);
        rightArrow = (ImageView) getActivity().findViewById(R.id.right_arrow);
        pageNumberTextView = (TextView) getActivity().findViewById(R.id.page_number);
        loadingProgressBar = (ProgressBar) getActivity().findViewById(R.id.progressBar);

        if (sort_type_view != null) {
            if (readPage() == "popular") {
                sort_type_view.setText(R.string.popular_movies);
            } else if (readPage() == "top_rated") {
                sort_type_view.setText(R.string.top_rated_movies);
            } else if (readPage() == "now_playing") {
                sort_type_view.setText(R.string.now_playing_movies);
            } else if (readPage() == "upcoming") {
                sort_type_view.setText(R.string.upcoming_movies);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(LOG_TAG + "  findme", "onDestroy()");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        TextView sort_type_view = (TextView) getActivity().findViewById(R.id.tv_main_sort_type);
        if (id == R.id.popular) {
            pageNumber = 1;
            loadingProgressBar.setVisibility(View.VISIBLE);
            updateMovies("popular", pageNumber, null);
            sort_type_view.setText(R.string.popular_movies);
            writePage("popular");
            if (pageNumberTextView != null) pageNumberTextView.setText("" + pageNumber);
            search_query = null;
        } else if (id == R.id.top_rated) {
            pageNumber = 1;
            loadingProgressBar.setVisibility(View.VISIBLE);
            updateMovies("top_rated", pageNumber, null);
            sort_type_view.setText(R.string.top_rated_movies);
            writePage("top_rated");
            if (pageNumberTextView != null) pageNumberTextView.setText("" + pageNumber);
            search_query = null;
        } else if (id == R.id.now_playing) {
            pageNumber = 1;
            loadingProgressBar.setVisibility(View.VISIBLE);
            updateMovies("now_playing", pageNumber, null);
            sort_type_view.setText(R.string.now_playing_movies);
            writePage("now_playing");
            if (pageNumberTextView != null) pageNumberTextView.setText("" + pageNumber);
            search_query = null;
        } else if (id == R.id.upcomming) {
            pageNumber = 1;
            loadingProgressBar.setVisibility(View.VISIBLE);
            updateMovies("upcoming", pageNumber, null);
            sort_type_view.setText(R.string.upcoming_movies);
            writePage("upcoming");
            if (pageNumberTextView != null) pageNumberTextView.setText("" + pageNumber);
            search_query = null;
        } else if (id == R.id.favorite) {
            Intent intent = new Intent(getActivity(), FavoritesActivity.class);
            startActivity(intent);
        } else if (id == R.id.search_icon) {
            showSearchDialog();
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_movies, container, false);
        gridview = (GridView) mRootView.findViewById(R.id.gridview);
        dbAdapter = new DBAdapter(getActivity());


        if (readPage() == "popular") {
            updateMovies("popular", pageNumber, null);
            if (pageNumberTextView != null) pageNumberTextView.setText("" + pageNumber);
        } else if (readPage() == "top_rated") {
            updateMovies("top_rated", pageNumber, null);
            if (pageNumberTextView != null) pageNumberTextView.setText("" + pageNumber);
        } else if (readPage() == "now_playing") {
            updateMovies("now_playing", pageNumber, null);
            if (pageNumberTextView != null) pageNumberTextView.setText("" + pageNumber);
        } else if (readPage() == "upcoming") {
            updateMovies("upcoming", pageNumber, null);
            if (pageNumberTextView != null) pageNumberTextView.setText("" + pageNumber);
        }
        // to be changed
        else {
            updateMovies("popular", pageNumber, null);
            if (pageNumberTextView != null) pageNumberTextView.setText("" + pageNumber);
        }


        // add listener to each image on grid
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                mMovieListener.setSelectedMovie(result.get(position));
            }
        });


        // page navigation
        if (leftArrow != null && rightArrow != null) {
            leftArrow.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (pageNumber > 1) {
                        pageNumber--;
                        loadingProgressBar.setVisibility(View.VISIBLE);
                        updateMovies((search_query == null) ? readPage() : null, pageNumber, (search_query == null) ? null : search_query);
                        if (pageNumberTextView != null) pageNumberTextView.setText("" + pageNumber);
                    }
                }
            });
            rightArrow.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (pageNumber < totalPages) {
                        pageNumber++;
                        loadingProgressBar.setVisibility(View.VISIBLE);
                        updateMovies((search_query == null) ? readPage() : null, pageNumber, (search_query == null) ? null : search_query);
                        if (pageNumberTextView != null) pageNumberTextView.setText("" + pageNumber);
                    }
                }
            });
        }

        return mRootView;
    }

    private void updateMovies(String reuest_type, int page, String query) {
        Context myContext = getActivity().getApplicationContext();

        String URL = "http://api.themoviedb.org/3/movie/" + reuest_type + "?api_key="
                + VolleySingleton.getInstance(myContext).getApi_key() + "&language=en-US&page=" + page;
        // for search
        if (query != null) {
            URL = "http://api.themoviedb.org/3/search/movie?api_key="
                    + VolleySingleton.getInstance(myContext).getApi_key() + "&language=en-US&query=" + query + "&page=" + page;
        }
        Log.d(LOG_TAG, " request : " + URL);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d(LOG_TAG, " response : " + response);
                        try {
                            JSONObject resultJson = new JSONObject(response);

                            // get total pages
                            totalPages = resultJson.getInt("total_pages");

                            JSONArray moviesArrayJson = resultJson.getJSONArray("results");
                            //result = new ArrayList<>();
                            result.clear();

                            for (int i = 0; i < moviesArrayJson.length(); i++) {
                                // movie JSON
                                JSONObject J = moviesArrayJson.getJSONObject(i);

                                Movie m = new Movie(
                                        J.getString("id"),
                                        J.getString("original_title"),
                                        "http://image.tmdb.org/t/p/w185/" + J.getString("poster_path"),
                                        "http://image.tmdb.org/t/p/w342/" + J.getString("backdrop_path"),
                                        J.getString("overview"),
                                        J.getString("vote_average"),
                                        J.getString("release_date"),
                                        false
                                );
                                // Genres
                                JSONArray genresArray = J.getJSONArray("genre_ids");
                                m.setGenres(convertJSONArray(genresArray));
                                result.add(m);
                            }
                            if (getActivity() != null) {
                                myAdapter = new ImageAdapter(getActivity(), result);
                                gridview.setAdapter(myAdapter);
                            }
                            if(loadingProgressBar != null )loadingProgressBar.setVisibility(View.INVISIBLE);

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
                spinner = (ProgressBar) mRootView.findViewById(R.id.progressBar);
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

    public void writePage(String page) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("page", page);
        editor.commit();
    }

    public String readPage() {
        return sharedpreferences.getString("page", "popular");
    }


    // convert
    public ArrayList<String> convertJSONArray(JSONArray data) throws JSONException {
        ArrayList<String> result = new ArrayList<>();
        String J;
        for (int i = 0; i < data.length(); i++) {
            J = data.getString(i);
            result.add(J);
        }
        return result;
    }

    // search dialog
    public void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Search");
        // Set up the input
        final EditText input = new EditText(getActivity());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);




        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pageNumber = 1;
                search_query = input.getText().toString();
                updateMovies(null, 1, search_query);
                sort_type_view.setText(search_query);
                if (pageNumberTextView != null) pageNumberTextView.setText("" + pageNumber);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //builder.show();
        dialog.show();
    }

    public void showMessage(String message){

    }


}