package com.malikbisic.sportapp.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;


import com.malikbisic.sportapp.R;

public class LeagueInfoActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private SectionPageAdapter sectionPageAdapter;
   Toolbar toolbar;
   Intent intent;
   String leagueName;
   TextView leaguNameTextview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_info);


        sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.containerLeagueInfoViewPager);
        setUpViewPager(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsLeaguInfo);
        tabLayout.setupWithViewPager(mViewPager);
        toolbar = (Toolbar) findViewById(R.id.toolbarLeagueInfo);
        setSupportActionBar(toolbar);
        leaguNameTextview = (TextView) findViewById(R.id.leaguenameinleagueinfotoolbar);
        intent = getIntent();
        leagueName = intent.getStringExtra("leagueName");
        leaguNameTextview.setText(leagueName);
        getSupportActionBar().setTitle("");
getSupportActionBar().setDisplayHomeAsUpEnabled(true);





    }
    private void setUpViewPager(ViewPager viewPager){
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentLeagueInfoStandings(),"Standings");
        adapter.addFragment(new FragmentLeagueInfoResults(),"Results");
        adapter.addFragment(new FragmentLeagueInfoFixtures(),"Fixtures");
        viewPager.setAdapter(adapter);

    }
}
