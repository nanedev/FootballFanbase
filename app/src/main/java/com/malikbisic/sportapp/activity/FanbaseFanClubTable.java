package com.malikbisic.sportapp.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.malikbisic.sportapp.R;

public class FanbaseFanClubTable extends AppCompatActivity {

    Toolbar likeToolbar;
    Intent myIntent;
    String openActivity = "";
    String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fanbase_fan_club_table);

        likeToolbar = (Toolbar) findViewById(R.id.clubTable_toolbar);
        setSupportActionBar(likeToolbar);
        getSupportActionBar().setTitle("Club Table");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myIntent = getIntent();
        openActivity = myIntent.getStringExtra("activityToBack");
        myUid = myIntent.getStringExtra("uid");
    }


    @Nullable
    @Override
    public Intent getParentActivityIntent() {


        if (openActivity.equals("myProfile")){

            Bundle bundle = new Bundle();
            bundle.putString("myUid", myUid);
            bundle.putBoolean("openFromFanBaseTable", true);

            ProfileFragment profileFragment = new ProfileFragment();
            profileFragment.setArguments(bundle);

            FragmentTransaction manager = getSupportFragmentManager().beginTransaction();

            manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                    R.anim.push_left_out, R.anim.push_left_out).replace(R.id.clubTable_layout, profileFragment, profileFragment.getTag()).addToBackStack(null).commit();
            Log.i("tacno", "true");

        } else if (openActivity.equals("commentsActivity")){
            Intent backComments = new Intent(FanbaseFanClubTable.this, CommentsActivity.class);
            //backComments.putExtra("keyComment", postKey);
            startActivity(backComments);
            finish();
        }

        return super.getParentActivityIntent();

    }
}
