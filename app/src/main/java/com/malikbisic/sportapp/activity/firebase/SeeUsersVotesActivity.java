package com.malikbisic.sportapp.activity.firebase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.StopAppServices;

public class SeeUsersVotesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_users_votes);
        Intent closeAPP = new Intent(this, StopAppServices.class);
        startService(closeAPP);
    }
}
