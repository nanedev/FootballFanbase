package com.malikbisic.sportapp.activity.api;

import android.content.Intent;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.malikbisic.sportapp.fragment.api.FixturesClubFragment;
import com.malikbisic.sportapp.fragment.api.FragmentClubInfo;
import com.malikbisic.sportapp.fragment.api.FragmentSquad;
import com.malikbisic.sportapp.activity.firebase.MainPage;
import com.malikbisic.sportapp.adapter.api.SectionPageAdapter;
import com.malikbisic.sportapp.model.api.SvgDrawableTranscoder;
import com.malikbisic.sportapp.utils.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class AboutFootballClub extends AppCompatActivity  {
    Toolbar toolbar;
    Intent intent;
   public static String clubLogo;
  public static  String clubName;
    String countryId;
    CircleImageView logo_image_club;
    TextView club_name_textview;
    CircleImageView flagImageView;
    String teamId;
    Bundle bundle;
    private ViewPager mViewPager;
    private SectionPageAdapter sectionPageAdapter;

    private final String countryUrl = "https://soccer.sportmonks.com/api/v2.0/countries/";
    private final String apiKey = Constants.API_KEY;

    String nameCounry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_football_club);
        Intent closeAPP = new Intent(this, StopAppServices.class);
        startService(closeAPP);
        sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.aboutFootballViewPager);
        setUpViewPager(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsAboutFootball);
        tabLayout.setupWithViewPager(mViewPager);

        toolbar = (Toolbar) findViewById(R.id.toolbarAboutFootball);
        logo_image_club = (CircleImageView) findViewById(R.id.clu_logo_about_football);
        club_name_textview = (TextView) findViewById(R.id.club_name_about_football);
        flagImageView = (CircleImageView) findViewById(R.id.flag_about_football);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("");
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

                    nameCounry = getData.getString("name");

                    if (nameCounry.equals("England")){
                        flagImageView.setImageDrawable(getResources().getDrawable(R.drawable.england));
                    } else if (nameCounry.equals("Northern Ireland")){
                        flagImageView.setImageDrawable(getResources().getDrawable(R.drawable.northern_ireland));
                    } else if (nameCounry.equals("Scotland")){
                        flagImageView.setImageDrawable(getResources().getDrawable(R.drawable.scotland));
                    } else if (nameCounry.equals("Wales")){
                        flagImageView.setImageDrawable(getResources().getDrawable(R.drawable.welsh_flag));
                    }

                    System.out.println("country name" + nameCounry);
                    String countryURL = "https://restcountries.eu/rest/v2/name/" + nameCounry;

                    JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, countryURL, null, new Response.Listener<JSONArray>() {
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
                                            .with(getApplicationContext())
                                            .using(Glide.buildStreamModelLoader(Uri.class, getApplicationContext()), InputStream.class)
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
                                            .into(flagImageView);

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
                    Volley.newRequestQueue(AboutFootballClub.this).add(request);


                } catch (JSONException e1) {
                    e1.printStackTrace();
                }



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
        adapter.addFragment(new FixturesClubFragment(), "Fixtures");
        viewPager.setAdapter(adapter);

    }


}
