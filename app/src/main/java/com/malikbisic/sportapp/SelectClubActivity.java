package com.malikbisic.sportapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SelectClubActivity extends AppCompatActivity {

    final String URL_BASE = "https://soccer.sportmonks.com/api/v2.0/teams";
    final String URL_LEAGUEID = "/season/";
    final String URL_APIKEY = "?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s";

    ArrayList<ClubModel> club = new ArrayList<>();
    ClubAdapter adapter;
    RecyclerView clubRecView;

    Intent myIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_club);
        myIntent = getIntent();

        clubRecView = (RecyclerView) findViewById(R.id.rec_view_favoriteClub);
        adapter = new ClubAdapter(club, getApplicationContext());
        clubRecView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        clubRecView.setLayoutManager(layoutManager);


        String leagueID = URL_LEAGUEID + myIntent.getStringExtra("leagueID");

        final String url = URL_BASE + leagueID + URL_APIKEY;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray array = response.getJSONArray("data");

                    for (int i = 0; i < array.length(); i++){
                        JSONObject clubObject = array.getJSONObject(i);

                        String name = clubObject.getString("name");
                        String logo_path = clubObject.getString("logo_path");

                        ClubModel clubModel = new ClubModel(name, logo_path);

                        club.add(clubModel);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                adapter.notifyDataSetChanged();
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("CLUB", "Err: " + error.getLocalizedMessage());
            }
        });

        Volley.newRequestQueue(this).add(request);

    }

    public static class ClubViewHolder extends RecyclerView.ViewHolder {

        ImageView clubLogo;
        TextView clubName;
        Context ctx;
        View vm;

        public ClubViewHolder(View itemView) {
            super(itemView);
            vm = itemView;
            clubLogo = (ImageView) vm.findViewById(R.id.club_logo);
            clubName = (TextView) vm.findViewById(R.id.club_Name);
            ctx = itemView.getContext();
        }

        public void updateUI(ClubModel model){

            clubName.setText(model.getName());
            Picasso.with(ctx).load(model.getLogo_path()).into(clubLogo);
        }
    }
}
