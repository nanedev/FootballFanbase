package com.malikbisic.sportapp.fragment.api;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.api.TeamAdapter;
import com.malikbisic.sportapp.adapter.api.TeamAdapterDef;
import com.malikbisic.sportapp.adapter.api.TeamAdapterGK;
import com.malikbisic.sportapp.adapter.api.TeamAdapterMid;
import com.malikbisic.sportapp.model.api.TeamModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FragmentSquad extends Fragment {
    Intent intent;
    TextView positionNameTextview;
    RecyclerView recyclerView;
    RecyclerView recyclerViewMid;
    RecyclerView recyclerViewDef;
    RecyclerView recyclerViewGK;
    TextView coachNameTextview;
    ArrayList<TeamModel> teamModelArrayList = new ArrayList<>();
    TeamAdapter adapter;
    TeamAdapterMid adapterMid;
    TeamAdapterDef adapterDef;
    TeamAdapterGK adapterGK;
    ArrayList<TeamModel> midfielderPosArray;
    ArrayList<TeamModel> defenderPosArray;
    ArrayList<TeamModel> goalkeeperPosArray;
    String clubLogo;
    String clubName;

    private final String API_KEY = "?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s";
    private final String URL = "https://soccer.sportmonks.com/api/v2.0/teams/";
    private final String INCLUDES = "&include=squad.player.position,coach";
    String finalUrl;

    ProgressDialog mDialog;

    public FragmentSquad() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_squad, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.team_recyclerview);
        recyclerViewMid = (RecyclerView) view.findViewById(R.id.teamMid_recyclerview);
        recyclerViewDef = (RecyclerView) view.findViewById(R.id.teamDef_recyclerview);
        recyclerViewGK = (RecyclerView) view.findViewById(R.id.teamGK_recyclerview);
        mDialog = new ProgressDialog(getActivity());
        midfielderPosArray = new ArrayList<>();
        defenderPosArray = new ArrayList<>();
        goalkeeperPosArray = new ArrayList<>();
        coachNameTextview = (TextView) view.findViewById(R.id.coachname);
        intent = getActivity().getIntent();
        String teamIdfromAct = intent.getStringExtra("teamId");
        clubLogo = intent.getStringExtra("teamLogo");
        clubName = intent.getStringExtra("teamName");
        finalUrl = URL + teamIdfromAct + API_KEY + INCLUDES;

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(view.getContext());
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(view.getContext());
        LinearLayoutManager layoutManager4 = new LinearLayoutManager(view.getContext());

        recyclerView.setLayoutManager(layoutManager);
        recyclerViewMid.setLayoutManager(layoutManager2);
        recyclerViewDef.setLayoutManager(layoutManager3);
        recyclerViewGK.setLayoutManager(layoutManager4);
        adapter = new TeamAdapter(getActivity().getApplicationContext(), getActivity(), teamModelArrayList);
        adapterMid = new TeamAdapterMid(getActivity().getApplicationContext(), getActivity(), midfielderPosArray);
        adapterDef = new TeamAdapterDef(getActivity().getApplicationContext(), getActivity(), defenderPosArray);
        adapterGK = new TeamAdapterGK(getActivity().getApplicationContext(), getActivity(), goalkeeperPosArray);


        recyclerView.setAdapter(adapter);
        recyclerViewMid.setAdapter(adapterMid);
        recyclerViewDef.setAdapter(adapterDef);
        recyclerViewGK.setAdapter(adapterGK);

        mDialog.setMessage("Loading...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.setIndeterminate(true);
        mDialog.show();
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, finalUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int playerId = 0;
                int positionId = 0;
                int numberId = 0;
                int countryId = 0;
                String commonName = "";
                String fullName = "";
                String firstName = "";
                String lastName = "";
                String nationality = "";
                String birthDate = "";
                String birthPlace = "";
                String height = "";
                String weight = "";
                String playerImage = "";
                String coachName = "";
                String positionName = "";
                try {
                    JSONObject getData = response.getJSONObject("data");


                    JSONObject getSquadObj = getData.getJSONObject("squad");
                    JSONObject getCoachObj = getData.getJSONObject("coach");
                    JSONObject getCoachData = getCoachObj.getJSONObject("data");
                    coachName = getCoachData.getString("fullname");
                    JSONArray getDataArray = getSquadObj.getJSONArray("data");

                    for (int i = 0; i < getDataArray.length(); i++) {
                        JSONObject object = getDataArray.getJSONObject(i);
                        int minutes = object.getInt("minutes");
                        int appearances = object.getInt("appearences");
                        int lineups = object.getInt("lineups");
                        int goals = object.getInt("goals");
                        int assists = object.getInt("assists");
                        int yellowCards = object.getInt("yellowcards");
                        int redCards = object.getInt("redcards");
                        boolean injured = object.getBoolean("injured");
                        int substituteIn = object.getInt("substitute_in");


                        JSONObject getPlayerData = object.getJSONObject("player");
                        JSONObject getDataFromPlayer = getPlayerData.getJSONObject("data");
                        JSONObject getPositionOfPlayer = getDataFromPlayer.getJSONObject("position");
                        JSONObject getPositionName = getPositionOfPlayer.getJSONObject("data");
                        positionName = getPositionName.getString("name");

                        playerId = object.getInt("player_id");
                        positionId = object.getInt("position_id");

                        numberId = object.getInt("number");
                        countryId = getDataFromPlayer.getInt("country_id");

                        commonName = getDataFromPlayer.getString("common_name");
                        fullName = getDataFromPlayer.getString("fullname");
                        firstName = getDataFromPlayer.getString("firstname");
                        lastName = getDataFromPlayer.getString("lastname");
                        nationality = getDataFromPlayer.getString("nationality");
                        birthDate = getDataFromPlayer.getString("birthdate");
                        birthPlace = getDataFromPlayer.getString("birthplace");
                        height = getDataFromPlayer.getString("height");
                        weight = getDataFromPlayer.getString("weight");
                        playerImage = getDataFromPlayer.getString("image_path");
                        TeamModel model = new TeamModel(playerId, positionId, numberId, countryId, commonName, fullName, firstName, lastName, nationality, birthDate, birthPlace, height, weight, playerImage, positionName, minutes, goals, appearances, assists, lineups, yellowCards, redCards, injured, substituteIn, coachName);

                        if (positionName.equals("Attacker")) {
                            teamModelArrayList.add(model);
                        } else if (positionName.equals("Midfielder")) {
                            midfielderPosArray.add(model);
                        } else if (positionName.equals("Defender")) {
                            defenderPosArray.add(model);
                        } else if (positionName.equals("Goalkeeper")) {
                            goalkeeperPosArray.add(model);
                        }


                        coachNameTextview.setText(coachName);


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    mDialog.dismiss();
                }
                mDialog.dismiss();

                adapter.notifyDataSetChanged();
                adapterMid.notifyDataSetChanged();
                adapterDef.notifyDataSetChanged();
                adapterGK.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mDialog.dismiss();
            }
        });
        Volley.newRequestQueue(getActivity().getApplicationContext()).add(objectRequest);

        return view;
    }


}