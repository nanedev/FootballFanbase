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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_username);
        enterUsername = (EditText) findViewById(R.id.input_username);
        contunue = (Button) findViewById(R.id.continue_to_profil);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        contunue.setOnClickListener(this);





    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.continue_to_profil) {
            mReference = mDatabase.getReference("Users");
            Query query = mReference.orderByChild("nick").equalTo(enterUsername.getText().toString());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    for (DataSnapshot nickNames : dataSnapshot.getChildren()) {

                        value = (String) nickNames.child("nick").getValue();


                    }

                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            if (TextUtils.isEmpty(enterUsername.getText().toString())) {
                enterUsername.setError("field can not be blank");
                valid = false;
            } else if (enterUsername.getText().toString().equals(value)) {
                Toast.makeText(EnterUsernameForApp.this, "Username already exists,can not continue!", Toast.LENGTH_LONG).show();
                valid = false;
            } else {
                enterUsername.setError(null);
                //Intent intent = new Intent(EnterUsernameForApp.this, ProfilActivity.class);
                //startActivity(intent);
            }
        }

    }


}
