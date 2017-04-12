package com.android.movieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MovieDetails extends AppCompatActivity {
    String key ;
    ArrayList<String> array_list = new ArrayList<String>();
    static SharedPreferences prefs ;
     Movie_info info ;
    Set<String> Fav_set;
    String[] Fav_array ;
    Gson gson = new Gson();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);

/*
       prefs = getSharedPreferences("MyPrefs1", Context.MODE_PRIVATE);
   info =
                (Movie_info) this.getIntent().getSerializableExtra("info");
        Fav_set = prefs.getStringSet("favourites", null) ;
        String json = gson.toJson(info);

        if(Fav_set != null &&Fav_set.contains(gson.toJson(info)))
            ((ImageButton) findViewById(R.id.imageButton)).setImageResource(R.drawable.remove_from_favourites);
*/

    }



    public void trailer_handler(View view)
    {

         info =
                (Movie_info) this.getIntent().getSerializableExtra("info");

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

    public void review_handler(View view)
    {

        Movie_info info =
                (Movie_info) this.getIntent().getSerializableExtra("info");

        String URL = "http://api.themoviedb.org/3/movie/"+info.ID + "/reviews?api_key=e7f00aeee1fc034148806b87ff292a55" ;

        JsonObjectRequest trailer_response = new JsonObjectRequest(Request.Method.GET, URL, (JSONObject) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray results_array = response.getJSONArray("results");

                    if(results_array.length() == 0){
                        Toast.makeText(MovieDetails.this, "Sorry, no available reviews!", Toast.LENGTH_SHORT).show();
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


    public void  favourite_handler(View view){

        info =
                (Movie_info) this.getIntent().getSerializableExtra("info");
        Fav_set = prefs.getStringSet("favourites", null) ;
        SharedPreferences.Editor edit = prefs.edit();

        if(Fav_set!= null &&Fav_set.contains(gson.toJson(info))) {
            Toast.makeText(MovieDetails.this, "Removed from Favourites!", Toast.LENGTH_SHORT).show();

Fav_set.remove(gson.toJson(info)) ;
            edit.remove("favourites") ;
            edit.commit();

            edit.putStringSet("favourites", Fav_set) ;
            edit.commit();
            ((ImageButton) findViewById(R.id.imageButton)).setImageResource(R.drawable.favorite_heart);
            return;
        }
        Toast.makeText(MovieDetails.this, "Added to Favourites!", Toast.LENGTH_SHORT).show();

        String json = gson.toJson(info);
      //  if(!Fav_set.contains(json))

     if(prefs.getStringSet("favourites", null)!=null)
       Fav_set = prefs.getStringSet("favourites", null) ;
else
     Fav_set = new HashSet<String>() ;

        edit.remove("favourites") ;
        edit.commit();


          Fav_set.add(json);
int i = Fav_set.size();
               edit.putStringSet("favourites", Fav_set) ;
               edit.commit();
        ((ImageButton) findViewById(R.id.imageButton)).setImageResource(R.drawable.remove_from_favourites);
    }


}
