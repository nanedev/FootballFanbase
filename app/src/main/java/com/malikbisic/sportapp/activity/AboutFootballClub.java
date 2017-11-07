package com.malikbisic.sportapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.Volley;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;


import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGExternalFileResolver;
import com.caverock.androidsvg.SVGParser;
import com.google.gson.JsonObject;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.classes.SvgDecoder;
import com.malikbisic.sportapp.model.SvgDrawableTranscoder;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.SOURCE;

public class AboutFootballClub extends AppCompatActivity  {
    Toolbar toolbar;
    Intent intent;
   static String clubLogo;
  static  String clubName;
    String countryId;
    CircleImageView logo_image_club;
    TextView club_name_textview;
    ImageView flagImageView;
    String teamId;
    Bundle bundle;
    private ViewPager mViewPager;
    private SectionPageAdapter sectionPageAdapter;

    private final String countryUrl = "https://soccer.sportmonks.com/api/v2.0/countries/";
    private final String apiKey = "?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_football_club);
        sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.aboutFootballViewPager);
        setUpViewPager(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsAboutFootball);
        tabLayout.setupWithViewPager(mViewPager);

        toolbar = (Toolbar) findViewById(R.id.toolbarAboutFootball);
        logo_image_club = (CircleImageView) findViewById(R.id.clu_logo_about_football);
        club_name_textview = (TextView) findViewById(R.id.club_name_about_football);
        flagImageView = (ImageView) findViewById(R.id.flag_about_football);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        intent = getIntent();
        clubLogo = intent.getStringExtra("teamLogo");
        clubName = intent.getStringExtra("teamName");
        countryId = intent.getStringExtra("countryId");
        teamId = intent.getStringExtra("teamId");

        Bundle bundle = new Bundle();
        if (teamId != null)
        bundle.putString("my_key",String.valueOf(teamId));
        FragmentSquad myFrag = new FragmentSquad();
        myFrag.setArguments(bundle);

        Picasso.with(this).load(clubLogo).into(logo_image_club);
        club_name_textview.setText(clubName);

        String finalUrl = countryUrl + countryId + apiKey;
        JsonObjectRequest countryReq = new JsonObjectRequest(Request.Method.GET, finalUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String flag = "";
                try {
                    JSONObject getData = response.getJSONObject("data");
                    JSONObject getExtra = getData.getJSONObject("extra");
                    flag = getExtra.getString("flag");

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;

                requestBuilder = Glide
                        .with(AboutFootballClub.this)
                        .using(Glide.buildStreamModelLoader(Uri.class, AboutFootballClub.this), InputStream.class)
                        .from(Uri.class)
                        .as(SVG.class)
                        .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                        .sourceEncoder(new StreamEncoder())
                        .cacheDecoder(new FileToStreamDecoder<SVG>(new SvgDecoder()))
                        .decoder(new SvgDecoder())
                        .animate(android.R.anim.fade_in);


                Uri uri = Uri.parse("http://www.w3.org/2000/svg");
                requestBuilder
                        // SVG cannot be serialized so it's not worth to cache it
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .load(uri)
                        .into(flagImageView);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(this).add(countryReq);


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AboutFootballClub.this, MainPage.class);
        startActivity(intent);
        super.onBackPressed();

    }

    private void setUpViewPager(ViewPager viewPager) {
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentSquad(), "Squad");
        adapter.addFragment(new FragmentClubInfo(), "Neki fragment info");
        viewPager.setAdapter(adapter);

    }


}
