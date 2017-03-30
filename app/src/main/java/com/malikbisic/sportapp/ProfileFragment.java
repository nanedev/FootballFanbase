package com.malikbisic.sportapp;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    ImageView profile;
    TextView username;
    TextView gender;
    TextView birthday;
    private String googleUser_id;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        googleUser_id = LoginActivity.gUserId;

        mDatabase = FirebaseDatabase.getInstance();

        mReference = mDatabase.getReference().child("Users").child(googleUser_id);

        profile = (ImageView) view.findViewById(R.id.get_profile_image_id);
        username = (TextView) view.findViewById(R.id.username_id);
        gender = (TextView) view.findViewById(R.id.gender_id);
        birthday = (TextView) view.findViewById(R.id.birthday_id);

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();

                try {
                    String profielImage = String.valueOf(value.get("profileImage"));
                    URL url = new URL(profielImage);
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    profile.setImageBitmap(bmp);
                    username.setText(String.valueOf(value.get("username")));
                    gender.setText(String.valueOf(value.get("gender")));
                    birthday.setText(String.valueOf(value.get("date")));
                }catch (Exception ex){
                    Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

}
