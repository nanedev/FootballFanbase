package com.malikbisic.sportapp.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.LeagueAdapter;
import com.malikbisic.sportapp.adapter.SelectLeagueAdapter;
import com.malikbisic.sportapp.model.LeagueModel;

import java.util.ArrayList;

public class LeagueInfoActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private SectionPageAdapter sectionPageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_info);

        sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.containerLeagueInfoViewPager);
        setUpViewPager(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsLeaguInfo);
        tabLayout.setupWithViewPager(mViewPager);


    }
    private void setUpViewPager(ViewPager viewPager){
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentLeagueInfoStandings(),"Standings");
        adapter.addFragment(new FragmentLeagueInfoFixtures(),"Fixtures");
        viewPager.setAdapter(adapter);

    }
}
