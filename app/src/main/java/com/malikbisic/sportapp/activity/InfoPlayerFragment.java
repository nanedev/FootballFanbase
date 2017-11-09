package com.malikbisic.sportapp.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.TransfersAdapter;
import com.malikbisic.sportapp.model.Transfers;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    TextView place_of_birth_textview;
    RecyclerView transferRecyclerview;
  //  ArrayList<Transfers> transfers;
    TransfersAdapter adapter;


    String URL_BASE = "https://soccer.sportmonks.com/api/v2.0/players/";
    String playerID;
    String URL_API = "?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s";
    String URL_INCLUDES="&include=transfers,trophies.seasons";
    String URL_TEAM = "https://soccer.sportmonks.com/api/v2.0/teams/";
    boolean fromMatch;


    public InfoPlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info_player, container, false);
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
        place_of_birth_textview = (TextView) v.findViewById(R.id.place_of_birth);
        getIntent = getActivity().getIntent();
        playerID = String.valueOf(getIntent.getIntExtra("playerID", 0));
        fromMatch = getIntent.getBooleanExtra("openMatchInfo", false);

        transferRecyclerview = (RecyclerView) v.findViewById(R.id.transfer_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    //    adapter = new TransfersAdapter(transfers,getActivity(),getContext());


        transferRecyclerview.setLayoutManager(linearLayoutManager);
        transferRecyclerview.setAdapter(adapter);

     //   transfers = new ArrayList<>();



        if (!fromMatch) {
            String playerDateOfBirth = getIntent.getStringExtra("playerBirthDate");
            String playerBirthPlace = getIntent.getStringExtra("playerBirthPlace");
            String playerPosition = getIntent.getStringExtra("playerPosition");
            String playerHeight = getIntent.getStringExtra("playerHeight");
            String playerWeight = getIntent.getStringExtra("playerWeight");
            String shirtNumber = getIntent.getStringExtra("shirtNumber");
            String countryName = getIntent.getStringExtra("nationality");
            String playerId = getIntent.getStringExtra("playerId");
            String finalUrl = URL_BASE + playerId + URL_API + URL_INCLUDES;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, finalUrl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        JSONObject getData = response.getJSONObject("data");
                        JSONObject getTransferObj = getData.getJSONObject("transfers");
                        JSONArray transferArray = getTransferObj.getJSONArray("data");
                        for (int i =0; i< transferArray.length();i++){
                            JSONObject object = transferArray.getJSONObject(i);

                          String fromTeam = String.valueOf(object.getInt("from_team_id"));
                            String toTeam = String.valueOf(object.getInt("to_team_id"));
                            String seasonId = String.valueOf(object.getInt("season_id"));
                            String transferDate = object.getString("date");
                          if (!object.isNull("amount")){
                             String amount = String.valueOf(object.getInt("amount"));
                          }
                          String fromTeamString = URL_TEAM + fromTeam + URL_API;
                          JsonObjectRequest teamRequest = new JsonObjectRequest(Request.Method.GET, fromTeamString, null, new Response.Listener<JSONObject>() {
                              @Override
                              public void onResponse(JSONObject response) {
                                  try {
                                      JSONObject getObj = response.getJSONObject("data");
                                      String name = getObj.getString("name");
                                  } catch (JSONException e) {
                                      e.printStackTrace();
                                  }
                              }
                          }, new Response.ErrorListener() {
                              @Override
                              public void onErrorResponse(VolleyError error) {

                              }
                          });
Volley.newRequestQueue(getActivity()).add(teamRequest);
                           Log.d("tag",String.valueOf(fromTeam));
                           Log.d("tag",toTeam);



                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            Volley.newRequestQueue(getActivity()).add(request);



            player_birth_textview.setText(playerDateOfBirth);
            position_player_textview.setText(playerPosition);
            shirt_number_textview.setText(shirtNumber);
            player_height_textview.setText(playerHeight);
            player_weight_textview.setText(playerWeight);
            country_name_textview.setText(countryName);


            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy", Locale.getDefault());
            try {
                calendar.setTime(format.parse(playerDateOfBirth));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            player_age_textview.setText(getAge(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
            place_of_birth_textview.setText(playerBirthPlace);




        }else {

            playerInfoFromMatchInfo();
        }

        return v;
    }

    public void playerInfoFromMatchInfo(){
        String fullUrl = URL_BASE + playerID + URL_API;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, fullUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error volley infoPlayer", error.getLocalizedMessage());
            }
        });

        Volley.newRequestQueue(getContext()).add(request);

    }

    private String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

}
