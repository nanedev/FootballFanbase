package com.malikbisic.sportapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.malikbisic.sportapp.R;

public class NewsActivity extends AppCompatActivity {

private static final String TAG = "News Activity";

    private ViewPager mViewPager;
    private SectionPageAdapter sectionPageAdapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent openMainPage = new Intent(NewsActivity.this, MainPage.class);
                    startActivity(openMainPage);
                    break;
                case R.id.navigation_dashboard:
                case R.id.navigation_notifications:

                    return true;
            }
            return false;
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.containerViewPager);
        setUpViewPager(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
    }

    private void setUpViewPager(ViewPager viewPager){
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentAllNews(),"All news");
        adapter.addFragment(new FragmentMyClubNews(),"My club");
        viewPager.setAdapter(adapter);

    }

}
