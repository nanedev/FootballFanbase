package com.malikbisic.sportapp.activity.firebase;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.api.SectionPageAdapter;
import com.malikbisic.sportapp.fragment.api.FragmentAllFixtures;
import com.malikbisic.sportapp.fragment.api.FragmentAllMatches;
import com.malikbisic.sportapp.fragment.api.FragmentMyClubMatches;
import com.malikbisic.sportapp.fragment.firebase.RankingAllTimeFragment;
import com.malikbisic.sportapp.fragment.firebase.RankingMonthFragment;

public class RankingsActivity extends AppCompatActivity {

    SectionPageAdapter adapter;
    ViewPager viewPager;

    Toolbar rankingsToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rankings);
        rankingsToolbar = (Toolbar) findViewById(R.id.rankingsToolbar);

        setSupportActionBar(rankingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        adapter = new SectionPageAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.rankingsViewPager);
        setUpViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsRankings);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);



    }


    private void setUpViewPager(ViewPager viewPager){
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new RankingMonthFragment(),"This month ranks");
        adapter.addFragment(new RankingAllTimeFragment(),"All time ranks");

        viewPager.setAdapter(adapter);

    }
}
