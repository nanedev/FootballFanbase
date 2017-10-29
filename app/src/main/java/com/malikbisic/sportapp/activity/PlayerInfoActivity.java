package com.malikbisic.sportapp.activity;

import android.content.Intent;
import android.media.Image;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.malikbisic.sportapp.R;
import com.squareup.picasso.Picasso;

public class PlayerInfoActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private SectionPageAdapter sectionPageAdapter;
    Toolbar toolbar;
    Intent myIntentm;
    ImageView player_image;
    TextView player_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_info);

        sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.playerInfoViewPager);
        setUpViewPager(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsPlayerInfo);
        tabLayout.setupWithViewPager(mViewPager);

        toolbar = (Toolbar) findViewById(R.id.playerinfotoolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("SportApp");
        }

        player_image = (ImageView) findViewById(R.id.player_info_image);
        player_name =(TextView) findViewById(R.id.player_info_name);
myIntentm = getIntent();
        String playerName = myIntentm.getStringExtra("playerName");
        String playerImage = myIntentm.getStringExtra("playerImage");
        Picasso.with(this).load(playerImage).into(player_image);
        player_name.setText(playerName);

    }

    private void setUpViewPager(ViewPager viewPager) {
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new InfoPlayerFragment(), "Info");
        adapter.addFragment(new PlayerStatsFragment(), "Stats");
        viewPager.setAdapter(adapter);

    }
}
