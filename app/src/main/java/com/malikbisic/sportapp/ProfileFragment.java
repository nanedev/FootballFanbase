package com.malikbisic.sportapp;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private ImageView profile;
    private TextView username;
    private TextView gender;
    private TextView birthday;
    private TextView country;
    private EditText club;
    private EditText player;

    private String uid;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mDatabase = FirebaseDatabase.getInstance();
        uid = MainPage.uid;
        mReference = mDatabase.getReference().child("Users").child(uid);

        profile = (ImageView) view.findViewById(R.id.get_profile_image_id);
        username = (TextView) view.findViewById(R.id.username_id);
        gender = (TextView) view.findViewById(R.id.gender_id);
        birthday = (TextView) view.findViewById(R.id.birthday_id);
        country = (TextView) view.findViewById(R.id.countryId);
        club = (EditText) view.findViewById(R.id.footballClubId);
        player = (EditText) view.findViewById(R.id.footballPlayerId);


        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();


                String profielImage = String.valueOf(value.get("profileImage"));
                Picasso.with(getActivity())
                        .load(profielImage)
                        .into(profile);
                username.setText(String.valueOf(value.get("username")));
                gender.setText(String.valueOf(value.get("gender")));
                birthday.setText(String.valueOf(value.get("date")));

                //String flag = String.valueOf(value.get("flag"));
               // country.setText(String.valueOf(value.get("Country")));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }
}


