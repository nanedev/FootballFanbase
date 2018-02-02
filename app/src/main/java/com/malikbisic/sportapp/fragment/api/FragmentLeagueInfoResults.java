package com.malikbisic.sportapp.fragment.api;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.StopAppServices;
import com.malikbisic.sportapp.adapter.api.FixturesLeagueAdapter;
import com.malikbisic.sportapp.model.api.FixturesLeagueModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Nane on 31.12.2017.
 */

public class FragmentLeagueInfoResults extends Fragment {

    public FragmentLeagueInfoResults() {
    }

    private String URL_BASE = "https://soccer.sportmonks.com/api/v2.0/leagues/";
    private int URL_LEAGUE_ID;
    private String URL_API = "?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s";
    private String URL_INCLUDES = "&include=season.results:order(starting_at|desc),season.results.localTeam,season.results.visitorTeam";
    RecyclerView leagueRecView;
    ArrayList<FixturesLeagueModel> modelArrayList = new ArrayList<>();
    ArrayList<String> dateList = new ArrayList<>();
    FixturesLeagueAdapter adapter;
    LinearLayoutManager layoutManager;

    String formattedDate;
    Date date;

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

    Button prevBtn;
    Button nextBtn;
    TextView dateLabel;
    String statusS;
    String ftScore;

    String prevDate = "";
    String currentDate = "";

    ProgressBar mDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_league_info_results, container, false);
        Intent closeAPP = new Intent(getContext(), StopAppServices.class);
        getActivity().startService(closeAPP);

        leagueRecView = (RecyclerView) view.findViewById(R.id.league_results_recView);
        adapter = new FixturesLeagueAdapter(modelArrayList, getActivity());
        layoutManager = new LinearLayoutManager(getActivity());
        leagueRecView.setLayoutManager(layoutManager);
        leagueRecView.setAdapter(adapter);
        URL_LEAGUE_ID = getActivity().getIntent().getIntExtra("league_id", 0);
        mDialog = (ProgressBar) view.findViewById(R.id.progressBarLeagueRes);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mDialog.setIndeterminate(true);
        loadResults();
    }

    public void loadResults(){
        mDialog.setVisibility(View.VISIBLE);

        String full_URL = URL_BASE + URL_LEAGUE_ID + URL_API + URL_INCLUDES;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, full_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject mainObject = response.getJSONObject("data");
                    JSONObject seasonObject = mainObject.getJSONObject("season");
                    JSONObject dataSeason = seasonObject.getJSONObject("data");
                    JSONObject fixturesObject = dataSeason.getJSONObject("results");
                    JSONArray fixturesData = fixturesObject.getJSONArray("data");

                    for (int i = 0; i < fixturesData.length(); i++){

                        JSONObject objectMain = fixturesData.getJSONObject(i);
                        idFixtures = objectMain.getString("id");

                        JSONObject locTeam = objectMain.getJSONObject("localTeam");
                        JSONObject visTeam = objectMain.getJSONObject("visitorTeam");
                        JSONObject localTeamObj = locTeam.getJSONObject("data");
                        JSONObject visitorTeamObj = visTeam.getJSONObject("data");

                        homeTeamName = localTeamObj.getString("name");
                        homeTeamLogo = localTeamObj.getString("logo_path");
                        localTeamId = localTeamObj.getInt("id");
                        awayTeamName = visitorTeamObj.getString("name");
                        awayTeamLogo = visitorTeamObj.getString("logo_path");
                        visitorTeamId = visitorTeamObj.getInt("id");

                        JSONObject timeMain = objectMain.getJSONObject("time");
                        JSONObject starting_at = timeMain.getJSONObject("starting_at");

                        mstartTime = starting_at.getString("time");
                        datum = starting_at.getString("date");
                        statusS = timeMain.getString("status");




                        if (datum.equals(prevDate)){

                            currentDate = "isti datum";
                        } else {
                            currentDate = starting_at.getString("date");
                        }


                        JSONObject scores = objectMain.getJSONObject("scores");
                        String localScore = scores.getString("localteam_score");
                        String visitScore = scores.getString("visitorteam_score");

                        ftScore = localScore + " - " + visitScore;

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            date = format.parse(datum);
                            System.out.println(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        android.text.format.DateFormat df = new android.text.format.DateFormat();
                        df.format("dd-MMM-yyyy", date);

                        FixturesLeagueModel model = new FixturesLeagueModel(homeTeamName, homeTeamLogo, awayTeamName, awayTeamLogo, mstartTime, date, currentDate, statusS, ftScore, idFixtures, localTeamId, visitorTeamId);
                        modelArrayList.add(model);

                        adapter.notifyDataSetChanged();
                        prevDate = datum;
                    }


                } catch (JSONException e) {
                    Log.e("responseErrorLeagFix", e.getLocalizedMessage());
                    mDialog.setVisibility(View.GONE);
                }
                mDialog.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("errorFixturesLeague", error.getLocalizedMessage());
                mDialog.setVisibility(View.GONE);
            }
        });
        Volley.newRequestQueue(getActivity()).add(request);
    }
}
