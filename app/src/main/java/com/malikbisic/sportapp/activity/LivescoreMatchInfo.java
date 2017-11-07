package com.malikbisic.sportapp.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.malikbisic.sportapp.R;
import com.squareup.picasso.Picasso;

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

    String fixturesID;
    int localTeamId;
    int visitorTeamId;

    Intent myIntent;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livescore_match_info);

        sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.livescoreMatchViewPager);
        setUpViewPager(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsAboutFootball);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(mViewPager.getCurrentItem());
        myIntent = getIntent();

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
        Picasso.with(this).load(homeTeamLogo).into(localTeamLogo);
        Picasso.with(this).load(awayTeamLogo).into(visitorTeamLogo);

        if (status.equals("LIVE") || status.equals("HT") || status.equals("FT")){
            scoreText.setText(score);
        } else if (status.equals("NS")){
            scoreText.setText("-:-");
        }
        try {
            String dateStr = startTimeS;
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date2 = df.parse(dateStr);
            df.setTimeZone(TimeZone.getDefault());
            String formattedDate = df.format(date2);
            startTime.setText(formattedDate);


        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private void setUpViewPager(ViewPager viewPager) {
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentLineup(), "Lineup");
        adapter.addFragment(new FragmentChatAboutMatch(), "Chat");
        adapter.addFragment(new FragmentStats(), "Stats");

        FragmentOdds fragmentOdds = new FragmentOdds();

        fragmentOdds.setArguments(bundle);
        adapter.addFragment(fragmentOdds, "Odds");
        viewPager.setAdapter(adapter);

    }
}
