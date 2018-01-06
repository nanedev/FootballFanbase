package com.malikbisic.sportapp.activity.api;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.api.SectionPageAdapter;
import com.malikbisic.sportapp.fragment.api.FragmentChatAboutMatch;
import com.malikbisic.sportapp.fragment.api.FragmentCommentsMatch;
import com.malikbisic.sportapp.fragment.api.FragmentLineup;
import com.malikbisic.sportapp.fragment.api.FragmentOdds;
import com.malikbisic.sportapp.fragment.api.FragmentStats;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class LivescoreMatchInfo extends AppCompatActivity {
    private ViewPager mViewPager;
    private SectionPageAdapter sectionPageAdapter;

    TextView localTeam;
    TextView visitorTeam;
    TextView leagueName;
    TextView startTime;
    ImageView localTeamLogo;
    ImageView visitorTeamLogo;
    TextView scoreText;
    String date;

    String fixturesID;
    int localTeamId;
    int visitorTeamId;

    Intent myIntent;
    Bundle bundle;

    Toolbar matchInfoToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livescore_match_info);

        sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.livescoreMatchViewPager);
        setUpViewPager(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsAboutFootball);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mViewPager.setCurrentItem(mViewPager.getCurrentItem());
        myIntent = getIntent();
        matchInfoToolbar = (Toolbar) findViewById(R.id.def_toolbar);
        setSupportActionBar(matchInfoToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mViewPager.setOffscreenPageLimit(4);

        localTeam = (TextView) findViewById(R.id.localTeamNameMatchInfo);
        localTeamLogo = (ImageView) findViewById(R.id.localTeamLogoMatchInfo);
        visitorTeam = (TextView) findViewById(R.id.visitorTeamNameMatchInfo);
        visitorTeamLogo = (ImageView) findViewById(R.id.visitorTeamLogoMatchInfo);
        leagueName = (TextView) findViewById(R.id.leagueNameMatchInfo);
        startTime = (TextView) findViewById(R.id.timeStartMatchInfo);
        scoreText = (TextView) findViewById(R.id.score);

        final String homeTeam = myIntent.getStringExtra("localTeamName");
        final String homeTeamLogo = myIntent.getStringExtra("localTeamLogo");
        final String awayTeam = myIntent.getStringExtra("visitorTeamName");
        final String awayTeamLogo = myIntent.getStringExtra("visitorTeamLogo");
        String league = myIntent.getStringExtra("leagueName");
        String startTimeS = myIntent.getStringExtra("startTime");
        String score = myIntent.getStringExtra("score");
        String status = myIntent.getStringExtra("status");
        fixturesID = myIntent.getStringExtra("idFixtures");
        date = myIntent.getStringExtra("dateMatch");
        localTeamId = myIntent.getIntExtra("localTeamId", 0);
        visitorTeamId = myIntent.getIntExtra("visitorTeamId", 0);
        bundle = new Bundle();
        bundle.putString("fixturesID", fixturesID);

        localTeam.setText(homeTeam);
        visitorTeam.setText(awayTeam);
        leagueName.setText(league);

        localTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aboutCLub = new Intent(LivescoreMatchInfo.this, AboutFootballClub.class);
                aboutCLub.putExtra("teamLogo", homeTeamLogo);
                aboutCLub.putExtra("teamName", homeTeam);
                aboutCLub.putExtra("teamId", String.valueOf(localTeamId));
                startActivity(aboutCLub);
            }
        });

        localTeamLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aboutCLub = new Intent(LivescoreMatchInfo.this, AboutFootballClub.class);
                aboutCLub.putExtra("teamLogo", homeTeamLogo);
                aboutCLub.putExtra("teamName", homeTeam);
                aboutCLub.putExtra("teamId", String.valueOf(localTeamId));
                startActivity(aboutCLub);
            }
        });

        visitorTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aboutCLub = new Intent(LivescoreMatchInfo.this, AboutFootballClub.class);
                aboutCLub.putExtra("teamLogo", awayTeamLogo);
                aboutCLub.putExtra("teamName", awayTeam);
                aboutCLub.putExtra("teamId", String.valueOf(visitorTeamId));
                startActivity(aboutCLub);
            }
        });
        visitorTeamLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aboutCLub = new Intent(LivescoreMatchInfo.this, AboutFootballClub.class);
                aboutCLub.putExtra("teamLogo", awayTeamLogo);
                aboutCLub.putExtra("teamName", awayTeam);
                aboutCLub.putExtra("teamId", String.valueOf(visitorTeamId));
                startActivity(aboutCLub);
            }
        });
        Glide.with(this).load(homeTeamLogo).into(localTeamLogo);
        Glide.with(this).load(awayTeamLogo).into(visitorTeamLogo);

        if (status.equals("LIVE") || status.equals("HT") || status.equals("FT")){
            scoreText.setText(score);
        } else if (status.equals("NS")){
            scoreText.setText("-:-");
        }
        try {
            String dateStr = startTimeS;
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date2 = df.parse(dateStr);
            df.setTimeZone(TimeZone.getDefault());
            String formattedDate = df.format(date2);

            String mytime = date;
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd");
            Date myDate = null;
            try {
                myDate = dateFormat.parse(mytime);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM");
            String finalDate = timeFormat.format(myDate);
            startTime.setText(finalDate + ", " + formattedDate);


        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private void setUpViewPager(ViewPager viewPager) {
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentLineup(), "Lineup");
        adapter.addFragment(new FragmentChatAboutMatch(), "Chat");
        adapter.addFragment(new FragmentStats(), "Stats");
        adapter.addFragment(new FragmentCommentsMatch(), "Commentary");

        FragmentOdds fragmentOdds = new FragmentOdds();

        fragmentOdds.setArguments(bundle);
        adapter.addFragment(fragmentOdds, "Odds");
        viewPager.setAdapter(adapter);

    }
}
