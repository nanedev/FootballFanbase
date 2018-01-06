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
import com.malikbisic.sportapp.fragment.api.PlayerStatsFragment;
import com.malikbisic.sportapp.adapter.api.SectionPageAdapter;
import com.malikbisic.sportapp.fragment.api.InfoPlayerFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class PlayerInfoActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private SectionPageAdapter sectionPageAdapter;
    Toolbar toolbar;
    Intent myIntentm;
    ImageView player_image;
    TextView player_name;
    NestedScrollView nestedScrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_info);

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
        myIntentm = getIntent();
        String playerName = myIntentm.getStringExtra("playerName");
        String playerImage = myIntentm.getStringExtra("playerImage");
        int playerID = myIntentm.getIntExtra("playerID", 0);
        boolean fromMatch = myIntentm.getBooleanExtra("openMatchInfo", false);


        Picasso.with(this).load(playerImage).into(player_image);
        player_name.setText(playerName);

        if (fromMatch){

            String url = "https://soccer.sportmonks.com/api/v2.0/players/"+playerID+"?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s&include=stats";

            JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        JSONObject mainObject = response.getJSONObject("data");

                        String playerName = mainObject.getString("fullname");
                        String playerImage = mainObject.getString("image_path");

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
        adapter.addFragment(new PlayerStatsFragment(), "Stats");
        viewPager.setAdapter(adapter);

    }
}
