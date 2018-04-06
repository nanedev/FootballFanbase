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
import android.widget.Button;
import android.widget.ProgressBar;
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
import com.malikbisic.sportapp.adapter.api.FixturesLeagueAdapter;
import com.malikbisic.sportapp.adapter.api.SectionPageAdapter;
import com.malikbisic.sportapp.adapter.api.TableAdapter;
import com.malikbisic.sportapp.fragment.api.FragmentLeagueInfoFixtures;
import com.malikbisic.sportapp.fragment.api.FragmentLeagueInfoResults;
import com.malikbisic.sportapp.fragment.api.FragmentLeagueInfoStandings;
import com.malikbisic.sportapp.model.api.FixturesLeagueModel;
import com.malikbisic.sportapp.model.api.SvgDrawableTranscoder;
import com.malikbisic.sportapp.model.api.TableModel;
import com.malikbisic.sportapp.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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


    RelativeLayout standingsLayout;
    RelativeLayout resultsLayout;
    RelativeLayout fixturesLayout;

    RelativeLayout horizontalnTableLayout;

    //fixtures
    String URL_BASE_FIXTURES = "https://soccer.sportmonks.com/api/v2.0/leagues/";
    public static int URL_LEAGUE_ID_FIXTURES;
    private String URL_API = Constants.API_KEY;
    private String URL_INCLUDES_FIXTURES = "&include=season.upcoming:order(starting_at|asc),season.upcoming.localTeam,season.upcoming.visitorTeam";

    ArrayList<FixturesLeagueModel> modelArrayList_fixtures = new ArrayList<>();
    ArrayList<String> dateList_fixtures = new ArrayList<>();
    FixturesLeagueAdapter adapter_fixtures;

    String formattedDate_fixtures;
    Date date_fixtures;

    String homeTeamName_fixtures;
    String awayTeamName_fixtures;
    String homeTeamLogo_fixtures;
    String awayTeamLogo_fixtures;
    String leagueName_fixtures;
    String mstartTime_fixtures;
    String datum_fixtures;
    String idFixtures_fixtures;
    int localTeamId_fixtures;
    int visitorTeamId_fixtures;

    Button prevBtn_fixtures;
    Button nextBtn_fixtures;
    TextView dateLabel_fixtures;
    String statusS_fixtures;
    String ftScore_fixtures;

    String prevDate_fixtures = "";
    String currentDate_fixtures = "";

    //results

    private String URL_BASE_RESULT = "https://soccer.sportmonks.com/api/v2.0/leagues/";
    public static int URL_LEAGUE_ID_RESULT;
    private String URL_INCLUDES_RESULT = "&include=season.results:order(starting_at|desc),season.results.localTeam,season.results.visitorTeam";

    ArrayList<FixturesLeagueModel> modelArrayList_results = new ArrayList<>();
    ArrayList<String> dateList_results = new ArrayList<>();
    FixturesLeagueAdapter adapter_results;


    String formattedDate_results;
    Date date_results;

    String homeTeamName_results;
    String awayTeamName_results;
    String homeTeamLogo_results;
    String awayTeamLogo_results;
    String leagueName_results;
    String mstartTime_results;
    String datum_results;
    String idFixtures_results;
    int localTeamId_results;
    int visitorTeamId_results;

    Button prevBtn_results;
    Button nextBtn_results;
    TextView dateLabel_results;
    String statusS_results;
    String ftScore_results;

    String prevDate_results = "";
    String currentDate_results = "";

    TextView fixturesText;
    TextView standingsText;
    TextView resultsText;

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
        toolbar = (Toolbar) findViewById(R.id.toolbarleagueingo);


        toolbarlayout = (RelativeLayout) findViewById(R.id.layoutprofiletoolbar);
        leaguNameTextview = (TextView) findViewById(R.id.leaguenamestandings);
        countryNameTextview = (TextView) findViewById(R.id.countrynamestandings);
        flag = (CircleImageView) findViewById(R.id.country_image_standings);
        tableLayout = (RelativeLayout) findViewById(R.id.tableinfolayout);
        championsLeagueTextview = (TextView) findViewById(R.id.textforchampionleague);
        europeLeagueTextview = (TextView) findViewById(R.id.textforeuropeleague);
        relegationTextview = (TextView) findViewById(R.id.textforrelegation);
        horizontalnTableLayout = (RelativeLayout) findViewById(R.id.horizontaltablelayout);
        standingsLayout = (RelativeLayout) findViewById(R.id.standingslayout);
        resultsLayout = (RelativeLayout) findViewById(R.id.resultslayout);
        fixturesLayout = (RelativeLayout) findViewById(R.id.fixturesLayout);
        championsLeagueTextview = (TextView) findViewById(R.id.textforchampionleague);
        europeLeagueTextview = (TextView) findViewById(R.id.textforeuropeleague);
        relegationTextview = (TextView) findViewById(R.id.textforrelegation);
        fixturesText = (TextView) findViewById(R.id.fixtures);
        resultsText = (TextView) findViewById(R.id.results);
        standingsText = (TextView) findViewById(R.id.standingsText);


        championsLeagueQualifTextview = (TextView) findViewById(R.id.textforchampionleagueQualif);
        europeLeagueQualifTextview = (TextView) findViewById(R.id.textforeuropeleagueQualif);
        relegationQualifTextview = (TextView) findViewById(R.id.textforrelegationQualif);


        LinearLayoutManager layoutManager = new LinearLayoutManager(LeagueInfoActivity.this);

        tableRecyclerview = (RecyclerView) findViewById(R.id.league_info_recycler_view);
        tableRecyclerview.setLayoutManager(layoutManager);

        mDialog = new SpotsDialog(LeagueInfoActivity.this, "Loading", R.style.StyleLogin);


        intent = getIntent();

        currentSeasonId = intent.getStringExtra("seasonId");
        leagueName = intent.getStringExtra("leagueName");
        countryName = intent.getStringExtra("countryName");
        leaguNameTextview.setText(leagueName);
        countryNameTextview.setText(countryName);
        URL_LEAGUE_ID_FIXTURES = intent.getIntExtra("league_id", 0);
        URL_LEAGUE_ID_RESULT = intent.getIntExtra("league_id", 0);

        championsLeagueTextview.setText(leagueName + " - " + " Champions League ");
        europeLeagueTextview.setText(leagueName + " - " + " Europa League ");
        relegationTextview.setText(leagueName + " - " + " Relegation Group ");

        parentChampionsleague = (RelativeLayout) findViewById(R.id.parentchampions);
        parentChampionsLEagueQualif = (RelativeLayout) findViewById(R.id.parentchampionsQualif);
        parentEuropeLeague = (RelativeLayout) findViewById(R.id.parenteuropeleague);
        parentEuroeLeagueQualif = (RelativeLayout) findViewById(R.id.parenteuropeleagueQualif);
        parentRelegation = (RelativeLayout) findViewById(R.id.parentrelegation);
        parentRelegationDoig = (RelativeLayout) findViewById(R.id.parentrelegationQualif);


        fixturesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                horizontalnTableLayout.setVisibility(View.GONE);
                tableLayout.setVisibility(View.GONE);
                parentChampionsleague.setVisibility(View.GONE);
                parentChampionsLEagueQualif.setVisibility(View.GONE);
                parentEuropeLeague.setVisibility(View.GONE);
                parentEuroeLeagueQualif.setVisibility(View.GONE);
                parentRelegation.setVisibility(View.GONE);
                parentRelegationDoig.setVisibility(View.GONE);
                tableListStandings.clear();
                modelArrayList_results.clear();
                standingsLayout.setBackground(null);
                standingsText.setTextColor(getResources().getColor(R.color.white));
                resultsLayout.setBackground(null);
                resultsText.setTextColor(getResources().getColor(R.color.white));
                fixturesLayout.setBackgroundColor(getResources().getColor(R.color.white));
                fixturesText.setTextColor(getResources().getColor(R.color.primary));

                loadFixtures();

            }
        });

        resultsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                horizontalnTableLayout.setVisibility(View.GONE);
                tableLayout.setVisibility(View.GONE);
                parentChampionsleague.setVisibility(View.GONE);
                parentChampionsLEagueQualif.setVisibility(View.GONE);
                parentEuropeLeague.setVisibility(View.GONE);
                parentEuroeLeagueQualif.setVisibility(View.GONE);
                parentRelegation.setVisibility(View.GONE);
                parentRelegationDoig.setVisibility(View.GONE);
                tableListStandings.clear();
                modelArrayList_fixtures.clear();
                standingsLayout.setBackground(null);
                standingsText.setTextColor(getResources().getColor(R.color.white));
                fixturesLayout.setBackground(null);
                fixturesText.setTextColor(getResources().getColor(R.color.white));
                resultsLayout.setBackgroundColor(getResources().getColor(R.color.white));
                resultsText.setTextColor(getResources().getColor(R.color.primary));

                loadResults();

            }
        });

        standingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                horizontalnTableLayout.setVisibility(View.VISIBLE);
                tableLayout.setVisibility(View.VISIBLE);
                parentChampionsleague.setVisibility(View.VISIBLE);
                parentChampionsLEagueQualif.setVisibility(View.VISIBLE);
                parentEuropeLeague.setVisibility(View.VISIBLE);
                parentEuroeLeagueQualif.setVisibility(View.VISIBLE);
                parentRelegation.setVisibility(View.VISIBLE);
                parentRelegationDoig.setVisibility(View.VISIBLE);
                modelArrayList_results.clear();
                modelArrayList_fixtures.clear();
                resultsLayout.setBackground(null);
                resultsText.setTextColor(getResources().getColor(R.color.white));
                fixturesLayout.setBackground(null);
                fixturesText.setTextColor(getResources().getColor(R.color.white));
                standingsLayout.setBackgroundColor(getResources().getColor(R.color.white));
                standingsText.setTextColor(getResources().getColor(R.color.primary));

                standingsTable();

            }
        });

        standingsTable();


    }

    public void loadResults() {

        adapter_results = new FixturesLeagueAdapter(modelArrayList_results, LeagueInfoActivity.this);
        tableRecyclerview.setAdapter(adapter_results);

        mDialog.show();
        String full_URL = URL_BASE_RESULT + URL_LEAGUE_ID_RESULT + URL_API + URL_INCLUDES_RESULT;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, full_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject mainObject = response.getJSONObject("data");
                    JSONObject seasonObject = mainObject.getJSONObject("season");
                    JSONObject dataSeason = seasonObject.getJSONObject("data");
                    JSONObject fixturesObject = dataSeason.getJSONObject("results");
                    JSONArray fixturesData = fixturesObject.getJSONArray("data");

                    for (int i = 0; i < fixturesData.length(); i++) {

                        JSONObject objectMain = fixturesData.getJSONObject(i);
                        idFixtures_results = objectMain.getString("id");

                        JSONObject locTeam = objectMain.getJSONObject("localTeam");
                        JSONObject visTeam = objectMain.getJSONObject("visitorTeam");
                        JSONObject localTeamObj = locTeam.getJSONObject("data");
                        JSONObject visitorTeamObj = visTeam.getJSONObject("data");

                        homeTeamName_results = localTeamObj.getString("name");
                        homeTeamLogo_results = localTeamObj.getString("logo_path");
                        localTeamId_results = localTeamObj.getInt("id");
                        awayTeamName_results = visitorTeamObj.getString("name");
                        awayTeamLogo_results = visitorTeamObj.getString("logo_path");
                        visitorTeamId_results = visitorTeamObj.getInt("id");

                        JSONObject timeMain = objectMain.getJSONObject("time");
                        JSONObject starting_at = timeMain.getJSONObject("starting_at");

                        mstartTime_results = starting_at.getString("time");
                        datum_results = starting_at.getString("date");
                        statusS_results = timeMain.getString("status");


                        if (datum_results.equals(prevDate_results)) {

                            currentDate_results = "isti datum";
                        } else {
                            currentDate_results = starting_at.getString("date");
                        }


                        JSONObject scores = objectMain.getJSONObject("scores");
                        String localScore = scores.getString("localteam_score");
                        String visitScore = scores.getString("visitorteam_score");

                        ftScore_results = localScore + " - " + visitScore;

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            date_results = format.parse(datum_results);
                            System.out.println(date_results);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        android.text.format.DateFormat df = new android.text.format.DateFormat();
                        df.format("dd-MMM-yyyy", date_results);

                        FixturesLeagueModel model = new FixturesLeagueModel(homeTeamName_results, homeTeamLogo_results, awayTeamName_results, awayTeamLogo_results, mstartTime_results, date_results, currentDate_results, statusS_results, ftScore_results, idFixtures_results, localTeamId_results, visitorTeamId_results);
                        modelArrayList_results.add(model);

                        adapter_results.notifyDataSetChanged();
                        prevDate_results = datum_results;
                    }


                } catch (JSONException e) {
                    Log.e("responseErrorLeagFix", e.getLocalizedMessage());
                    mDialog.dismiss();
                }
                mDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("errorFixturesLeague", error.getLocalizedMessage());
                mDialog.dismiss();
            }
        });
        Volley.newRequestQueue(LeagueInfoActivity.this).add(request);
    }


    public void loadFixtures() {

        mDialog.show();
        adapter_fixtures = new FixturesLeagueAdapter(modelArrayList_fixtures, LeagueInfoActivity.this);
        tableRecyclerview.setAdapter(adapter_fixtures);
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();

        String myTimezone = tz.getID();


        String full_URL = URL_BASE_FIXTURES + URL_LEAGUE_ID_FIXTURES + URL_API + URL_INCLUDES_FIXTURES + "&tz=" + myTimezone;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, full_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONObject mainObject = response.getJSONObject("data");
                    JSONObject seasonObject = mainObject.getJSONObject("season");
                    JSONObject dataSeason = seasonObject.getJSONObject("data");
                    JSONObject fixturesObject = dataSeason.getJSONObject("upcoming");
                    JSONArray fixturesData = fixturesObject.getJSONArray("data");

                    for (int i = 0; i < fixturesData.length(); i++) {

                        JSONObject objectMain = fixturesData.getJSONObject(i);
                        idFixtures_fixtures = objectMain.getString("id");

                        JSONObject locTeam = objectMain.getJSONObject("localTeam");
                        JSONObject visTeam = objectMain.getJSONObject("visitorTeam");
                        JSONObject localTeamObj = locTeam.getJSONObject("data");
                        JSONObject visitorTeamObj = visTeam.getJSONObject("data");

                        homeTeamName_fixtures = localTeamObj.getString("name");
                        homeTeamLogo_fixtures = localTeamObj.getString("logo_path");
                        localTeamId_fixtures = localTeamObj.getInt("id");
                        awayTeamName_fixtures = visitorTeamObj.getString("name");
                        awayTeamLogo_fixtures = visitorTeamObj.getString("logo_path");
                        visitorTeamId_fixtures = visitorTeamObj.getInt("id");

                        JSONObject timeMain = objectMain.getJSONObject("time");
                        JSONObject starting_at = timeMain.getJSONObject("starting_at");

                        mstartTime_fixtures = starting_at.getString("time");
                        datum_fixtures = starting_at.getString("date");
                        statusS_fixtures = timeMain.getString("status");


                        if (datum_fixtures.equals(prevDate_fixtures)) {

                            currentDate_fixtures = "isti datum";
                        } else {
                            currentDate_fixtures = starting_at.getString("date");
                        }


                        JSONObject scores = objectMain.getJSONObject("scores");
                        String localScore = scores.getString("localteam_score");
                        String visitScore = scores.getString("visitorteam_score");

                        ftScore_fixtures = localScore + " - " + visitScore;

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            date_fixtures = format.parse(datum_fixtures);
                            System.out.println(date_fixtures);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        android.text.format.DateFormat df = new android.text.format.DateFormat();
                        df.format("dd-MMM-yyyy", date_fixtures);

                        FixturesLeagueModel model = new FixturesLeagueModel(homeTeamName_fixtures, homeTeamLogo_fixtures, awayTeamName_fixtures, awayTeamLogo_fixtures, mstartTime_fixtures, date_fixtures, currentDate_fixtures, statusS_fixtures, ftScore_fixtures, idFixtures_fixtures, localTeamId_fixtures, visitorTeamId_fixtures);
                        modelArrayList_fixtures.add(model);

                        adapter_fixtures.notifyDataSetChanged();
                        prevDate_fixtures = datum_fixtures;

                    }
                   /* Collections.sort(modelArrayList, new Comparator<FixturesLeagueModel>() {

                        @Override
                        public int compare(FixturesLeagueModel l, FixturesLeagueModel r) {
                            return l.getDate().compareTo(r.getDate());
                        }

                    }); */

                    mDialog.dismiss();
                } catch (JSONException e) {
                    Log.e("responseErrorLeagFix", e.getLocalizedMessage());
                    mDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("errorFixturesLeague", error.getLocalizedMessage());
                mDialog.dismiss();
            }
        });
        Volley.newRequestQueue(LeagueInfoActivity.this).add(request);
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
                            } else if (model.getCountryName().equals("Europe")) {
                                flag.setImageDrawable(LeagueInfoActivity.this.getResources().getDrawable(R.drawable.europe));

                            } else {


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

                            if (model.getResult().contains("Promotion - ") && !model.getResult().contains("(Qualification)") && !model.getResult().contains("(Play Offs)") && !model.getResult().equals("Promotion - Europa League (Group Stage)") && !model.getResult().equals("Promotion - Europa League (Qualification)")) {
                                parentChampionsleague.setVisibility(View.VISIBLE);
                                championsLeagueTextview.setText(leagueName + " - " + " " + model.getResult());

                            }
                            if (model.getResult().contains("Promotion - Champions League (Qualification)") || model.getResult().contains("(Play Offs)")) {
                                parentChampionsLEagueQualif.setVisibility(View.VISIBLE);
                                championsLeagueQualifTextview.setText(leagueName + " - " + " " + model.getResult());

                            }

                            if (model.getResult().contains("Promotion - Europa League (Group Stage)")) {
                                parentEuropeLeague.setVisibility(View.VISIBLE);
                                europeLeagueTextview.setText(leagueName + " Europa League (Group Stage)");
                            }
                            if (model.getResult().contains("Promotion - Europa League (Qualification)")) {
                                parentEuroeLeagueQualif.setVisibility(View.VISIBLE);
                                europeLeagueQualifTextview.setText(leagueName + " Europa League (Qualification)");
                            }
                            if (model.getResult().contains("Relegation - ")) {
                                parentRelegation.setVisibility(View.VISIBLE);
                                relegationTextview.setText(leagueName + " " + model.getResult());
                            }
                            if (model.getResult().contains(" (Relegation)")) {
                                parentRelegationDoig.setVisibility(View.VISIBLE);
                                relegationQualifTextview.setText(leagueName + " " + model.getResult());
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
