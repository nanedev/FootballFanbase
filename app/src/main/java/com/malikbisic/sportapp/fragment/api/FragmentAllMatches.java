package com.malikbisic.sportapp.fragment.api;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;
import com.google.gson.JsonArray;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.StopAppServices;
import com.malikbisic.sportapp.listener.OnLoadMoreListener;
import com.malikbisic.sportapp.activity.api.SearchableCountry;
import com.malikbisic.sportapp.adapter.api.SelectLeagueAdapter;
import com.malikbisic.sportapp.model.api.LeagueModel;
import com.malikbisic.sportapp.model.api.SvgDrawableTranscoder;
import com.malikbisic.sportapp.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAllMatches extends Fragment implements SearchView.OnQueryTextListener {
    final String URL_BASE = " https://soccer.sportmonks.com/api/v2.0/leagues";
    final String URL_APIKEY = Constants.API_KEY;
    final String INCLUDE = "&include=country,season:order(league_id|asc)";

    RecyclerView selectLeagueRecyclerView;
    HashMap<Integer, LeagueModel> selectLeaguelist;
    String leagueName;
    int currentSeason;
    String countryName;
    int league_id;
    SelectLeagueAdapter adapterLeague;
    SearchView mSearchView;
    JSONObject extra;

    LinearLayoutManager linearLayoutManager;
    AlertDialog progressBar;
    boolean isCup;

    int leaguePos = 4;

    public FragmentAllMatches() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_matches, container, false);
        Intent closeAPP = new Intent(getContext(), StopAppServices.class);
        getActivity().startService(closeAPP);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        progressBar = new SpotsDialog(getActivity(),"Loading",R.style.StyleLogin);
        selectLeagueRecyclerView = (RecyclerView) view.findViewById(R.id.search_league_recyclerview);
        selectLeaguelist = new HashMap<>();
        adapterLeague = new SelectLeagueAdapter(selectLeaguelist, getContext(), getActivity(), selectLeagueRecyclerView);
        selectLeagueRecyclerView.setAdapter(adapterLeague);

        mSearchView = (SearchView) view.findViewById(R.id.search_for_league);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        selectLeagueRecyclerView.setLayoutManager(linearLayoutManager);

        selectLeagueRecyclerView.setHasFixedSize(true);
        selectLeagueRecyclerView.setItemViewCacheSize(40);
        selectLeagueRecyclerView.setDrawingCacheEnabled(true);
        selectLeagueRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        setupSearchView();

        progressBar.show();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                leaguePos = 0;

                newText = newText.toLowerCase();
                HashMap< Integer, LeagueModel> newList = new HashMap<>();
                for (LeagueModel leagueModel : selectLeaguelist.values()) {
                    String name = leagueModel.getName().toLowerCase();
                    if (name.contains(newText)) {

                        newList.put(leaguePos, leagueModel);
                        leaguePos++;


                    }

                    else if (TextUtils.isEmpty(name)){
                        selectLeaguelist.clear();
                        leaguePos = 4;
                        loadData();
                    }
                }

                selectLeagueRecyclerView.setLayoutManager(linearLayoutManager);
                adapterLeague = new SelectLeagueAdapter(newList, getActivity().getApplicationContext(), getActivity(), selectLeagueRecyclerView);
                selectLeagueRecyclerView.setAdapter(adapterLeague);
                selectLeagueRecyclerView.setItemViewCacheSize(newList.size());
                adapterLeague.notifyDataSetChanged();


                return true;


            }
        });
        loadData();


        return view;
    }

    public void loadData() {

        final String url = URL_BASE + URL_APIKEY + INCLUDE;

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {

                try {

                    final JSONArray array = response.getJSONArray("data");

                    for (int i = 0; i < array.length(); i++) {
                        if (!getActivity().isFinishing()) {
                            progressBar.setMessage("Loading...");

                            progressBar.show();
                            JSONObject obj = array.getJSONObject(i);
                            isCup = obj.getBoolean("is_cup");
                            String lgName = obj.getString("name");
                            JSONObject cObj = obj.getJSONObject("country");
                            JSONObject ob = cObj.getJSONObject("data");
                            String cyName = ob.getString("name");

                            if (!isCup) {

                                if (lgName.equals("Premier League") && cyName.equals("England")) {


                                    league_id = obj.getInt("id");
                                    currentSeason = obj.getInt("current_season_id");
                                    leagueName = obj.getString("name");


                                    JSONObject countryObj = obj.getJSONObject("country");
                                    JSONObject object = countryObj.getJSONObject("data");
                                    countryName = object.getString("name");

                                    LeagueModel model = new LeagueModel(leagueName, String.valueOf(currentSeason), countryName, league_id, isCup);
                                    selectLeaguelist.put(0, model);

                                } else if (lgName.equals("Bundesliga")) {


                                    league_id = obj.getInt("id");
                                    currentSeason = obj.getInt("current_season_id");
                                    leagueName = obj.getString("name");


                                    JSONObject countryObj = obj.getJSONObject("country");
                                    JSONObject object = countryObj.getJSONObject("data");
                                    countryName = object.getString("name");

                                    LeagueModel model = new LeagueModel(leagueName, String.valueOf(currentSeason), countryName, league_id, isCup);
                                    selectLeaguelist.put(1, model);

                                } else if (lgName.equals("Serie A")) {


                                    league_id = obj.getInt("id");
                                    currentSeason = obj.getInt("current_season_id");
                                    leagueName = obj.getString("name");


                                    JSONObject countryObj = obj.getJSONObject("country");
                                    JSONObject object = countryObj.getJSONObject("data");
                                    countryName = object.getString("name");

                                    LeagueModel model = new LeagueModel(leagueName, String.valueOf(currentSeason), countryName, league_id, isCup);
                                    selectLeaguelist.put(2, model);

                                } else if (lgName.equals("La Liga")) {


                                    league_id = obj.getInt("id");
                                    currentSeason = obj.getInt("current_season_id");
                                    leagueName = obj.getString("name");


                                    JSONObject countryObj = obj.getJSONObject("country");
                                    JSONObject object = countryObj.getJSONObject("data");
                                    countryName = object.getString("name");

                                    LeagueModel model = new LeagueModel(leagueName, String.valueOf(currentSeason), countryName, league_id, isCup);
                                    selectLeaguelist.put(3, model);

                                } else if (lgName.equals("Ligue 1")) {


                                    league_id = obj.getInt("id");
                                    currentSeason = obj.getInt("current_season_id");
                                    leagueName = obj.getString("name");


                                    JSONObject countryObj = obj.getJSONObject("country");
                                    JSONObject object = countryObj.getJSONObject("data");
                                    countryName = object.getString("name");

                                    LeagueModel model = new LeagueModel(leagueName, String.valueOf(currentSeason), countryName, league_id, isCup);
                                    selectLeaguelist.put(4, model);

                                } else {
                                    leaguePos++;
                                    league_id = obj.getInt("id");
                                    currentSeason = obj.getInt("current_season_id");
                                    leagueName = obj.getString("name");


                                    JSONObject countryObj = obj.getJSONObject("country");
                                    JSONObject object = countryObj.getJSONObject("data");
                                    countryName = object.getString("name");

                                    LeagueModel model = new LeagueModel(leagueName, String.valueOf(currentSeason), countryName, league_id, isCup);
                                    selectLeaguelist.put(leaguePos, model);
                                }
                            }
                        }
                        selectLeagueRecyclerView.setItemViewCacheSize(selectLeaguelist.size());
                        adapterLeague.notifyDataSetChanged();
                        mSearchView.clearFocus();
                    }

                } catch (JSONException e) {
                    Log.e("Excpetion JSON", "Err: " + e.getLocalizedMessage());
                    progressBar.dismiss();
                }
                progressBar.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LEAGUE", "Err: " + error.getLocalizedMessage());
                progressBar.dismiss();
            }
        });

        Volley.newRequestQueue(getActivity()).add(request);

    }

    private void setupSearchView() {

        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
        mSearchView.clearFocus();
        mSearchView.setQueryHint("Search league");

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    public static class SelectLeagueViewHolder extends RecyclerView.ViewHolder {

        public TextView leagueName;
        public TextView countryName;
        HashMap< Integer, LeagueModel> leagues = new HashMap<>();
        View vm;
        CircleImageView zastava;

        public SelectLeagueViewHolder(View itemView, HashMap< Integer, LeagueModel> leagues) {
            super(itemView);
            vm = itemView;
            leagueName = (TextView) vm.findViewById(R.id.leagueNameInMatches);
            countryName = (TextView) vm.findViewById(R.id.countryNameInMatches);
            zastava = (CircleImageView) vm.findViewById(R.id.zastavadrzave);
            vm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            this.leagues = leagues;
        }

        public void updateUI(LeagueModel model, final Activity ctx) {

            if (!ctx.isFinishing()) {
                leagueName.setText(model.getName().toUpperCase());
                countryName.setText(model.getCountry_name().toUpperCase() + ":");

                Log.i("country name: ", model.getCountry_name());


                if (model.getCountry_name().equals("England")) {
                    zastava.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.england));
                } else if (model.getCountry_name().equals("Northern Ireland")) {
                    zastava.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.northern_ireland));
                } else if (model.getCountry_name().equals("Scotland")) {
                    zastava.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.scotland));
                } else if (model.getCountry_name().equals("Wales")) {
                    zastava.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.welsh_flag));
                } else if (model.getCountry_name().equals("Europe")){
                    zastava.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.europe));

                }

                    else {


                    String countryURL = "http://countryapi.gear.host/v1/Country/getCountries?pName=" + model.getCountry_name();

                    final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, countryURL, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                JSONArray arrayLeague = response.getJSONArray("Response");
                                for (int i = 0; i < arrayLeague.length(); i++) {

                                    JSONObject object = arrayLeague.getJSONObject(i);
                                    String countryName = object.getString("Name");
                                    String countryImage = object.getString("FlagPng");

                                    System.out.println("country flag" + countryImage);

                                    Glide.with(ctx).load(countryImage).diskCacheStrategy(DiskCacheStrategy.ALL).into(zastava);

                                }


                            } catch (JSONException e) {
                                Log.v("json", e.getLocalizedMessage());
                            }

                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Log.v("json", error.getLocalizedMessage());
                        }
                    });
                    Volley.newRequestQueue(itemView.getContext()).add(request);

                }
                Log.i("country: ", model.getCountry_name() + " , league: " + model.getName());

            }


        }
    }

}
