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
import com.malikbisic.sportapp.adapter.api.CommentsMatchAdapter;
import com.malikbisic.sportapp.model.api.CommentaryMatchModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCommentsMatch extends Fragment {

    String fixturesID;
    public static String URL_BASE = "https://soccer.sportmonks.com/api/v2.0/commentaries/fixture/";
    public static String URL_API = "?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s";

    ArrayList<CommentaryMatchModel> modelArrayList = new ArrayList<>();
    CommentsMatchAdapter adapter;
    RecyclerView recyclerView;


    public FragmentCommentsMatch() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_fragment_comments_match, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.comments_recView);
        adapter = new CommentsMatchAdapter(modelArrayList, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        fixturesID = getActivity().getIntent().getStringExtra("idFixtures");
        String fullURL = URL_BASE + fixturesID + URL_API;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, fullURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray mainData = response.getJSONArray("data");

                    for (int i = 0; i < mainData.length(); i++){
                        JSONObject getData = mainData.getJSONObject(i);
                        int minute = getData.getInt("minute");
                        String comments = getData.getString("comment");

                        CommentaryMatchModel model = new CommentaryMatchModel(minute, comments);
                        modelArrayList.add(model);
                        adapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    Log.e("err", e.getLocalizedMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError", error.getLocalizedMessage());
            }
        });
        Volley.newRequestQueue(getActivity()).add(request);

        return view;
    }

}
