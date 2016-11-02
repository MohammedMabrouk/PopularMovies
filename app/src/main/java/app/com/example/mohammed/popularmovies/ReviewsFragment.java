package app.com.example.mohammed.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ReviewsFragment extends Fragment {

    private static final String LOG_TAG = ReviewsFragment.class.getCanonicalName();
    private ListView listView;
    private ArrayList<Review> reviews = new ArrayList<>();
    private ReviewAdapter myAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ReviewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReviewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReviewsFragment newInstance(String param1, String param2) {
        ReviewsFragment fragment = new ReviewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_reviews, container, false);
        listView = (ListView)rootView.findViewById(R.id.review_list);

        Intent intent = getActivity().getIntent();
        String id = intent.getStringExtra("id");

        getReviews(id);
        return rootView;
    }

    public void getReviews(String movieID){
        Context myContext = getActivity().getApplicationContext();

        String URL = "http://api.themoviedb.org/3/movie/" + movieID + "/reviews?api_key="
                + VolleySingleton.getInstance(myContext).getApi_key();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG , " response : " + response);
                        try {
                            JSONObject resultJson = new JSONObject(response);
                            JSONArray reviewsArrayJson = resultJson.getJSONArray("results");
                            reviews.clear();
                            if(reviewsArrayJson.length() > 0){
                                for(int i = 0 ; i < reviewsArrayJson.length() ; i++){
                                    JSONObject J = reviewsArrayJson.getJSONObject(i);
                                    reviews.add(new Review(
                                            J.getString("id"),
                                            J.getString("author"),
                                            J.getString("content")
                                    ));
                                    Log.d(LOG_TAG , "author : " + J.getString("author"));
                                }

                                myAdapter = new ReviewAdapter(getActivity(), reviews);
                                listView.setAdapter(myAdapter);
                            }else{
                                Log.d(LOG_TAG, "No reviews");
                            }

                            // handle view

                        } catch (JSONException e) {
                            Log.d(LOG_TAG, "error : " + e);
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, "ERROR :" + error);
            }
        });
        VolleySingleton.getInstance(myContext).addToRequestQueue(stringRequest);

    }


}
