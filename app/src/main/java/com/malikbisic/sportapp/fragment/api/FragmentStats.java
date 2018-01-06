package com.malikbisic.sportapp.fragment.api;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.malikbisic.sportapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentStats extends Fragment {

    private String URL_BASE = "https://soccer.sportmonks.com/api/v2.0/fixtures/";
    private String URL_API = "?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s";
    private String URL_INCLUDE = "&include=stats";
    private String fixturesID;
    private String fullURL;


    public FragmentStats() {
        // Required empty public constructor
    }

    ProgressBar shotHomeProgress;
    ProgressBar shotAwayProgress;
    ProgressBar onGoalHomeProgress;
    ProgressBar onGoalAwayProgress;
    ProgressBar offGoalHomeProgress;
    ProgressBar offGoalAwayProgress;
    ProgressBar possessionHomeProgress;
    ProgressBar possessionAwayProgress;
    ProgressBar faulsHomeProgress;
    ProgressBar faulsAwayProgress;
    ProgressBar yellowHomeProgress;
    ProgressBar yellowAwayProgress;
    ProgressBar redHomeProgress;
    ProgressBar redAwayProgress;

    TextView shotHomeTextView;
    TextView shotAwayTextView;
    TextView onGoalHomeTextView;
    TextView onGoalAwayTextView;
    TextView offGoalHomeTextView;
    TextView offGoalAwayTextView;
    TextView possessionHomeTextView;
    TextView possessionAwayTextView;
    TextView faulsHomeTextView;
    TextView faulsAwayTextView;
    TextView yellowHomeTextView;
    TextView yellowAwayTextView;
    TextView redHomeTextView;
    TextView redAwayTextView;

    LinearLayout statsLayout;
    TextView noStats;

    int homeTeamId;
    int awayTeamId;

    int firstTeamStatsArray, secondTeamStatsArray;

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fragment_stats, container, false);

        fixturesID = getActivity().getIntent().getStringExtra("idFixtures");
        homeTeamId = getActivity().getIntent().getIntExtra("localTeamId", 0);
        awayTeamId = getActivity().getIntent().getIntExtra("visitorTeamId", 0);

        Log.i("ThomeId", String.valueOf(homeTeamId));
        Log.i("TAwayid", String.valueOf(awayTeamId));

        shotHomeProgress = (ProgressBar) v.findViewById(R.id.shotsHomeProgress);
        shotAwayProgress = (ProgressBar) v.findViewById(R.id.shotAwayProgress);
        onGoalHomeProgress = (ProgressBar) v.findViewById(R.id.ongoalHomeProgress);
        onGoalAwayProgress = (ProgressBar) v.findViewById(R.id.ongoalAwayProgress);
        offGoalHomeProgress = (ProgressBar) v.findViewById(R.id.offgoalHomeProgress);
        offGoalAwayProgress = (ProgressBar) v.findViewById(R.id.offgoalAwayProgress);
        possessionHomeProgress = (ProgressBar) v.findViewById(R.id.homePossessionProgress);
        possessionAwayProgress = (ProgressBar) v.findViewById(R.id.awayPossessionProgress);
        faulsHomeProgress = (ProgressBar) v.findViewById(R.id.homeFaulsProgress);
        faulsAwayProgress = (ProgressBar) v.findViewById(R.id.awayFaulsProgress);
        yellowHomeProgress = (ProgressBar) v.findViewById(R.id.homeYellowProgress);
        yellowAwayProgress = (ProgressBar) v.findViewById(R.id.awayYellowProgress);
        redHomeProgress = (ProgressBar) v.findViewById(R.id.homeRedProgress);
        redAwayProgress = (ProgressBar) v.findViewById(R.id.awayRedProgress);

        shotHomeTextView = (TextView) v.findViewById(R.id.homeShots);
        shotAwayTextView = (TextView) v.findViewById(R.id.awayShots);
        onGoalHomeTextView = (TextView) v.findViewById(R.id.homeOnGoalShots);
        onGoalAwayTextView = (TextView) v.findViewById(R.id.awayOnGoalShots);
        offGoalHomeTextView = (TextView) v.findViewById(R.id.homeOffGoalShots);
        offGoalAwayTextView = (TextView) v.findViewById(R.id.awayOffGoalShots);
        shotHomeTextView = (TextView) v.findViewById(R.id.homeShots);
        possessionHomeTextView = (TextView) v.findViewById(R.id.homePossession);
        possessionAwayTextView = (TextView) v.findViewById(R.id.awayPossession);
        faulsHomeTextView = (TextView) v.findViewById(R.id.homeFauls);
        faulsAwayTextView = (TextView) v.findViewById(R.id.awayFauls);
        yellowHomeTextView = (TextView) v.findViewById(R.id.homeYellow);
        yellowAwayTextView = (TextView) v.findViewById(R.id.awayYellow);
        redHomeTextView = (TextView) v.findViewById(R.id.homeRed);
        redAwayTextView = (TextView) v.findViewById(R.id.awayRed);

        statsLayout = (LinearLayout) v.findViewById(R.id.layoutStats);
        noStats = (TextView) v.findViewById(R.id.statsnotAvailable);

        fullURL = URL_BASE + fixturesID + URL_API + URL_INCLUDE;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, fullURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                int homeShot = 0, awayShot = 0, onGoalHome = 0, onGoalAway = 0, offGoalHome = 0, offGoalAway = 0, possesionHome = 0, possesionAway = 0, faulsHome = 0,
                        faulsAway = 0, yellowHome = 0, yellowAway = 0, redHome = 0, redAway = 0;

                String homeShotInfo, awayShotInfo, onGoalHomeInfo, onGoalAwayInfo, offGoalHomeInfo, offGoalAwayInfo, possessionHomeInfo, possessionAwayInfo,
                        faulsHomeInfo, faulsAwayInfo, yellowHomeInfo, yellowAwayInfo, redHomeInfo, redAwayInfo;

                String haveInfo = "have";
                String noInformation = "no information";
                try {
                    JSONObject mainObject = response.getJSONObject("data");


                        statsLayout.setVisibility(View.VISIBLE);
                        noStats.setVisibility(View.GONE);

                        JSONObject statsObject = mainObject.getJSONObject("stats");

                        JSONArray dataArray = statsObject.getJSONArray("data");

                    if (dataArray.length() == 0){
                        statsLayout.setVisibility(View.GONE);
                        noStats.setVisibility(View.VISIBLE);
                    } else {

                        if (homeTeamId < awayTeamId){
                            firstTeamStatsArray = 0;
                            secondTeamStatsArray = 1;
                        } else if (homeTeamId > awayTeamId){
                            firstTeamStatsArray = 1;
                            secondTeamStatsArray = 0;
                        }


                        //home team information stats
                        JSONObject homeTeamobject = dataArray.getJSONObject(firstTeamStatsArray);

                        JSONObject shotHomeObject = homeTeamobject.getJSONObject("shots");
                        if (!shotHomeObject.isNull("total")) {
                            homeShot = shotHomeObject.getInt("total");
                            homeShotInfo = haveInfo;
                        } else {
                            homeShotInfo = noInformation;
                        }
                        if (!shotHomeObject.isNull("ongoal")) {
                            onGoalHome = shotHomeObject.getInt("ongoal");
                            onGoalHomeInfo = haveInfo;
                        } else {
                            onGoalHomeInfo = noInformation;
                        }
                        if (!shotHomeObject.isNull("offgoal")) {
                            offGoalHome = shotHomeObject.getInt("offgoal");
                            offGoalHomeInfo = haveInfo;
                        } else {
                            offGoalHomeInfo = noInformation;
                        }

                        if (!homeTeamobject.isNull("possessiontime")) {
                            possesionHome = homeTeamobject.getInt("possessiontime");
                            possessionHomeInfo = haveInfo;
                        } else {
                            possessionHomeInfo = noInformation;
                        }
                        if (!homeTeamobject.isNull("fauls")) {
                            faulsHome = homeTeamobject.getInt("fauls");
                            faulsHomeInfo = haveInfo;
                        } else {
                            faulsHomeInfo = noInformation;
                        }
                        if (!homeTeamobject.isNull("yellowcards")) {
                            yellowHome = homeTeamobject.getInt("yellowcards");
                            yellowHomeInfo = haveInfo;
                        } else {
                            yellowHomeInfo = noInformation;
                        }

                        if (!homeTeamobject.isNull("redcards")) {
                            redHome = homeTeamobject.getInt("redcards");
                            redHomeInfo = haveInfo;
                        } else {
                            redHomeInfo = noInformation;
                        }

                        //away team information stats
                        JSONObject awayTeamobject = dataArray.getJSONObject(secondTeamStatsArray);

                        JSONObject shotAwayObject = awayTeamobject.getJSONObject("shots");
                        if (!shotAwayObject.isNull("total")) {
                            awayShot = shotAwayObject.getInt("total");
                            awayShotInfo = haveInfo;
                        } else {
                            awayShotInfo = noInformation;
                        }
                        if (!shotAwayObject.isNull("ongoal")) {
                            onGoalAway = shotAwayObject.getInt("ongoal");
                            onGoalAwayInfo = haveInfo;
                        } else {
                            onGoalAwayInfo = noInformation;
                        }
                        if (!shotAwayObject.isNull("offgoal")) {
                            offGoalAway = shotAwayObject.getInt("offgoal");
                            offGoalAwayInfo = haveInfo;
                        } else {
                            offGoalAwayInfo = noInformation;
                        }

                        if (!awayTeamobject.isNull("possessiontime")) {
                            possesionAway = awayTeamobject.getInt("possessiontime");
                            possessionAwayInfo = haveInfo;
                        } else {
                            possessionAwayInfo = noInformation;
                        }
                        if (!awayTeamobject.isNull("fauls")) {
                            faulsAway = awayTeamobject.getInt("fauls");
                            faulsAwayInfo = haveInfo;
                        } else {
                            faulsAwayInfo = noInformation;
                        }
                        if (!awayTeamobject.isNull("yellowcards")) {
                            yellowAway = awayTeamobject.getInt("yellowcards");
                            yellowAwayInfo = haveInfo;
                        } else {
                            yellowAwayInfo = noInformation;
                        }

                        if (!awayTeamobject.isNull("redcards")) {
                            redAway = awayTeamobject.getInt("redcards");
                            redAwayInfo = haveInfo;
                        } else {
                            redAwayInfo = noInformation;
                        }
                        int totalShot = homeShot + awayShot;
                        shotHomeProgress.setMax(totalShot);
                        shotAwayProgress.setMax(totalShot);
                        shotHomeProgress.setProgress(homeShot);
                        shotAwayProgress.setProgress(awayShot);

                        int ongoalTotal = onGoalHome + onGoalAway;
                        onGoalHomeProgress.setMax(ongoalTotal);
                        onGoalAwayProgress.setMax(ongoalTotal);
                        onGoalHomeProgress.setProgress(onGoalHome);
                        onGoalAwayProgress.setProgress(onGoalAway);

                        int offgoalTotal = offGoalHome + offGoalAway;
                        offGoalHomeProgress.setMax(offgoalTotal);
                        offGoalAwayProgress.setMax(offgoalTotal);
                        offGoalHomeProgress.setProgress(offGoalHome);
                        offGoalAwayProgress.setProgress(offGoalAway);

                        int possessionTotal = possesionHome + possesionAway;
                        possessionHomeProgress.setMax(possessionTotal);
                        possessionAwayProgress.setMax(possessionTotal);
                        possessionHomeProgress.setProgress(possesionHome);
                        possessionAwayProgress.setProgress(possesionAway);

                        int faulsTotal = faulsHome + faulsAway;
                        faulsHomeProgress.setMax(faulsTotal);
                        faulsAwayProgress.setMax(faulsTotal);
                        faulsHomeProgress.setProgress(faulsHome);
                        faulsAwayProgress.setProgress(faulsAway);

                        int yellowTotal = yellowHome + yellowAway;
                        yellowHomeProgress.setMax(yellowTotal);
                        yellowAwayProgress.setMax(yellowTotal);
                        yellowHomeProgress.setProgress(yellowHome);
                        yellowAwayProgress.setProgress(yellowAway);

                        int redTotal = redHome + redAway;
                        redHomeProgress.setMax(redTotal);
                        redAwayProgress.setMax(redTotal);
                        redHomeProgress.setProgress(redHome);
                        redAwayProgress.setProgress(redAway);

                        if (homeShotInfo.equals(noInformation)) {
                            shotHomeTextView.setText("NO");
                        } else {
                            shotHomeTextView.setText(Integer.toString(homeShot));
                        }

                        if (awayShotInfo.equals(noInformation)) {
                            shotAwayTextView.setText("NO");
                        } else {
                            shotAwayTextView.setText(Integer.toString(awayShot));
                        }

                        if (onGoalHomeInfo.equals(noInformation)) {
                            onGoalHomeTextView.setText("NO");
                        } else {
                            onGoalHomeTextView.setText(Integer.toString(onGoalHome));
                        }

                        if (onGoalAwayInfo.equals(noInformation)) {
                            onGoalAwayTextView.setText("NO");
                        } else {
                            onGoalAwayTextView.setText(Integer.toString(onGoalAway));
                        }

                        if (offGoalHomeInfo.equals(noInformation)) {
                            offGoalHomeTextView.setText("NO");
                        } else {
                            offGoalHomeTextView.setText(Integer.toString(offGoalHome));
                        }

                        if (offGoalAwayInfo.equals(noInformation)) {
                            offGoalAwayTextView.setText("NO");
                        } else {
                            offGoalAwayTextView.setText(Integer.toString(offGoalAway));
                            Log.i("away of goal", String.valueOf(offGoalAway));
                        }

                        if (possessionHomeInfo.equals(noInformation)) {
                            possessionHomeTextView.setText("NO");
                        } else {
                            possessionHomeTextView.setText(Integer.toString(possesionHome) +"%");
                        }

                        if (possessionAwayInfo.equals(noInformation)) {
                            possessionAwayTextView.setText("NO");
                        } else {
                            possessionAwayTextView.setText(Integer.toString(possesionAway) + "%");
                        }

                        if (faulsHomeInfo.equals(noInformation)) {
                            faulsHomeTextView.setText("NO");
                        } else {
                            faulsHomeTextView.setText(Integer.toString(faulsHome));
                        }

                        if (faulsAwayInfo.equals(noInformation)) {
                            faulsAwayTextView.setText("NO");
                        } else {
                            faulsAwayTextView.setText(Integer.toString(faulsAway));
                        }

                        if (yellowHomeInfo.equals(noInformation)) {
                            yellowHomeTextView.setText("NO");
                        } else {
                            yellowHomeTextView.setText(Integer.toString(yellowHome));
                        }

                        if (yellowAwayInfo.equals(noInformation)) {
                            yellowAwayTextView.setText("NO");
                        } else {
                            yellowAwayTextView.setText(Integer.toString(yellowAway));
                        }

                        if (redHomeInfo.equals(noInformation)) {
                            redHomeTextView.setText("NO");
                        } else {
                            redHomeTextView.setText(Integer.toString(redHome));
                        }

                        if (redAwayInfo.equals(noInformation)) {
                            redHomeTextView.setText("NO");
                        } else {
                            redAwayTextView.setText(Integer.toString(redAway));
                        }
                    }
                } catch (JSONException e) {
                    Log.e("JSON ERROR STATS", e.getLocalizedMessage());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR VOLLEY STATS", error.getLocalizedMessage());
            }
        });

        Volley.newRequestQueue(getContext()).add(request);


        return v;
    }

}
