package com.malikbisic.sportapp.activity.api;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.StopAppServices;
import com.malikbisic.sportapp.fragment.api.FragmentAllFixtures;
import com.malikbisic.sportapp.fragment.api.FragmentAllMatches;
import com.malikbisic.sportapp.fragment.api.FragmentMyClubMatches;
import com.malikbisic.sportapp.adapter.api.SectionPageAdapter;

public class FootballActivity extends AppCompatActivity {
    private ViewPager mViewPager;
 SectionPageAdapter sectionPageAdapter;

    ProgressDialog progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_football);
        Intent closeAPP = new Intent(this, StopAppServices.class);
        startService(closeAPP);

        sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.containerFootbalViewPager);
        setUpViewPager(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(2);



        }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void setUpViewPager(ViewPager viewPager){
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentAllMatches(),"League");
        adapter.addFragment(new FragmentMyClubMatches(),"Live");
        adapter.addFragment(new FragmentAllFixtures(), "Fixtures");
        viewPager.setAdapter(adapter);

    }
    }

