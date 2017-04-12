package com.android.movieapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment {

    public MovieDetailsFragment() {
    }

    Movie_info info ;
    String key ;
    Set<String> Fav_set;
    Gson gson = new Gson();
    View rootView ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         rootView = inflater.inflate(R.layout.fragment_movie_details, container, false) ;

        Intent intent = getActivity().getIntent() ;
if(intent != null )
{
if(intent.hasExtra("info")) //Single pane Mode
{
     info =
            (Movie_info) intent.getSerializableExtra("info");
}
    else    //2Pane Mode
{
     info =
            (Movie_info) getArguments().getSerializable("info");

}

    ((TextView) rootView.findViewById(R.id.TiltextView)).setText(info.Title);
    ((TextView) rootView.findViewById(R.id.date_value)).setText(info.Release_Date);
if (!(MovieDetails.prefs.getInt("loc",0)==R.id.favourite_sort))
    info.Poster_URL = "http://image.tmdb.org/t/p/w342/".concat(info.Poster_URL) ;
    Picasso.with(getActivity()).load(info.Poster_URL).into((ImageView)rootView.findViewById(R.id.imageView));

   ((TextView) rootView.findViewById(R.id.DescView)).setText(info.Overview);
    ((TextView) rootView.findViewById(R.id.RateValueView)).setText(info.Rating);

}

        Fav_set = MovieDetails.prefs.getStringSet("favourites", null) ;
        String json = gson.toJson(info);

        if(Fav_set != null &&Fav_set.contains(gson.toJson(info)))
            ((ImageButton) rootView.findViewById(R.id.imageButton)).setImageResource(R.drawable.remove_from_favourites);



        ImageView iv = (ImageView) rootView.findViewById(R.id.TrailerView);
        ImageView iv2 = (ImageView) rootView.findViewById(R.id.imageView2);
        ImageButton ib = (ImageButton) rootView.findViewById(R.id.imageButton);

        iv.setOnClickListener(new View.OnClickListener() {
    public void onClick(View v) {

     trailer_handler();

    }
});


        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                review_handler();
            }
        });


        ib.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                favourite_handler();

            }
        });


        return rootView;

    }





    public void trailer_handler()
    {


        String URL = "http://api.themoviedb.org/3/movie/"+info.ID + "/videos?api_key=e7f00aeee1fc034148806b87ff292a55" ;

        JsonObjectRequest trailer_response = new JsonObjectRequest(Request.Method.GET, URL, (JSONObject) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray results_array = response.getJSONArray("results");
                    key = results_array.getJSONObject(0).getString("key");

                    String s = "http://www.youtube.com/watch?v=" + key;
                    //   Toast.makeText(getApplicationContext(), key, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(s)));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        AppController.getInstance().addToRequestQueue(trailer_response);

    }


    public void review_handler()
    {

        String URL = "http://api.themoviedb.org/3/movie/"+info.ID + "/reviews?api_key=e7f00aeee1fc034148806b87ff292a55" ;

        JsonObjectRequest trailer_response = new JsonObjectRequest(Request.Method.GET, URL, (JSONObject) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray results_array = response.getJSONArray("results");

                    if(results_array.length() == 0){
                        Toast.makeText(getContext(), "Sorry, no available reviews!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String  rev_URL = results_array.getJSONObject(0).getString("url");


                    //   Toast.makeText(getApplicationContext(), key, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(rev_URL)));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        AppController.getInstance().addToRequestQueue(trailer_response);

        // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=Hxy8BZGQ5Jo")));

    }


    public void  favourite_handler(){

       MovieDetails.prefs = getActivity().getSharedPreferences("MyPrefs1", Context.MODE_PRIVATE);
        Fav_set = MovieDetails.prefs.getStringSet("favourites", null) ;
        SharedPreferences.Editor edit = MovieDetails.prefs.edit();

        if(Fav_set!= null &&Fav_set.contains(gson.toJson(info))) {
            Toast.makeText(getContext(), "Removed from Favourites!", Toast.LENGTH_SHORT).show();

            Fav_set.remove(gson.toJson(info)) ;
            edit.remove("favourites") ;
            edit.commit();

            edit.putStringSet("favourites", Fav_set) ;
            edit.commit();
            ((ImageButton) rootView.findViewById(R.id.imageButton)).setImageResource(R.drawable.favorite_heart);
            return;
        }
        Toast.makeText(getContext(), "Added to Favourites!", Toast.LENGTH_SHORT).show();

        String json = gson.toJson(info);
        //  if(!Fav_set.contains(json))

        if(MovieDetails.prefs.getStringSet("favourites", null)!=null)
            Fav_set = MovieDetails.prefs.getStringSet("favourites", null) ;
        else
            Fav_set = new HashSet<String>() ;

        edit.remove("favourites") ;
        edit.commit();


        Fav_set.add(json);
        int i = Fav_set.size();
        edit.putStringSet("favourites", Fav_set) ;
        edit.commit();
        ((ImageButton) rootView.findViewById(R.id.imageButton)).setImageResource(R.drawable.remove_from_favourites);
    }







}
