package com.malikbisic.sportapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.malikbisic.sportapp.R;

public class SingleFixtureActivity extends AppCompatActivity {
    TextView countryNameTextview;
    TextView leagueNameTextview;
Intent intent;
String getCountryName;
String getLeagueName;
String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fixture);
        countryNameTextview = (TextView) findViewById(R.id.imeDrzave);
        leagueNameTextview = (TextView) findViewById(R.id.nazivLige);
        intent = getIntent();
        getCountryName = intent.getStringExtra("countryName");
        getLeagueName = intent.getStringExtra("leagueName");

      date = intent.getStringExtra("date");
        countryNameTextview.setText(getCountryName);
        leagueNameTextview.setText(getLeagueName);



        String url = " https://soccer.sportmonks.com/api/v2.0/fixtures/"+date+"?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s";


    }
}
