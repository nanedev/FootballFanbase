package com.malikbisic.sportapp.fragment.api;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.api.SectionPageAdapter;
import com.malikbisic.sportapp.classes.CustomViewPager;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLineup extends Fragment {



    private CustomViewPager mViewPager;
    private SectionPageAdapter sectionPageAdapter;

    ViewPager pager;


    String homeTeam;
    String awayTeam;
    public FragmentLineup() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fragment_lineup, container, false);

        sectionPageAdapter = new SectionPageAdapter(getActivity().getSupportFragmentManager());
        mViewPager = (CustomViewPager) v.findViewById(R.id.lineUp);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tabsLineUp);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(mViewPager.getCurrentItem());
        mViewPager.setPagingEnabled(false);
        mViewPager.setOffscreenPageLimit(1);

        homeTeam = getActivity().getIntent().getStringExtra("localTeamName");
        awayTeam = getActivity().getIntent().getStringExtra("visitorTeamName");
        Log.i("Teamhome", homeTeam);
        Log.i("TeamAway", awayTeam);
        setUpViewPager(mViewPager, homeTeam, awayTeam);


        return v;
    }


    private void setUpViewPager(ViewPager viewPager, String homeTeam, String awayTeam) {
        SectionPageAdapter adapter = new SectionPageAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new HomeTeamLineUp(), ""+homeTeam);
        adapter.addFragment(new AwayTeamLineUp(), ""+awayTeam);
        viewPager.setAdapter(adapter);



    }

}
