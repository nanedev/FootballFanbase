package com.malikbisic.sportapp.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.TableAdapter;
import com.malikbisic.sportapp.model.TableModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
    private String INCLUDE_IN_URL = "&include=standings.team";
    TextView leagueNameTextview;

    public FragmentLeagueInfoStandings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_league_info_standings, container, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        leagueNameTextview = (TextView) view.findViewById(R.id.name_of_league);
        tableRecyclerview = (RecyclerView) view.findViewById(R.id.league_info_recycler_view);
        tableRecyclerview.setLayoutManager(layoutManager);
        adapter = new TableAdapter(tableListStandings, getActivity().getApplicationContext(),getActivity());


        tableRecyclerview.setAdapter(adapter);


        intent = getActivity().getIntent();
        currentSeasonId = intent.getStringExtra("leagueID");
        leagueName = intent.getStringExtra("leagueName");
        leagueNameTextview.setText(leagueName);
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
                int points = 0;
                try {
                    JSONArray dataList = response.getJSONArray("data");

                    for (int i = 0; i < dataList.length(); i++) {
                        JSONObject getObjectFromList = dataList.getJSONObject(i);
                        JSONObject standings = getObjectFromList.getJSONObject("standings");
                        JSONArray getDataList = standings.getJSONArray("data");

                        for (int x = 0; x < getDataList.length(); x++) {

                            JSONObject getData = getDataList.getJSONObject(x);
                            position = getData.getInt("position");
                            teamId = getData.getInt("team_id");
                            teamName = getData.getString("team_name");
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


                            Log.i("pozicija", String.valueOf(position));
                            Log.i("poena", String.valueOf(points));

                            Log.i("played", String.valueOf(played));
                            Log.i("draws", String.valueOf(draws));
                            Log.i("goaldif", String.valueOf(goalDif));
                            TableModel model = new TableModel(position, teamId, teamName, played, wins, draws, lost, goalScored, goalConcided, goalDif, points, logo);
                            tableListStandings.add(model);
                        }

                        adapter.notifyDataSetChanged();
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
