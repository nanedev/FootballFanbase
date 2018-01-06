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
import com.malikbisic.sportapp.adapter.api.OddsAdapter;
import com.malikbisic.sportapp.model.api.OddsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentOdds extends Fragment {

    //https://soccer.sportmonks.com/api/v2.0/odds/fixture/1625119?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s

    String URL_BASE = "https://soccer.sportmonks.com/api/v2.0/odds/fixture/";
    String URL_API = "?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s";

    String fixturesID;
    String companyName;
    String value1, valueX, value2;

    ArrayList<OddsModel> oddsModelArrayList = new ArrayList<>();
    OddsAdapter adapter;
    RecyclerView oddsRecyclerview;

    public FragmentOdds() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fragment_odds, container, false);

            fixturesID = getActivity().getIntent().getStringExtra("idFixtures");
            oddsRecyclerview = (RecyclerView) v.findViewById(R.id.odds_recView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        adapter = new OddsAdapter(getActivity(), oddsModelArrayList);
        oddsRecyclerview.setLayoutManager(manager);
        oddsRecyclerview.setAdapter(adapter);

        String fullUrl = URL_BASE + fixturesID + URL_API;


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, fullUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray objectArray = response.getJSONArray("data");

                    JSONObject objectData = objectArray.getJSONObject(0);
                    JSONObject bookmarkObject = objectData.getJSONObject("bookmaker");
                    JSONArray dataArray = bookmarkObject.getJSONArray("data");

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject objectInArray = dataArray.getJSONObject(i);
                        companyName = objectInArray.getString("name");

                        JSONObject objectOdds = objectInArray.getJSONObject("odds");
                        JSONArray oddsArray = objectOdds.getJSONArray("data");


                        JSONObject valueo1bject = oddsArray.getJSONObject(0);
                        value1 = valueo1bject.getString("value");

                        JSONObject valueo2bject = oddsArray.getJSONObject(1);
                        value2 = valueo2bject.getString("value");

                        JSONObject valueoXbject = oddsArray.getJSONObject(2);
                        valueX = valueoXbject.getString("value");


                        OddsModel model = new OddsModel(value1, valueX, value2, companyName);
                        oddsModelArrayList.add(model);
                        adapter.notifyDataSetChanged();
                    }



                } catch (JSONException e) {
                    Log.e("JSON ODDS", e.getLocalizedMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY ODDS ", error.getLocalizedMessage());
            }
        });

        Volley.newRequestQueue(getActivity()).add(request);

        return v;
    }

}
