package com.malikbisic.sportapp.activity.api;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.StopAppServices;
import com.malikbisic.sportapp.adapter.api.SectionPageAdapter;
import com.malikbisic.sportapp.adapter.api.TableAdapter;
import com.malikbisic.sportapp.fragment.api.FragmentLeagueInfoFixtures;
import com.malikbisic.sportapp.fragment.api.FragmentLeagueInfoResults;
import com.malikbisic.sportapp.fragment.api.FragmentLeagueInfoStandings;
import com.malikbisic.sportapp.model.api.SvgDrawableTranscoder;
import com.malikbisic.sportapp.model.api.TableModel;
import com.malikbisic.sportapp.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class LeagueInfoActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private SectionPageAdapter sectionPageAdapter;
    Toolbar toolbar;
    Intent intent;
    String leagueName;
    TextView leaguNameTextview;
    String currentSeasonId;
    String countryName;
    RelativeLayout toolbarlayout;
    TextView countryNameTextview;
     RecyclerView tableRecyclerview;
    TableAdapter adapter;
    AlertDialog mDialog;
    String finalUrl;
    private String URL_STANDINGS = "https://soccer.sportmonks.com/api/v2.0/standings/season/";
    private String INCLUDE_IN_URL = "&include=standings.league.country%2Cstandings.team";
    TableModel model;
    ArrayList<TableModel> tableListStandings = new ArrayList<>();
    CircleImageView flag;
    RelativeLayout tableLayout;


    RelativeLayout parentChampionsleague;
    RelativeLayout parentChampionsLEagueQualif;
    RelativeLayout parentEuropeLeague;
    RelativeLayout parentEuroeLeagueQualif;
    RelativeLayout parentRelegation;
    RelativeLayout parentRelegationDoig;




    TextView championsLeagueTextview;
    TextView championsLeagueQualifTextview;


    TextView europeLeagueTextview;
    TextView europeLeagueQualifTextview;
    TextView relegationTextview;
    TextView relegationQualifTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_info);
        Intent closeAPP = new Intent(this, StopAppServices.class);
        startService(closeAPP);
toolbar = (Toolbar)  findViewById(R.id.toolbarleagueingo);


toolbarlayout =(RelativeLayout) findViewById(R.id.layoutprofiletoolbar);
leaguNameTextview = (TextView) findViewById(R.id.leaguenamestandings);
countryNameTextview = (TextView) findViewById(R.id.countrynamestandings);
        flag = (CircleImageView) findViewById(R.id.country_image_standings);
        tableLayout = (RelativeLayout)findViewById(R.id.tableinfolayout);
        championsLeagueTextview = (TextView) findViewById(R.id.textforchampionleague);
        europeLeagueTextview = (TextView) findViewById(R.id.textforeuropeleague);
        relegationTextview = (TextView) findViewById(R.id.textforrelegation);


        championsLeagueQualifTextview = (TextView) findViewById(R.id.textforchampionleagueQualif);
        europeLeagueQualifTextview = (TextView) findViewById(R.id.textforeuropeleagueQualif);
        relegationQualifTextview = (TextView) findViewById(R.id.textforrelegationQualif);

        parentChampionsleague = (RelativeLayout) findViewById(R.id.parentchampions);
        parentChampionsLEagueQualif = (RelativeLayout) findViewById(R.id.parentchampionsQualif);
        parentEuropeLeague = (RelativeLayout) findViewById(R.id.parenteuropeleague);
        parentEuroeLeagueQualif = (RelativeLayout) findViewById(R.id.parenteuropeleagueQualif);
        parentRelegation = (RelativeLayout) findViewById(R.id.parentrelegation);
        parentRelegationDoig = (RelativeLayout) findViewById(R.id.parentrelegationQualif);



        LinearLayoutManager layoutManager = new LinearLayoutManager(LeagueInfoActivity.this);

        tableRecyclerview = (RecyclerView) findViewById(R.id.league_info_recycler_view);
        tableRecyclerview.setLayoutManager(layoutManager);

        mDialog = new SpotsDialog(LeagueInfoActivity.this,"Loading",R.style.StyleLogin);



        intent = getIntent();

        currentSeasonId = intent.getStringExtra("seasonId");
        leagueName = intent.getStringExtra("leagueName");
        countryName = intent.getStringExtra("countryName");
        leaguNameTextview.setText(leagueName);
        countryNameTextview.setText(countryName);



