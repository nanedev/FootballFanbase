package com.malikbisic.sportapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.AllFixturesAdapter;
import com.malikbisic.sportapp.adapter.SingleFixtureAdapter;
import com.malikbisic.sportapp.model.AllFixturesModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SingleFixtureActivity extends AppCompatActivity {
    TextView countryNameTextview;
    TextView leagueNameTextview;
    Intent intent;
    String getCountryName;
    String getLeagueName;
    int leagueID;
    ArrayList<AllFixturesModel> singleFixturesList = new ArrayList<>();
    RecyclerView singleRecView;
    SingleFixtureAdapter adapter;


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

    String dateUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fixture);
        countryNameTextview = (TextView) findViewById(R.id.imeDrzave);
        leagueNameTextview = (TextView) findViewById(R.id.nazivLige);
        intent = getIntent();
        getCountryName = intent.getStringExtra("countryName");
        getLeagueName = intent.getStringExtra("leagueName");
        dateUrl = intent.getStringExtra("datum");
        adapter = new SingleFixtureAdapter(singleFixturesList, this, this);
        singleRecView = (RecyclerView) findViewById(R.id.singleFixtureRecyclerview);
        singleRecView.setLayoutManager(new LinearLayoutManager(this));
        singleRecView.setAdapter(adapter);

        leagueID = intent.getIntExtra("leagueID", 0);
        countryNameTextview.setText(getCountryName);
        leagueNameTextview.setText(getLeagueName);

        Log.i("fixture", String.valueOf(leagueID));

        String url = "https://soccer.sportmonks.com/api/v2.0/fixtures/date/"+dateUrl+"?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s&include=localTeam,visitorTeam,league&leagues="+leagueID;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {


                    JSONArray dataArray = response.getJSONArray("data");

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject objectMain = dataArray.getJSONObject(i);
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
                            int minutes = timeMain.getInt("minute");

                            JSONObject leagueMain = objectMain.getJSONObject("league");
                            JSONObject dataLeague = leagueMain.getJSONObject("data");

                            leagueName = dataLeague.getString("name");




                            JSONObject scores = objectMain.getJSONObject("scores");
                            String localScore = scores.getString("localteam_score");
                            String visitScore = scores.getString("visitorteam_score");

                            ftScore = localScore + " - " + visitScore;

                            AllFixturesModel model = new AllFixturesModel(homeTeamName, homeTeamLogo, awayTeamName, awayTeamLogo, mstartTime, leagueName, datum, statusS,ftScore, idFixtures, localTeamId, visitorTeamId,minutes);
                            singleFixturesList.add(model);
                            adapter.notifyDataSetChanged();
                        }

                    } catch (JSONException e1) {
                    Log.e("exc", e1.getLocalizedMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("volley", error.getLocalizedMessage());
            }
        });

        Volley.newRequestQueue(SingleFixtureActivity.this).add(request);

    }
}
