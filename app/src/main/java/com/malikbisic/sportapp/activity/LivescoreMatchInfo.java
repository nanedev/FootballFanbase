package com.malikbisic.sportapp.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.malikbisic.sportapp.R;
import com.squareup.picasso.Picasso;

public class LivescoreMatchInfo extends AppCompatActivity {
    private ViewPager mViewPager;
    private SectionPageAdapter sectionPageAdapter;

    TextView localTeam;
    TextView visitorTeam;
    TextView leagueName;
    TextView startTime;
    ImageView localTeamLogo;
    ImageView visitorTeamLogo;

    Intent myIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livescore_match_info);

        sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.aboutFootballViewPager);
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

        String homeTeam = myIntent.getStringExtra("localTeamName");
        String homeTeamLogo = myIntent.getStringExtra("localTeamLogo");
        String awayTeam = myIntent.getStringExtra("visitorTeamName");
        String awayTeamLogo = myIntent.getStringExtra("visitorTeamLogo");
        String league = myIntent.getStringExtra("leagueName");
        String startTimeS = myIntent.getStringExtra("startTime");

        localTeam.setText(homeTeam);
        visitorTeam.setText(awayTeam);
        leagueName.setText(league);
        startTime.setText(startTimeS.substring(0, 5));
        Picasso.with(this).load(homeTeamLogo).into(localTeamLogo);
        Picasso.with(this).load(awayTeamLogo).into(visitorTeamLogo);

    }

    private void setUpViewPager(ViewPager viewPager) {
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentLineup(), "Lineup");
        adapter.addFragment(new FragmentChatAboutMatch(), "Chat");
        adapter.addFragment(new FragmentStats(), "Stats");
        adapter.addFragment(new FragmentOdds(), "Odds");
        viewPager.setAdapter(adapter);

    }
}
