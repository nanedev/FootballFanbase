package com.malikbisic.sportapp.activity.firebase;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.StopAppServices;
import com.malikbisic.sportapp.adapter.api.SectionPageAdapter;
import com.malikbisic.sportapp.fragment.api.FragmentAllFixtures;
import com.malikbisic.sportapp.fragment.api.FragmentAllMatches;
import com.malikbisic.sportapp.fragment.api.FragmentMyClubMatches;
import com.malikbisic.sportapp.fragment.firebase.ProfileFragment;
import com.malikbisic.sportapp.fragment.firebase.RankingAllTimeFragment;
import com.malikbisic.sportapp.fragment.firebase.RankingMonthFragment;

public class RankingsActivity extends AppCompatActivity {

    SectionPageAdapter adapter;
    ViewPager viewPager;

    Toolbar rankingsToolbar;

    Intent myIntent;
    boolean fromUsersProfile;
    int clubPosition;
    String backActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rankings);
        rankingsToolbar = (Toolbar) findViewById(R.id.rankingsToolbar);
        Intent closeAPP = new Intent(this, StopAppServices.class);
        startService(closeAPP);

        setSupportActionBar(rankingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        adapter = new SectionPageAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.rankingsViewPager);
        setUpViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsRankings);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);
        myIntent = getIntent();
        fromUsersProfile = myIntent.getBooleanExtra("profileUsers", false);
        clubPosition = myIntent.getIntExtra("clubPosition", 0);
        backActivity = myIntent.getStringExtra("openActivityToBack");

        if (fromUsersProfile){
            viewPager.setCurrentItem(2);
        }

    }


    private void setUpViewPager(ViewPager viewPager){
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new RankingMonthFragment(),"This month ranks");
        adapter.addFragment(new RankingAllTimeFragment(),"All time ranks");

        viewPager.setAdapter(adapter);

    }


    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        if (backActivity.equals("mainPage")){
            Intent backToMain = new Intent(RankingsActivity.this, MainPage.class);
            startActivity(backToMain);
        } else if (backActivity.equals("profileFragment")){
            Intent backToProfile = new Intent(RankingsActivity.this, ProfileFragment.class);
            startActivity(backToProfile);
        } else if (backActivity.equals("usersActivity")){
            Intent backToUserActivity = new Intent(RankingsActivity.this, UserProfileActivity.class);
            startActivity(backToUserActivity);
        }
        return super.getParentActivityIntent();
    }
}