standingsTable();



    }


    public void standingsTable() {
        mDialog.show();
        adapter = new TableAdapter(tableListStandings, LeagueInfoActivity.this, LeagueInfoActivity.this);
        tableRecyclerview.setAdapter(adapter);

        finalUrl = URL_STANDINGS + currentSeasonId + Constants.API_KEY + INCLUDE_IN_URL;
        JsonObjectRequest standingsRequest = new JsonObjectRequest(Request.Method.GET, finalUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int position = 0;
                int teamId = 0;
                String teamName = "";
                int played = 0;
                int wins = 0;
                int draws = 0;
                int goalScored = 0;
                int goalConcided = 0;
                int lost = 0;
                String logo = "";
                String goalDif = "";
                String countryId = "";
                int points = 0;
                String result;
                try {
                    JSONArray dataList = response.getJSONArray("data");

                    for (int i = 0; i < dataList.length(); i++) {
                        JSONObject getObjectFromList = dataList.getJSONObject(i);
                        JSONObject standings = getObjectFromList.getJSONObject("standings");
                        JSONArray getDataList = standings.getJSONArray("data");
                        tableListStandings.clear();

                        for (int x = 0; x < getDataList.length(); x++) {

                            JSONObject getData = getDataList.getJSONObject(x);
                            position = getData.getInt("position");
                            teamId = getData.getInt("team_id");
                            teamName = getData.getString("team_name");
                            result = getData.getString("result");
                            JSONObject overall = getData.getJSONObject("overall");
                            played = overall.getInt("games_played");
                            wins = overall.getInt("won");
                            draws = overall.getInt("draw");
                            lost = overall.getInt("lost");
                            goalScored = overall.getInt("goals_scored");
                            goalConcided = overall.getInt("goals_against");


                            JSONObject total = getData.getJSONObject("total");
                            goalDif = total.getString("goal_difference");
                            points = total.getInt("points");

                            JSONObject team = getData.getJSONObject("team");
                            JSONObject getTeamJsonObj = team.getJSONObject("data");
                            logo = getTeamJsonObj.getString("logo_path");
                            countryId = getTeamJsonObj.getString("country_id");

                            JSONObject getCountryLeague = getData.getJSONObject("league");
                            JSONObject getCountryData = getCountryLeague.getJSONObject("data");
                            JSONObject getCountry = getCountryData.getJSONObject("country");
                            JSONObject getCountryName = getCountry.getJSONObject("data");
                            String country = getCountryName.getString("name");

                            Log.i("pozicija", String.valueOf(position));
                            Log.i("poena", String.valueOf(points));
                            model = new TableModel(position, teamId, teamName, played, wins, draws, lost, goalScored, goalConcided, goalDif, points, logo, countryId, country, result);
                            tableListStandings.add(model);

                            if (model.getCountryName().equals("England")) {
                                flag.setImageDrawable(LeagueInfoActivity.this.getResources().getDrawable(R.drawable.england));
                            } else if (model.getCountryName().equals("Northern Ireland")) {
                                flag.setImageDrawable(LeagueInfoActivity.this.getResources().getDrawable(R.drawable.northern_ireland));
                            } else if (model.getCountryName().equals("Scotland")) {
                                flag.setImageDrawable(LeagueInfoActivity.this.getResources().getDrawable(R.drawable.scotland));
                            } else if (model.getCountryName().equals("Wales")) {
                                flag.setImageDrawable(LeagueInfoActivity.this.getResources().getDrawable(R.drawable.welsh_flag));
                            } else if (model.getCountryName().equals("Europe")){
                                flag.setImageDrawable(LeagueInfoActivity.this.getResources().getDrawable(R.drawable.europe));

                            }

                            else {


                                String countryURL = "http://countryapi.gear.host/v1/Country/getCountries?pName=" + model.getCountryName();

                                final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, countryURL, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        try {
                                            JSONArray arrayLeague = response.getJSONArray("Response");
                                            for (int i = 0; i < arrayLeague.length(); i++) {

                                                JSONObject object = arrayLeague.getJSONObject(i);
                                                String countryName = object.getString("Name");
                                                String countryImage = object.getString("FlagPng");

                                                System.out.println("country flag" + countryImage);

                                                Glide.with(LeagueInfoActivity.this).load(countryImage).diskCacheStrategy(DiskCacheStrategy.ALL).into(flag);

                                            }


                                        } catch (JSONException e) {
                                            Log.v("json", e.getLocalizedMessage());
                                        }

                                    }

                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //Log.v("json", error.getLocalizedMessage());
                                    }
                                });
                                Volley.newRequestQueue(LeagueInfoActivity.this).add(request);

                            }


                            if (model.getResult().equals("Promotion - Champions League (Group Stage)")) {
                                parentChampionsleague.setVisibility(View.VISIBLE);
                                championsLeagueTextview.setText(leagueName + " - " + " Champions League (Group Stage)");

                            }
                            if (model.getResult().equals("Promotion - Champions League (Qualification)")){
                                parentChampionsLEagueQualif.setVisibility(View.VISIBLE);
                                championsLeagueQualifTextview.setText(leagueName + " - " + " Champions League (Qualification)");

                            }
                            if (model.getResult().equals("Promotion - Europa League (Group Stage)")){
                                parentEuropeLeague.setVisibility(View.VISIBLE);
                                europeLeagueTextview.setText(leagueName + "Europa League (Group Stage)");
                            }
                            if (model.getResult().equals("Promotion - Europa League (Qualification)")){
                              parentEuroeLeagueQualif.setVisibility(View.VISIBLE);
                              europeLeagueQualifTextview.setText(leagueName + " Europa League (Qualification)");
                            }
                            if (model.getResult().equals("Relegation - Ligue 2")){
                                  parentRelegation.setVisibility(View.VISIBLE);
                                  relegationTextview.setText(leagueName + " Relegation - Ligue 2");
                            }
                            if (model.getResult().equals("Ligue 1 (Relegation)")){
                              parentRelegationDoig.setVisibility(View.VISIBLE);
                              relegationQualifTextview.setText(leagueName + " Ligue 1 (Relegation)");
                            }
                            adapter.notifyDataSetChanged();

                        }
                    }

                } catch (JSONException e) {
                    Log.v("Excpetion JSON", "Err: " + e.getLocalizedMessage());
                    mDialog.dismiss();
                }
                mDialog.dismiss();

            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LEAGUE", "Err: " + error.getLocalizedMessage());
                mDialog.dismiss();
            }
        });


        Volley.newRequestQueue(

                LeagueInfoActivity.this).

                add(standingsRequest);

    }


}
