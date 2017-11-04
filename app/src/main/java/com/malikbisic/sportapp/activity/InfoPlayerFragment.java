package com.malikbisic.sportapp.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malikbisic.sportapp.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoPlayerFragment extends Fragment {
CircleImageView club_logo_imageview;
TextView club_name_textview;
TextView player_age_textview;
TextView player_birth_textview;
TextView country_name_textview;
CircleImageView country_player_imageview;
TextView position_player_textview;
TextView shirt_number_textview;
TextView player_height_textview;
TextView player_weight_textview;
Intent getIntent;


    public InfoPlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info_player,container,false);
        // Inflate the layout for this fragment

        club_logo_imageview = (CircleImageView) v.findViewById(R.id.logo_club_for_player_info);
        club_name_textview = (TextView) v.findViewById(R.id.club_name_for_player_info);
        player_age_textview = (TextView) v.findViewById(R.id.player_info_age);
        player_birth_textview = (TextView) v.findViewById(R.id.birthdateplayerinfo);
        country_player_imageview = (CircleImageView) v.findViewById(R.id.country_flag_player_info);
        country_name_textview = (TextView) v.findViewById(R.id.country_name_player_info);
        position_player_textview = (TextView) v.findViewById(R.id.player_info_position);
        Picasso.with(getActivity()).load(AboutFootballClub.clubLogo).into(club_logo_imageview);
        club_name_textview.setText(AboutFootballClub.clubName);
        shirt_number_textview = (TextView) v.findViewById(R.id.player_shirt_number);
        player_height_textview = (TextView) v.findViewById(R.id.player_height);
        player_weight_textview = (TextView) v.findViewById(R.id.player_weight);
getIntent = getActivity().getIntent();
String playerDateOfBirth = getIntent.getStringExtra("playerBirthDate");
String playerBirthPlace = getIntent.getStringExtra("playerBirthPlace");
String playerPosition = getIntent.getStringExtra("playerPosition");
String playerHeight = getIntent.getStringExtra("playerHeight");
String playerWeight = getIntent.getStringExtra("playerWeight");
String shirtNumber = getIntent.getStringExtra("shirtNumber");

player_birth_textview.setText(playerDateOfBirth);
position_player_textview.setText(playerPosition);
shirt_number_textview.setText(shirtNumber);
player_height_textview.setText(playerHeight);
player_weight_textview.setText(playerWeight);


        return v;
    }

}
