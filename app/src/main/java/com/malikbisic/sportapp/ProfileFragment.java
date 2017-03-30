package com.malikbisic.sportapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    ImageView profile;
    TextView username;
    TextView gender;
    TextView birthday;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profile = (ImageView) view.findViewById(R.id.get_profile_image_id);
        username = (TextView) view.findViewById(R.id.username_id);
        gender = (TextView) view.findViewById(R.id.gender_id);
        birthday = (TextView) view.findViewById(R.id.birthday_id);

        return view;
    }

}
