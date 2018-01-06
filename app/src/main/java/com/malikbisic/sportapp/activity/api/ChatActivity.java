package com.malikbisic.sportapp.activity.api;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.fragment.firebase.FragmentChat;
import com.malikbisic.sportapp.fragment.api.FragmentChatUsers;
import com.malikbisic.sportapp.activity.firebase.MainPage;
import com.malikbisic.sportapp.adapter.api.SectionPageAdapter;

public class ChatActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private SectionPageAdapter sectionPageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.containerChatViewPager);
        setUpViewPager(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent backMainpAGE = new Intent(ChatActivity.this, MainPage.class);
        startActivity(backMainpAGE);
    }

    private void setUpViewPager(ViewPager viewPager){
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentChat(),"Chat");
        adapter.addFragment(new FragmentChatUsers(),"Users");
        viewPager.setAdapter(adapter);

    }



}
