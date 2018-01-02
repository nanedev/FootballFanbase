package com.malikbisic.sportapp.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.malikbisic.sportapp.adapter.TableAdapter;
import com.malikbisic.sportapp.model.SvgDrawableTranscoder;
import com.malikbisic.sportapp.model.TableModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLeagueInfoStandings extends Fragment {
    private String URL_STANDINGS = "https://soccer.sportmonks.com/api/v2.0/standings/season/";
    Intent intent;
    String currentSeasonId;
    String leagueName;
    String finalUrl;
    RecyclerView tableRecyclerview;
    TableAdapter adapter;
    ArrayList<TableModel> tableListStandings = new ArrayList<>();
    final String URL_APIKEY = "?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s";
    private String INCLUDE_IN_URL = "&include=standings.league.country%2Cstandings.team";
    TextView leagueNameTextview;
    ProgressDialog mDialog;
    RelativeLayout tableLayout;
    TextView tableTextview;
    Toolbar toolbar;
    TextView leagueNameInToolbar;
    String countryName;
    TextView countryNameTextview;
    TableModel model;
    CircleImageView flag;

    public FragmentLeagueInfoStandings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_league_info_standings, container, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        leagueNameTextview = (TextView) view.findViewById(R.id.leaguenamestandings);
        tableRecyclerview = (RecyclerView) view.findViewById(R.id.league_info_recycler_view);
        tableRecyclerview.setLayoutManager(layoutManager);
        countryNameTextview = (TextView) view.findViewById(R.id.countrynamestandings);
        flag = (CircleImageView) view.findViewById(R.id.country_image_standings);
        adapter = new TableAdapter(tableListStandings, getActivity().getApplicationContext(), getActivity());
        mDialog = new ProgressDialog(getActivity());
        mDialog.setIndeterminate(true);
        mDialog.setMessage("Loading...");
        mDialog.show();
        tableLayout = (RelativeLayout) view.findViewById(R.id.tablefragmentlayout);
        tableTextview = (TextView) view.findViewById(R.id.tableTextTextview);
        tableLayout.setActivated(true);
        tableTextview.setTextColor(Color.parseColor("#ffffff"));
        toolbar = (Toolbar) view.findViewById(R.id.toolbarLeagueInfo);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        leagueNameInToolbar = (TextView) view.findViewById(R.id.leaguenameinleagueinfotoolbar);


        tableRecyclerview.setAdapter(adapter);


        intent = getActivity().getIntent();
        currentSeasonId = intent.getStringExtra("seasonId");
        leagueName = intent.getStringExtra("leagueName");
        countryName = intent.getStringExtra("countryName");
        leagueNameTextview.setText(leagueName);
        countryNameTextview.setText(countryName);
        finalUrl = URL_STANDINGS + currentSeasonId + URL_APIKEY + INCLUDE_IN_URL;

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
                            model = new TableModel(position, teamId, teamName, played, wins, draws, lost, goalScored, goalConcided, goalDif, points, logo, countryId, country,result);
                            tableListStandings.add(model);


                            if (model.getCountryName().equals("England")) {
                                flag.setImageDrawable(getResources().getDrawable(R.drawable.england));
                            } else if (model.getCountryName().equals("Northern Ireland")) {
                                flag.setImageDrawable(getResources().getDrawable(R.drawable.northern_ireland));
                            } else if (model.getCountryName().equals("Scotland")) {
                                flag.setImageDrawable(getResources().getDrawable(R.drawable.scotland));
                            } else if (model.getCountryName().equals("Wales")) {
                                flag.setImageDrawable(getResources().getDrawable(R.drawable.welsh_flag));
                            }


                            String countryURL = "https://restcountries.eu/rest/v2/name/" + model.getCountryName();

                            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, countryURL, null, new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    Log.i("json", response.toString());
                                    try {
                                        for (int i = 0; i < response.length(); i++) {

                                            JSONObject object = response.getJSONObject(i);
                                            String countryName = object.getString("name");
                                            String countryImage = object.getString("flag");
                                            System.out.println("country flag" + countryImage);
                                            GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;

                                            requestBuilder = Glide
                                                    .with(getActivity())
                                                    .using(Glide.buildStreamModelLoader(Uri.class, getActivity()), InputStream.class)
                                                    .from(Uri.class)
                                                    .as(SVG.class)
                                                    .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                                                    .sourceEncoder(new StreamEncoder())
                                                    .cacheDecoder(new FileToStreamDecoder<SVG>(new SearchableCountry.SvgDecoder()))
                                                    .decoder(new SearchableCountry.SvgDecoder())
                                                    .animate(android.R.anim.fade_in);


                                            Uri uri = Uri.parse(countryImage);

                                            requestBuilder
                                                    // SVG cannot be serialized so it's not worth to cache it
                                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                                    .load(uri)
                                                    .into(flag);

                                        }
                                        adapter.notifyDataSetChanged();
                                        mDialog.dismiss();


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
                            Volley.newRequestQueue(getActivity()).add(request);
                        }


                    }

                } catch (JSONException e) {
                    Log.v("Excpetion JSON", "Err: " + e.getLocalizedMessage());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LEAGUE", "Err: " + error.getLocalizedMessage());
            }
        });


        Volley.newRequestQueue(getActivity()).add(standingsRequest);


        return view;
    }

}
