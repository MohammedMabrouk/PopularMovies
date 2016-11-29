package app.com.example.mohammed.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import DB.DBAdapter;
import JavaBeans.Movie;
import JavaBeans.Review;
import JavaBeans.Trailer;
import app.com.example.mohammed.popularmovies.databinding.FragmentDetailsBinding;


public class DetailsFragment extends Fragment {

    private static final String LOG_TAG = DetailsFragment.class.getCanonicalName();

    private LinearLayout trailersLayout;
    private ArrayList<Trailer> trailers = new ArrayList<>();

    private LinearLayout reviewsLayout;
    private ArrayList<Review> reviews = new ArrayList<>();

    private DBAdapter dbAdapter;

    private View mRootView;

    private Bundle bundle;
    private Intent intent;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public DetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG + "  findme", "onCreate()");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // initialize
        bundle = this.getArguments();
        intent = getActivity().getIntent();
        dbAdapter = new DBAdapter(getActivity());
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentDetailsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false);
        View rootView = binding.getRoot();

        // initialize
        mRootView = rootView;
        trailersLayout = (LinearLayout) rootView.findViewById(R.id.linear_layout_trailers);
        reviewsLayout = (LinearLayout) rootView.findViewById(R.id.linear_layout_reviews);

        // view details
        loadDetails(bundle, intent, binding, mRootView);

        return rootView;
    }

    public void loadDetails(Bundle myBundle, Intent myIntent, FragmentDetailsBinding binding, View rootView){
        Movie movie = new Movie();
        Boolean type = false; // true : main, false : fav

        // portrait
        if(myIntent.getSerializableExtra("from_main_movie1") != null){
            Log.d(LOG_TAG, "type1");
            movie = (Movie)myIntent.getSerializableExtra("from_main_movie1");
            type = true;
        }
        else if(myIntent.getSerializableExtra("from_fav_movie1") != null){
            Log.d(LOG_TAG, "type2");
            movie = (Movie)myIntent.getSerializableExtra("from_fav_movie1");
            type = false;
        }
        // landscape or tablet
        else if(myBundle != null && bundle.getSerializable("from_main_movie2") != null){
            Log.d(LOG_TAG, "type3");
            movie = (Movie)bundle.getSerializable("from_main_movie2");
            type = true;
        }
        else if(myBundle != null && bundle.getSerializable("from_fav_movie2") != null){
            Log.d(LOG_TAG, "type4");
            movie = (Movie)bundle.getSerializable("from_fav_movie2");
            type = false;
        }


        final String id = movie.getId();
        String poster = movie.getPoster();
        String title = movie.getTitle();
        getActivity().setTitle(title);

        boolean fav = false;
        if (dbAdapter.getMovie(id) != null) {
            fav = true;
        }
        movie.setFavMovie(fav);
        // load details
        movie.setVoteAverage(movie.getVoteAverage());
        binding.setMovie(movie);
        // load poster
        ImageView view = (ImageView) rootView.findViewById(R.id.poster);
        Picasso.with(getActivity()).load(poster).error(R.drawable.default_img).into(view);
        // load fav button
        loadFavButton(rootView, movie);

        if(type){
            // get trailers
            getTrailers(id);
            // get reviews
            getReviews(id);
        }else{
            // get trailers
            getTrailersDB(movie.getId());
            // get reviews
            getReviewsDB(movie.getId());
        }
    }

    public void loadFavButton(View rootView, final Movie movie){
        final Button b= (Button) rootView.findViewById(R.id.fav_button);

        if(movie.isFavMovie()){
            b.setText(R.string.remove_fav);
            b.setCompoundDrawablesWithIntrinsicBounds( R.drawable.heart_after_16, 0, 0, 0);
        }

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(dbAdapter.getMovie(movie.getId()) == null){

                    Log.d(LOG_TAG, movie.getId() + " not found, added");
                    if(dbAdapter.addMovie(movie, trailers, reviews) > 0) {
                        // layout
                        b.setText(R.string.remove_fav);
                        b.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_after_16, 0, 0, 0);
                        Toast.makeText(getContext(), "Added To Favorites", Toast.LENGTH_SHORT).show();
                    }
                }else{

                    if(dbAdapter.deleteMovie(movie.getId()) > 0) {
                        b.setText(R.string.add_fav);
                        b.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_before_16, 0, 0, 0);
                        Toast.makeText(getContext(), "Removed From Favorites", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }


    public void getReviews(String movieID) {
        Context myContext = getActivity().getApplicationContext();

        String URL = "http://api.themoviedb.org/3/movie/" + movieID + "/reviews?api_key="
                + VolleySingleton.getInstance(myContext).getApi_key();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject resultJson = new JSONObject(response);
                            JSONArray reviewsArrayJson = resultJson.getJSONArray("results");
                            if (reviewsArrayJson.length() > 0) {
                                for (int i = 0; i < reviewsArrayJson.length(); i++) {
                                    JSONObject J = reviewsArrayJson.getJSONObject(i);
                                    reviews.add(new Review(
                                            J.getString("id"),
                                            J.getString("author"),
                                            J.getString("content")
                                    ));

                                }

                            }

                            // handle view
                            ProgressBar spinner1;
                            spinner1 = (ProgressBar)mRootView.findViewById(R.id.progressBar2);
                            spinner1.setVisibility(View.GONE);

                            TextView tv = (TextView)mRootView.findViewById(R.id.pbar2_textview);
                            tv.setVisibility(View.GONE);

                            addLayout("reviews");
                        } catch (JSONException e) {
                            Log.d(LOG_TAG, "error : " + e);
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, "ERROR :" + error);
                // handle view
                ProgressBar spinner2;
                spinner2 = (ProgressBar)mRootView.findViewById(R.id.progressBar2);
                spinner2.setVisibility(View.GONE);

                TextView tv = (TextView)mRootView.findViewById(R.id.pbar2_textview);
                tv.setText("No Internet Connection");
            }
        });
        VolleySingleton.getInstance(myContext).addToRequestQueue(stringRequest);

    }

    public void getReviewsDB(String movie_id){
        // handle view
        ProgressBar spinner2;
        spinner2 = (ProgressBar)mRootView.findViewById(R.id.progressBar2);
        spinner2.setVisibility(View.GONE);

        TextView tv = (TextView)mRootView.findViewById(R.id.pbar2_textview);
        tv.setVisibility(View.GONE);

        reviews = dbAdapter.getReviews(movie_id);
        addLayout("reviews");
    }
    public void getTrailersDB(String movie_id){
        // handle view
        ProgressBar spinner1;
        spinner1 = (ProgressBar)mRootView.findViewById(R.id.progressBar1);
        spinner1.setVisibility(View.GONE);

        TextView tv = (TextView)mRootView.findViewById(R.id.pbar1_textview);
        tv.setVisibility(View.GONE);

        trailers = dbAdapter.getTrailers(movie_id);
        addLayout("trailers");
    }

    public void getTrailers(String movieID) {
        Context myContext = getActivity().getApplicationContext();

        String URL = "http://api.themoviedb.org/3/movie/" + movieID + "/videos?api_key="
                + VolleySingleton.getInstance(myContext).getApi_key();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject resultJson = new JSONObject(response);
                            JSONArray reviewsArrayJson = resultJson.getJSONArray("results");
                            if (reviewsArrayJson.length() > 0) {
                                for (int i = 0; i < reviewsArrayJson.length(); i++) {
                                    JSONObject J = reviewsArrayJson.getJSONObject(i);
                                    trailers.add(new Trailer(
                                            J.getString("name"),
                                            J.getString("site"),
                                            J.getString("size"),
                                            "https://www.youtube.com/watch?v=" + J.getString("key")
                                    ));
                                }

                            }
                            // handle view
                            ProgressBar spinner1;
                            spinner1 = (ProgressBar)mRootView.findViewById(R.id.progressBar1);
                            spinner1.setVisibility(View.GONE);

                            TextView tv = (TextView)mRootView.findViewById(R.id.pbar1_textview);
                            tv.setVisibility(View.GONE);

                            addLayout("trailers");
                        } catch (JSONException e) {
                            Log.d(LOG_TAG, "error : " + e);
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, "ERROR :" + error);
                // handle view
                ProgressBar spinner1;
                spinner1 = (ProgressBar)mRootView.findViewById(R.id.progressBar1);
                spinner1.setVisibility(View.GONE);

                TextView tv = (TextView)mRootView.findViewById(R.id.pbar1_textview);
                tv.setText("No Internet Connection");
            }
        });
        VolleySingleton.getInstance(myContext).addToRequestQueue(stringRequest);

    }

    private void addLayout(String type) {

        if(reviewsLayout != null && trailersLayout != null){
            switch (type) {
                case "reviews":
                    if (reviews.size() > 0 && reviews != null) {

                        for (Review r : reviews) {
                            try{
                                View reviewItemView = LayoutInflater.from(getActivity()).inflate(R.layout.review_item, reviewsLayout, false);
                                TextView author = (TextView) reviewItemView.findViewById(R.id.review_author);
                                author.setText(r.getAuthor());

                                TextView content = (TextView) reviewItemView.findViewById(R.id.review_content);
                                content.setText(r.getContent());

                                reviewsLayout.addView(reviewItemView);
                            }catch(Exception e){
                                Log.d(LOG_TAG, " reviews error : " + e);
                            }

                        }

                    } else {
                        try{
                            View reviewItemView = LayoutInflater.from(getActivity()).inflate(R.layout.review_item, reviewsLayout, false);
                            TextView author = (TextView) reviewItemView.findViewById(R.id.review_author);
                            author.setText("No Reviews.");
                            reviewsLayout.addView(reviewItemView);
                        }catch(Exception e){
                            Log.d(LOG_TAG, "no reviews, error : " + e);
                        }
                    }
                    break;

                case "trailers":
                    if (trailers.size() > 0 && trailers != null) {

                        for (Trailer r : trailers) {
                            try{
                                View trailerItemView = LayoutInflater.from(getActivity()).inflate(R.layout.trailer_item, trailersLayout, false);
                                TextView name = (TextView) trailerItemView.findViewById(R.id.trailer_name);
                                name.setText(r.getName());

                                TextView details = (TextView) trailerItemView.findViewById(R.id.details);
                                details.setText(r.getSite() + " - " + r.getSize());

                                final String url = r.getUrl();
                                ImageView img = (ImageView) trailerItemView.findViewById(R.id.player_icon);
                                trailerItemView.setOnClickListener(new OnClickListener() {
                                    public void onClick(View view) {
                                        Log.d(LOG_TAG, "clicked" + url);
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                        startActivity(intent);
                                    }
                                });

                                trailersLayout.addView(trailerItemView);
                            }catch (Exception e){
                                Log.d(LOG_TAG , " trailers eror : " + e);
                            }
                        }

                    } else {
                        try{
                            View trailerItemView = LayoutInflater.from(getActivity()).inflate(R.layout.review_item, trailersLayout, false);
                            TextView msg = (TextView) trailerItemView.findViewById(R.id.review_author);
                            msg.setText("No Trailers.");

                            trailersLayout.addView(trailerItemView);
                        }catch(Exception e){
                            Log.d(LOG_TAG, "no trilers, error : " + e);
                        }

                    }
                    break;
            }
        }



    }


}
