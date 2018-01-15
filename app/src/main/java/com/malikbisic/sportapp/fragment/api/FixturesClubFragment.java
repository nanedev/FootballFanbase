
package com.malikbisic.sportapp.fragment.api;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.api.ClubFixturesAdapter;
import com.malikbisic.sportapp.model.api.ClubFixturesModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FixturesClubFragment extends Fragment {

    String URL_BASE = "https://soccer.sportmonks.com/api/v2.0/teams/";
    String teamID;
    String URL_API = "?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s";
    String URL_INCLUDES = "&include=upcoming:order(starting_at|asc),upcoming.localTeam,upcoming.visitorTeam,upcoming.league";

    ArrayList<ClubFixturesModel> clubFixturesModelArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    ClubFixturesAdapter adapter;

    public FixturesClubFragment() {
        // Required empty public constructor
    }

    String homeTeamName;
    String awayTeamName;
    String homeTeamLogo;
    String awayTeamLogo;
    String leagueName;
    String mstartTime;
    String datum;
    String idFixtures;
    int localTeamId;
    int visitorTeamId;
    String statusS;
    String ftScore;

    String prevLeagueName = "";
    String currentLeagueName = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fixtures_club, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.fixturesClub_recView);
        adapter = new ClubFixturesAdapter(clubFixturesModelArrayList, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        teamID = getActivity().getIntent().getStringExtra("teamId");

        String fullURL = URL_BASE + teamID + URL_API + URL_INCLUDES;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, fullURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject objectMain = response.getJSONObject("data");
                    JSONObject upcomigObject = objectMain.getJSONObject("upcoming");
                    JSONArray dataArray = upcomigObject.getJSONArray("data");

                    for (int i = 0; i < dataArray.length(); i++){
                        JSONObject objectInArray = dataArray.getJSONObject(i);

                        JSONObject timeObject = objectInArray.getJSONObject("time");
                        JSONObject startTimeObject = timeObject.getJSONObject("starting_at");
                        mstartTime = startTimeObject.getString("time");
                        datum = startTimeObject.getString("date");
                        statusS = timeObject.getString("status");

                        JSONObject locTeam = objectInArray.getJSONObject("localTeam");
                        JSONObject visTeam = objectInArray.getJSONObject("visitorTeam");
                        JSONObject localTeamObj = locTeam.getJSONObject("data");
                        JSONObject visitorTeamObj = visTeam.getJSONObject("data");

                        homeTeamName = localTeamObj.getString("name");
                        homeTeamLogo = localTeamObj.getString("logo_path");
                        localTeamId = localTeamObj.getInt("id");
                        awayTeamName = visitorTeamObj.getString("name");
                        awayTeamLogo = visitorTeamObj.getString("logo_path");
                        visitorTeamId = visitorTeamObj.getInt("id");

                        JSONObject leagueMain = objectInArray.getJSONObject("league");
                        JSONObject dataLeague = leagueMain.getJSONObject("data");

                        currentLeagueName = dataLeague.getString("name");

                        if (currentLeagueName.equals(prevLeagueName)) {
                            leagueName = "";
                        } else {
                            leagueName = currentLeagueName;
                        }

                        ClubFixturesModel model = new ClubFixturesModel(homeTeamName, homeTeamLogo, awayTeamName, awayTeamLogo,mstartTime, datum, leagueName, statusS);
                        clubFixturesModelArrayList.add(model);
                        adapter.notifyDataSetChanged();
                        prevLeagueName = currentLeagueName;

                    }


                } catch (JSONException e) {
                    Log.e("clubJSOn", e.getLocalizedMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("club errorVolley", error.getLocalizedMessage());
            }
        });

        Volley.newRequestQueue(getContext()).add(request);


        return  v;
    }

}
