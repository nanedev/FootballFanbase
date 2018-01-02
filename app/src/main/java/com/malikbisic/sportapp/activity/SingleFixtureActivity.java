package com.malikbisic.sportapp.activity;

import android.content.Intent;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.malikbisic.sportapp.adapter.AllFixturesAdapter;
import com.malikbisic.sportapp.adapter.SingleFixtureAdapter;
import com.malikbisic.sportapp.model.AllFixturesModel;
import com.malikbisic.sportapp.model.SvgDrawableTranscoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

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
    Toolbar toolbar;
    CircleImageView countryLogo;
int getLeagueID;
    TextView titleInToolbar;
    TextView dateInToolbar;
    String formatedDate;
    int minutes;
    int localTeamScore;
    int visitorTeamScore;
    int seasonID;
    String countryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fixture);
        countryNameTextview = (TextView) findViewById(R.id.imeDrzave);
        leagueNameTextview = (TextView) findViewById(R.id.nazivLige);
        countryLogo = (CircleImageView) findViewById(R.id.zastavaDrzave);
        intent = getIntent();
        getCountryName = intent.getStringExtra("countryName");
        getLeagueName = intent.getStringExtra("leagueName");
        dateUrl = intent.getStringExtra("datum");
        adapter = new SingleFixtureAdapter(singleFixturesList, this, this);
        singleRecView = (RecyclerView) findViewById(R.id.singleFixtureRecyclerview);
        singleRecView.setLayoutManager(new LinearLayoutManager(this));
        singleRecView.setAdapter(adapter);
        toolbar = (Toolbar) findViewById(R.id.singleFixtureToolbar);
        titleInToolbar = (TextView) findViewById(R.id.toolbar_titleSingleFixture);
        dateInToolbar = (TextView) findViewById(R.id.dateSingleFixtureToolbarTextview);


        leagueID = intent.getIntExtra("leagueID", 0);
        seasonID = intent.getIntExtra("seasonId" ,0);
        countryNameTextview.setText(getCountryName.toUpperCase() + ":");
        leagueNameTextview.setText(getLeagueName.toUpperCase());
        setSupportActionBar(toolbar);

        titleInToolbar.setText(getLeagueName);

SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());

        try {
            Date newDate = format.parse(dateUrl);

            format = new SimpleDateFormat("dd.MM.yyyy",Locale.getDefault());
            String date = format.format(newDate);
            dateInToolbar.setText(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Log.i("fixture", String.valueOf(leagueID));

        if (getCountryName.equals("England")) {
            countryLogo.setImageDrawable(getResources().getDrawable(R.drawable.england));
        } else if (getCountryName.equals("Northern Ireland")) {
            countryLogo.setImageDrawable(getResources().getDrawable(R.drawable.northern_ireland));
        } else if (getCountryName.equals("Scotland")) {
            countryLogo.setImageDrawable(getResources().getDrawable(R.drawable.scotland));
        } else if (getCountryName.equals("Wales")) {
            countryLogo.setImageDrawable(getResources().getDrawable(R.drawable.welsh_flag));
        }


        String countryURL = "https://restcountries.eu/rest/v2/name/" + getCountryName;

        JsonArrayRequest countryReq = new JsonArrayRequest(Request.Method.GET, countryURL, null, new Response.Listener<JSONArray>() {
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
                                .with(SingleFixtureActivity.this)
                                .using(Glide.buildStreamModelLoader(Uri.class, SingleFixtureActivity.this), InputStream.class)
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
                                .into(countryLogo);

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
        Volley.newRequestQueue(this).add(countryReq);



        String url = "https://soccer.sportmonks.com/api/v2.0/fixtures/date/"+dateUrl+"?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s&include=localTeam%2CvisitorTeam%2Cleague.country&leagues="+leagueID;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {


                    JSONArray dataArray = response.getJSONArray("data");

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject objectMain = dataArray.getJSONObject(i);
                            idFixtures = objectMain.getString("id");
    getLeagueID = objectMain.getInt("league_id");

    seasonID = objectMain.getInt("season_id");
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


                            if (!timeMain.isNull("minute")) {
                                minutes = timeMain.getInt("minute");
                            }

                            JSONObject leagueMain = objectMain.getJSONObject("league");
                            JSONObject dataLeague = leagueMain.getJSONObject("data");
                            JSONObject getCountry = dataLeague.getJSONObject("country");
                            JSONObject getCountryData = getCountry.getJSONObject("data");
                            countryName = getCountryData.getString("name");

                            leagueName = dataLeague.getString("name");


                            Log.i("vrijeme", String.valueOf(minutes));


                            JSONObject scores = objectMain.getJSONObject("scores");
                            String localScore = scores.getString("localteam_score");
                            String visitScore = scores.getString("visitorteam_score");

                            ftScore = localScore + " - " + visitScore;

                            AllFixturesModel model = new AllFixturesModel(homeTeamName, homeTeamLogo, awayTeamName, awayTeamLogo, mstartTime, leagueName, datum, statusS,ftScore, idFixtures, localTeamId, visitorTeamId,minutes,leagueID,seasonID,countryName);
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
