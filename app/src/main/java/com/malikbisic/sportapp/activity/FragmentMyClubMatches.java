package com.malikbisic.sportapp.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.LivescoreAdapter;
import com.malikbisic.sportapp.model.LivescoreModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.http.GET;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMyClubMatches extends Fragment {

    String URL_LIVESCORE = "https://soccer.sportmonks.com/api/v2.0/livescores";
    String URL_API = "?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s";
    String URL_INCLUDES = "&include=localTeam,visitorTeam,league";
    String url;

    LivescoreAdapter adapter;
    ArrayList<LivescoreModel> listScore;
    RecyclerView livescore_recview;

    public FragmentMyClubMatches() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_club_matches, container, false);
        url = URL_LIVESCORE + URL_API + URL_INCLUDES;
        listScore = new ArrayList<>();
        livescore_recview = (RecyclerView) view.findViewById(R.id.livescoreMatches);
        adapter = new LivescoreAdapter(listScore);
        livescore_recview.setLayoutManager(new LinearLayoutManager(getActivity()));
        livescore_recview.setAdapter(adapter);

        JsonObjectRequest livescoreRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String localTeamName;
                String localTeamLogo;
                String visitorTeamLogo;
                String visitorTeamName;
                String score;
                String status;
                String timeStart;

                try {
                    JSONArray dataArray = response.getJSONArray("data");

                    for (int i = 0; i < dataArray.length(); i++){
                        JSONObject objectArray = dataArray.getJSONObject(i);

                        JSONObject localTeamobject = objectArray.getJSONObject("localTeam");
                        JSONObject visitorTeamobject = objectArray.getJSONObject("visitorTeam");
                        JSONObject timeObject = objectArray.getJSONObject("time");
                        JSONObject scoreObejct = objectArray.getJSONObject("scores");

                        JSONObject locTeam = localTeamobject.getJSONObject("data");
                        JSONObject visTeam = visitorTeamobject.getJSONObject("data");

                        localTeamName = locTeam.getString("name");
                        localTeamLogo = locTeam.getString("logo_path");
                        visitorTeamName = visTeam.getString("name");
                        visitorTeamLogo = visTeam.getString("logo_path");


                        String localTeamScore = String.valueOf(scoreObejct.getInt("localteam_score"));
                        String visitorTeamScore = String.valueOf(scoreObejct.getInt("visitorteam_score"));
                        score = localTeamScore + " : " + visitorTeamScore;

                        status = timeObject.getString("status");

                        JSONObject startTime = timeObject.getJSONObject("starting_at");
                        timeStart = startTime.getString("time");

                        LivescoreModel model = new LivescoreModel(localTeamName, localTeamLogo, visitorTeamName, visitorTeamLogo, score, status, timeStart);
                        listScore.add(model);
                    }
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    Log.e("JSONERRO LIVESCORE", e.getLocalizedMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEYERROR LIVESCORE", error.getLocalizedMessage());
            }
        });

        Volley.newRequestQueue(getActivity()).add(livescoreRequest);

        return view;
    }

}
