package app.com.example.mohammed.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

import DB.DBAdapter;
import JavaBeans.Movie;


public class FavoritesFragment extends Fragment {
    private final static String LOG_TAG = FavoritesFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ArrayList<Movie> movies = new ArrayList<>();
    private ImageAdapter myAdapter;
    private GridView gridview;

    DBAdapter dbAdapter;
    TextView msg;

    private int counter = 0;

    // tablet support
    private MovieListener mMovieListener;

    void setMovieListener(MovieListener mMovieListener) {
        this.mMovieListener = mMovieListener;
    }

    public FavoritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoritesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritesFragment newInstance(String param1, String param2) {
        FavoritesFragment fragment = new FavoritesFragment();
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
        Log.d(LOG_TAG, "on create");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        counter++;

        Log.d(LOG_TAG, "on create view, counter : " + counter);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        gridview = (GridView) rootView.findViewById(R.id.fav_grid);
        msg = (TextView) rootView.findViewById(R.id.fav_message);

        dbAdapter = new DBAdapter(getActivity());

        if (dbAdapter.getAllMovies() != null) {
            msg.setVisibility(View.GONE);
            movies = dbAdapter.getAllMovies();

            myAdapter = new ImageAdapter(getActivity(), movies);
            gridview.setAdapter(myAdapter);

            // add listener to each image on grid
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    // get the clicked movie info
                    mMovieListener.setSelectedMovie(movies.get(position));
                }
            });


        } else {
            msg.setVisibility(View.VISIBLE);
            msg.setText("No Favorite Movies Yet, Add Some.");
        }


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (dbAdapter.getAllMovies() != null) {
            msg.setVisibility(View.GONE);
            movies = dbAdapter.getAllMovies();

            myAdapter = new ImageAdapter(getActivity(), movies);
            gridview.setAdapter(myAdapter);


        } else {
            myAdapter = new ImageAdapter(getActivity(), movies = new ArrayList<>());
            gridview.setAdapter(myAdapter);
            msg.setVisibility(View.VISIBLE);
            msg.setText("No Favorite Movies Yet, Add Some.");
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "on stop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(LOG_TAG, "on destroy view");
    }
}
