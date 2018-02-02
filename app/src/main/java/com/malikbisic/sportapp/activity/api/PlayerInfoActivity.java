package com.malikbisic.sportapp.activity.api;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.StopAppServices;
import com.malikbisic.sportapp.adapter.api.SectionPageAdapter;
import com.malikbisic.sportapp.fragment.api.InfoPlayerFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PlayerInfoActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private SectionPageAdapter sectionPageAdapter;
    Toolbar toolbar;
    Intent myIntentm;
    ImageView player_image;
    TextView player_name;
    NestedScrollView nestedScrollView;
    TextView playerAge;
    TextView playerCLubName;
    ImageView playerClubImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_info);
        Intent closeAPP = new Intent(this, StopAppServices.class);
        startService(closeAPP);

        sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.playerInfoViewPager);
        setUpViewPager(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsPlayerInfo);
        tabLayout.setupWithViewPager(mViewPager);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedscrollview);
        nestedScrollView.setFillViewport(true);

        toolbar = (Toolbar) findViewById(R.id.playerinfotoolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("SportApp");
        }

        player_image = (ImageView) findViewById(R.id.player_info_image);
        player_name = (TextView) findViewById(R.id.player_info_name);
        playerAge = (TextView)  findViewById(R.id.yearsPLayer);
        playerClubImage = (ImageView) findViewById(R.id.clubimageheader);
        playerCLubName = (TextView) findViewById(R.id.clubName);
        myIntentm = getIntent();
        String playerName = myIntentm.getStringExtra("playerName");
        String playerImage = myIntentm.getStringExtra("playerImage");
        String playerId = myIntentm.getStringExtra("playerId");
        boolean fromMatch = myIntentm.getBooleanExtra("openMatchInfo", false);
        String playerDateOfBirth = myIntentm.getStringExtra("playerBirthDate");

        Picasso.with(this).load(playerImage).into(player_image);
        player_name.setText(playerName);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy", Locale.getDefault());
        try {
            calendar.setTime(format.parse(playerDateOfBirth));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        playerAge.setText("Age: " + getAge(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) +"  " + "(" +playerDateOfBirth + ")");



        if (!fromMatch){

            String url = "https://soccer.sportmonks.com/api/v2.0/players/"+playerId+"?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s&include=team";

            JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        JSONObject mainObject = response.getJSONObject("data");
                        JSONObject teamObj = mainObject.getJSONObject("team");
                        JSONObject getDataTeam = teamObj.getJSONObject("data");


                        String playerName = mainObject.getString("fullname");
                        String playerImage = mainObject.getString("image_path");
                        String clubName = getDataTeam.getString("name");
                        String clubImage = getDataTeam.getString("logo_path");

                        Picasso.with(PlayerInfoActivity.this).load(clubImage).into(playerClubImage);
                        playerCLubName.setText(clubName);

                        player_name.setText(playerName);
                        Glide.with(PlayerInfoActivity.this).load(playerImage).into(player_image);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("errorRequest", error.getLocalizedMessage());
                }
            });
            Volley.newRequestQueue(this).add(request);

        }

    }

    private void setUpViewPager(ViewPager viewPager) {
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new InfoPlayerFragment(), "Info");

        viewPager.setAdapter(adapter);

    }
    private String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }
}
