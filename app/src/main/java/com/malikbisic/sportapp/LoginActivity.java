package com.malikbisic.sportapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "SGhHUU1SO947OcEmg9x0rHutI";
    private static final String TWITTER_SECRET = "7dm7t0hRqgQzudePEiU9KLeovMfLfr4riKBHP64sTCnMH0WSVd";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    Button openRegisterActivityBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
//        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_login);

       openRegisterActivityBtn = (Button) findViewById(R.id.registerBtn);

        openRegisterActivityBtn.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.registerBtn){
            Intent openRegisterIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(openRegisterIntent);
        }
    }
}
