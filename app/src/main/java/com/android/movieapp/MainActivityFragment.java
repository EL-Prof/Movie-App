package com.android.movieapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    JSONArray moviesArray ;
    Custom_Adapter myAdapter = null ;
    GridView  gridView ;
    String[]  Fav_array    ;
    Movie_info obj ;
    Gson gson ;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

         gridView = (GridView) root.findViewById(R.id.gridView);
    //   myAdapter = new Custom_Adapter(getActivity() , null) ;
        gridView.setAdapter(myAdapter);
        MovieDetails.prefs = getActivity().getSharedPreferences("MyPrefs1", Context.MODE_PRIVATE);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id) {
            //    MovieDetails.prefs = getActivity().getSharedPreferences("MyPrefs1", Context.MODE_PRIVATE);
                Intent intent = null;
                if (MovieDetails.prefs.getInt("loc", 0) == R.id.favourite_sort) {
                    gson = new Gson();
                    Movie_info obj2 = gson.fromJson(Fav_array[position], Movie_info.class);
                  //  intent = new Intent(getActivity(), MovieDetails.class).putExtra("info", obj2);
                  //  startActivity(intent);
                    ((MainActivity) getActivity()).view_details(obj2);
                                        return;
                }
                Movie_info info = new Movie_info();
                try {
                    info.Title = moviesArray.getJSONObject(position).getString("original_title");
                    info.Poster_URL = moviesArray.getJSONObject(position).getString("poster_path");
                    info.Overview = moviesArray.getJSONObject(position).getString("overview");
                    info.Rating = moviesArray.getJSONObject(position).getString("vote_average");
                    info.Release_Date = moviesArray.getJSONObject(position).getString("release_date");
                    info.ID = moviesArray.getJSONObject(position).getString("id");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //   intent = new Intent(getActivity() , MovieDetails.class).putExtra("info" , info) ;
                //    startActivity(intent);
                ((MainActivity) getActivity()).view_details(info);


            }
        });

        setHasOptionsMenu(true);
    //    new FetchMoviesTask().execute() ;
updateMovies();

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh, menu);
        inflater.inflate(R.menu.spinner, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MovieDetails.prefs = getActivity().getSharedPreferences("MyPrefs1", Context.MODE_PRIVATE);
        if(item.getItemId() == R.id.action_refresh)
        {
            if(MovieDetails.prefs.getInt("loc" , 0) == R.id.favourite_sort)
          get_favourites();
            else
                updateMovies();

        }
        else if(item.getItemId() == R.id.popular_sort) {
            MovieDetails.prefs.edit().putInt("loc" ,R.id.popular_sort ).commit() ;
            updateMovies();
        }
            else if(item.getItemId() == R.id.topRated_sort)
        {
            MovieDetails.prefs.edit().putInt("loc",R.id.topRated_sort ).commit() ;
            updateMovies();
        }
        else if(item.getItemId() == R.id.favourite_sort)
        {
            MovieDetails.prefs.edit().putInt("loc",R.id.favourite_sort ).commit() ;
            get_favourites();
        }

        return true;
    }


    private void get_favourites(){

        MovieDetails.prefs = getActivity().getSharedPreferences("MyPrefs1", Context.MODE_PRIVATE);
        MovieDetails.prefs.edit().putInt("loc" ,R.id.favourite_sort ).commit() ;
        Set<String> Fav_set = MovieDetails.prefs.getStringSet("favourites", null);
        if(Fav_set==null)
            return;
        Fav_array = Fav_set.toArray(new String[Fav_set.size()]);
        String[] strings = new String[Fav_array.length];

        gson = new Gson();
        String base = "http://image.tmdb.org/t/p/w342/";
        for (int i = 0; i < Fav_array.length; i++) {
            obj = gson.fromJson(Fav_array[i], Movie_info.class);
            strings[i] = base.concat(obj.Poster_URL);
        }


        myAdapter = new Custom_Adapter(getActivity(), strings);
        gridView.setAdapter(myAdapter);
    }
    private void updateMovies (){

        FetchMoviesTask MovTask = new FetchMoviesTask();
        MovTask.execute();

    }

    public class FetchMoviesTask extends AsyncTask<Void, Void, String[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            MovieDetails.prefs = getActivity().getSharedPreferences("MyPrefs1", Context.MODE_PRIVATE);

             String Movies_BASE_URL = null ;
if(MovieDetails.prefs.getInt("loc",0)== R.id.popular_sort){

    Movies_BASE_URL = "http://api.themoviedb.org/3/movie/popular?api_key" +
            "=e7f00aeee1fc034148806b87ff292a55";
}
            else
{
    MovieDetails.prefs.edit().putInt("loc" ,R.id.topRated_sort ).commit() ;
    Movies_BASE_URL = "http://api.themoviedb.org/3/movie/top_rated?api_key" +
            "=e7f00aeee1fc034148806b87ff292a55";

}
           try{
            URL url = new URL(Movies_BASE_URL.toString()) ;

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();


            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();


            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = reader.readLine()) != null) {

                buffer.append(line + "\n");

            }

            if (buffer.length() == 0) {

                return null;
            }

            moviesJsonStr = buffer.toString();

        } catch (IOException e) {

            Log.e(LOG_TAG, "Error ", e);

            return null;
        } finally {

            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (reader != null) {

                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }

            }

        }

        try{

            return getMovieDataFromJson(moviesJsonStr) ;
        }

        catch (JSONException e){

            Log.e(LOG_TAG , e.getMessage() , e) ;
            e.printStackTrace();
        }
        return null;    }

        private String[] getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            JSONObject moviesJson = new JSONObject(movieJsonStr);
             moviesArray = moviesJson.getJSONArray("results");
            String [] moviesPosters = new String [moviesArray.length()] ;

            for (int i = 0; i < moviesArray.length(); i++) {
                moviesPosters[i] = moviesArray.getJSONObject(i).getString("poster_path") ;
            }

return moviesPosters ;
    }


        @Override
        protected void onPostExecute(String[] strings) {

            if (strings != null) {
                String base = "http://image.tmdb.org/t/p/w342/";
                for (int i = 0; i < strings.length; i++) {
                    strings[i] = base.concat(strings[i]);
                }
                myAdapter = new Custom_Adapter(getActivity(), strings);
                gridView.setAdapter(myAdapter);
            }
        }
    }



    }
