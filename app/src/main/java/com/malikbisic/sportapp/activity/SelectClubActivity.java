package com.malikbisic.sportapp.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.ClubAdapter;
import com.malikbisic.sportapp.model.ClubModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SelectClubActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    final String URL_BASE = "https://soccer.sportmonks.com/api/v2.0/teams";
    final String URL_LEAGUEID = "/season/";
    final String URL_APIKEY = "?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s";

    ArrayList<ClubModel> club = new ArrayList<>();
    ClubAdapter adapter;
    RecyclerView clubRecView;
    SearchView searchViewClub;
    Intent myIntent;

    //nesto

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_club);
        myIntent = getIntent();
        adapter = new ClubAdapter(club, this, this);
        clubRecView = (RecyclerView) findViewById(R.id.rec_view_favoriteClub);
        clubRecView.setAdapter(adapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        clubRecView.setLayoutManager(layoutManager);
        searchViewClub = (SearchView) findViewById(R.id.search_club);
        searchViewClub.setOnQueryTextListener(this);


        String leagueID = URL_LEAGUEID + myIntent.getStringExtra("leagueID");

        final String url = URL_BASE + leagueID + URL_APIKEY;

        searchViewClub.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                newText = newText.toLowerCase();
                ArrayList<ClubModel> newList = new ArrayList<>();
                for (ClubModel clubModel : club) {
                    String name = clubModel.getName().toLowerCase();
                    if (name.contains(newText)) {

                        newList.add(clubModel);


                    }
                }
                clubRecView.setLayoutManager(new LinearLayoutManager(SelectClubActivity.this));
                adapter = new ClubAdapter(newList, SelectClubActivity.this, SelectClubActivity.this);
                clubRecView.setAdapter(adapter);
                adapter.notifyDataSetChanged();


                return true;
            }

        });



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

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return true;
    }

    public static class ClubViewHolder extends RecyclerView.ViewHolder {

        ImageView clubLogo;
        TextView clubName;
        Context ctx;
        public View vm;

        ArrayList<ClubModel> clubs = new ArrayList<>();
        public ClubViewHolder(View itemView, ArrayList<ClubModel> clubs) {
            super(itemView);
            vm = itemView;
            clubLogo = (ImageView) vm.findViewById(R.id.club_logo);
            clubName = (TextView) vm.findViewById(R.id.club_Name);
            ctx = itemView.getContext();
            this.clubs = clubs;
        }

        public void updateUI(ClubModel model){

            clubName.setText(model.getName());
            Picasso.with(ctx).load(model.getLogo_path()).into(clubLogo);
        }
    }
}
