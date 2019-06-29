package com.example.flicks;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flicks.models.Movie;
import com.example.flicks.models.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    //constants

    //base URL
    public static final String API_BASE_URL = "http://api.themoviedb.org/3";
    //API key
    public static final String API_KEY_PARAM = "api_key";
    //API key param
    //tags for logging in this class
    public static final String TAG = "MainActivity";

    //instance fields
    //instance of AsincHttpClient
    AsyncHttpClient client;

    //Arraylist for currently playing movies
    ArrayList<Movie> movies;

    //the recycler view
    RecyclerView rvMovies;

    //the  adapter wired to the recycler view
    MovieAdapter adapter;

    Config config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializers

        //client initializer
        client = new AsyncHttpClient();
        //initialize the Arraylist
        movies = new ArrayList<>();

        //initiale the adapter
        adapter = new MovieAdapter(movies);

        //resolve the recycler view and connect a laout manager and the adapter
        rvMovies = findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);

        getConfiguration();

        //get now playing movies

    }

    //get the list of currently playing movies
    private void getNowPlaying(){
        //create the url
        String url = API_BASE_URL + "/movie/now_playing";

        //setting the request params
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    //iterate through array
                    for (int i = 0; i <results.length();i++){
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);
                        //notify adapter that a row was added
                        adapter.notifyItemInserted(movies.size()-1);
                    }
                    Log.i(TAG, String.format("Loaded %s movies",results.length()));
                } catch (JSONException e) {
                    logError("Failed to parse now playing movies",e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get data from the now playing endpoint", throwable, true);
            }
        });
    }

    private void getConfiguration(){
        //create the url
        String url = API_BASE_URL + "/configuration";

        //setting the request params
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));

        //getting a JSON object from the API
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response); why is this removed
                try {
                    config = new Config(response);
                    Log.i(TAG, String.format("Loaded configurations with the imageBaseUrl %s and posterSize %s", config.getImageBaseUrl(), config.getPosterSize()));
                    adapter.setConfig(config);
                    getNowPlaying();
                } catch (JSONException e) {
                    //do this to avoid printing stackTrace
                    logError("Failed parsing configurations", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed getting configuration", throwable, true);
            }
        });

    }

    //helper that displays errors to user
    private void logError(String message, Throwable error, boolean alertUser){
        Log.e(TAG, message, error);

        //avoiding silent errors
        if (alertUser){
            //long toast with error
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    //setup function for listener, this enables
}
