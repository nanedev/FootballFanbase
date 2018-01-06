package com.malikbisic.sportapp.fragment.api;

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
import com.malikbisic.sportapp.adapter.api.LineUpAdapter;
import com.malikbisic.sportapp.model.api.LineUpModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HomeTeamLineUp extends Fragment {

    private String URL_BASE = "https://soccer.sportmonks.com/api/v2.0/fixtures/";
    private String URL_API = "?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s";
    private String URL_INCLUDE = "&include=lineup";
    private String fixturesID;
    private String fullURL;

    RecyclerView homeLineUpRec;
    ArrayList<LineUpModel> lineUpModelArrayList = new ArrayList<>();
    LineUpAdapter adapter;
    public HomeTeamLineUp() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home_team_line_up, container, false);
        fixturesID = getActivity().getIntent().getStringExtra("idFixtures");

        fullURL = URL_BASE + fixturesID + URL_API + URL_INCLUDE;

        homeLineUpRec = (RecyclerView) v.findViewById(R.id.homeTeamLineup);
        adapter = new LineUpAdapter(lineUpModelArrayList, getActivity());
        homeLineUpRec.setLayoutManager(new LinearLayoutManager(getContext()));
        homeLineUpRec.setAdapter(adapter);

        Log.i("homeLine", "up team");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, fullURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject mainObject = response.getJSONObject("data");
                    JSONObject lineupObject = mainObject.getJSONObject("lineup");
                    JSONArray dataArray = lineupObject.getJSONArray("data");

                    for (int i = 0; i <= 10; i++){
                        JSONObject playerObject = dataArray.getJSONObject(i);
                        String playerName = playerObject.getString("player_name");
                        int playerNumber = playerObject.getInt("number");
                        int playerID = playerObject.getInt("player_id");
                        LineUpModel model = new LineUpModel(playerNumber, playerName, playerID);
                        lineUpModelArrayList.add(model);
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    Log.e("LineUp Home error", e.getLocalizedMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LineUp error volley", error.getLocalizedMessage());
            }
        });

        Volley.newRequestQueue(getContext()).add(request);
        return  v;
    }

}
