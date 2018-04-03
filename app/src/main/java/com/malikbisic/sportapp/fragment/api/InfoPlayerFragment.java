package com.malikbisic.sportapp.fragment.api;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.malikbisic.sportapp.R;

import com.malikbisic.sportapp.activity.StopAppServices;
import com.malikbisic.sportapp.classes.PlayerComments;

import com.malikbisic.sportapp.utils.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoPlayerFragment extends Fragment {


    TextView position_player_textview;
    TextView shirt_number_textview;
    TextView player_height_textview;
    TextView player_weight_textview;
    Intent getIntent;



    TextView playerClubNameHeader;
    TextView playerYeardHeader;
    ImageView playerClubLogoHeader;

    String URL_BASE = "https://soccer.sportmonks.com/api/v2.0/players/";
    String playerID;
    String URL_API = Constants.API_KEY;
    String URL_INCLUDES = "&include=transfers,trophies.seasons";
    String URL_TEAM = "https://soccer.sportmonks.com/api/v2.0/teams/";
    boolean fromMatch;
    TextView goalsTextview;
    TextView assistsTextview;
    TextView redCardsTextview;
    TextView yellowCardsTextview;
    TextView totalMatchesTextview;

    ImageButton sendCommentBtn;
    EditText textComment;
    RecyclerView commentsRecView;

    public InfoPlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info_player, container, false);
        // Inflate the layout for this fragment
        Intent closeAPP = new Intent(getContext(), StopAppServices.class);
        getActivity().startService(closeAPP);
        position_player_textview = (TextView) v.findViewById(R.id.player_info_position);

        shirt_number_textview = (TextView) v.findViewById(R.id.player_shirt_number);
        player_height_textview = (TextView) v.findViewById(R.id.player_height);
        player_weight_textview = (TextView) v.findViewById(R.id.player_weight);
        sendCommentBtn = (ImageButton) v.findViewById(R.id.sendCommentsPlayerBtn);
        textComment = (EditText) v.findViewById(R.id.edittextplayerinfo);


        goalsTextview = (TextView) v.findViewById(R.id.goalsNumber);
        assistsTextview = (TextView) v.findViewById(R.id.assistsNumber);
        redCardsTextview = (TextView) v.findViewById(R.id.redCardNumber);
        yellowCardsTextview = (TextView) v.findViewById(R.id.yellowCardNumber);
        totalMatchesTextview = (TextView) v.findViewById(R.id.matches_played);

        getIntent = getActivity().getIntent();
        playerID = String.valueOf(getIntent.getIntExtra("playerID", 0));
        fromMatch = getIntent.getBooleanExtra("openMatchInfo", false);
        playerClubNameHeader = (TextView) v.findViewById(R.id.player_info_name);
        playerYeardHeader = (TextView) v.findViewById(R.id.yearsPLayer);
        commentsRecView = (RecyclerView) v.findViewById(R.id.commentsPlayerRec);


        String playerId2 = getIntent.getStringExtra("playerId");
        PlayerComments playerComments = new PlayerComments();
        playerComments.sendComments(sendCommentBtn, playerId2, textComment);
        playerComments.getComments(commentsRecView, playerId2, getActivity());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        //    adapter = new TransfersAdapter(transfers,getActivity(),getContext());


        String goals = getIntent.getStringExtra("goals");
        String assists = getIntent.getStringExtra("assists");
        String totalMatches = getIntent.getStringExtra("appearances");
        String yellowCards = getIntent.getStringExtra("yellowCards");
        String redCards = getIntent.getStringExtra("redCards");

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
                        for (int i = 0; i < transferArray.length(); i++) {
                            JSONObject object = transferArray.getJSONObject(i);

                            String fromTeam = String.valueOf(object.getInt("from_team_id"));
                            String toTeam = String.valueOf(object.getInt("to_team_id"));
                            String seasonId = String.valueOf(object.getInt("season_id"));
                            String transferDate = object.getString("date");
                            if (!object.isNull("amount")) {
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
                            Log.d("tag", String.valueOf(fromTeam));
                            Log.d("tag", toTeam);


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


            position_player_textview.setText(playerPosition);
            shirt_number_textview.setText(shirtNumber);
            player_height_textview.setText(playerHeight);
            player_weight_textview.setText(playerWeight);

            goalsTextview.setText(goals);
            assistsTextview.setText(assists);
            yellowCardsTextview.setText(yellowCards);
            redCardsTextview.setText(redCards);
            totalMatchesTextview.setText(totalMatches);


            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy", Locale.getDefault());
            try {
                calendar.setTime(format.parse(playerDateOfBirth));
            } catch (ParseException e) {
                e.printStackTrace();
            }


        } else {

            playerInfoFromMatchInfo();
        }

        return v;
    }

    public void playerInfoFromMatchInfo() {
        String fullUrl = URL_BASE + playerID + URL_API + URL_INCLUDES;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, fullUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject getData = response.getJSONObject("data");

                    String birthday = getData.getString("birthdate");

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy", Locale.getDefault());
                    try {
                        calendar.setTime(format.parse(birthday));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String birth_place = getData.getString("birthplace");


                    String country = getData.getString("nationality");

                    String shirtNumber = getIntent.getStringExtra("numberShirt");
                    shirt_number_textview.setText(shirtNumber);

                    String height = getData.getString("height");
                    String weight = getData.getString("weight");

                    player_height_textview.setText(height);
                    player_weight_textview.setText(weight);


                    String urlPosition = URL_BASE + playerID + URL_API + "&include=position";

                    JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, urlPosition, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject mainObject = response.getJSONObject("data");
                                JSONObject posObject = mainObject.getJSONObject("position");
                                JSONObject namePos = posObject.getJSONObject("data");

                                String position = namePos.getString("name");
                                position_player_textview.setText(position);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    Volley.newRequestQueue(getActivity()).add(request1);


                    JSONObject getTransferObj = getData.getJSONObject("transfers");
                    JSONArray transferArray = getTransferObj.getJSONArray("data");
                    for (int i = 0; i < transferArray.length(); i++) {
                        JSONObject object = transferArray.getJSONObject(i);

                        String fromTeam = String.valueOf(object.getInt("from_team_id"));
                        String toTeam = String.valueOf(object.getInt("to_team_id"));
                        String seasonId = String.valueOf(object.getInt("season_id"));
                        String transferDate = object.getString("date");
                        if (!object.isNull("amount")) {
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
                        Log.d("tag", String.valueOf(fromTeam));
                        Log.d("tag", toTeam);


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
