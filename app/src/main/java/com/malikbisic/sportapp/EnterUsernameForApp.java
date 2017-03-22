package com.malikbisic.sportapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class EnterUsernameForApp extends AppCompatActivity implements View.OnClickListener {
    private EditText enterUsername;
    private Button contunue;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private String value;
    private boolean valid = true;
    private String username;

    private ArrayList<String> nickList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_username);
        enterUsername = (EditText) findViewById(R.id.input_username);
        nickList = new ArrayList<>();
        contunue = (Button) findViewById(R.id.continue_to_profil);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        contunue.setOnClickListener(this);


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Usernames");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    for (ParseObject object : objects){
                        nickList.add(String.valueOf(object.get("username")));
                    }
                }
            }
        });




    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.continue_to_profil) {

            if (valid()){
                Intent intent = new Intent(EnterUsernameForApp.this, ProfilActivity.class);
                startActivity(intent);
            }

        }

    }


    public boolean valid(){
        valid = true;

        String nick = enterUsername.getText().toString().trim();
        if (TextUtils.isEmpty(enterUsername.getText().toString())) {
            enterUsername.setError("field can not be blank");
            valid = false;
        } else if (nickList.contains(nick)) {
            Toast.makeText(EnterUsernameForApp.this, "Username already exists,can not continue!", Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            enterUsername.setError(null);

        }
    return valid;
    }


}
