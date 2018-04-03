package com.malikbisic.sportapp.fragment.api;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAllMatches extends Fragment implements SearchView.OnQueryTextListener {
    final String URL_BASE = "https://soccer.sportmonks.com/api/v2.0/countries";
    final String URL_APIKEY = Constants.API_KEY;

    RecyclerView selectLeagueRecyclerView;
    ArrayList<LeagueModel> selectLeaguelist;
    String leagueName;
    int currentSeason;
    String countryName;
    int league_id;
    SelectLeagueAdapter adapterLeague;
    SearchView mSearchView;
    JSONObject extra;
    int currentPage = 1;
    LinearLayoutManager linearLayoutManager;
    ProgressDialog progressBar;

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
        progressBar = new ProgressDialog(getActivity());
        selectLeagueRecyclerView = (RecyclerView) view.findViewById(R.id.search_league_recyclerview);
        selectLeaguelist = new ArrayList<>();
        adapterLeague = new SelectLeagueAdapter(selectLeaguelist, getContext(), getActivity(), selectLeagueRecyclerView);
        selectLeagueRecyclerView.setAdapter(adapterLeague);

        mSearchView = (SearchView) view.findViewById(R.id.search_for_league);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        selectLeagueRecyclerView.setLayoutManager(linearLayoutManager);
        setupSearchView();

        progressBar.setCancelable(false);
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.setMessage("Loading...");
        progressBar.setIndeterminate(true);
        progressBar.show();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                newText = newText.toLowerCase();
                ArrayList<LeagueModel> newList = new ArrayList<>();
                for (LeagueModel leagueModel : selectLeaguelist) {
                    String name = leagueModel.getName().toLowerCase();
                    if (name.contains(newText)) {

                        newList.add(leagueModel);


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

        adapterLeague.setOnLoadMore(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                currentPage++;
                loadData();

            }
        });
        return view;
    }

    public void loadData() {

        final String url = URL_BASE + URL_APIKEY + "&include=leagues" + "&page=" + currentPage;

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {

                try {

                    final JSONArray arrayCountry = response.getJSONArray("data");

                    for (int i = 0; i < arrayCountry.length(); i++) {
                        if (!getActivity().isFinishing()) {
                            progressBar.setMessage("Loadind...");

                            progressBar.show();

                            JSONObject objectCountry = arrayCountry.getJSONObject(i);

                            countryName = objectCountry.getString("name");
                            String countryID = objectCountry.getString("id");

                            JSONObject jsonObject = objectCountry.getJSONObject("leagues");


                            JSONArray jsonArray = jsonObject.getJSONArray("data");


                            for (int j = 0; j < jsonArray.length(); j++) {

                                JSONObject obj = jsonArray.getJSONObject(j);

                                league_id = obj.getInt("id");
                                currentSeason = obj.getInt("current_season_id");
                                leagueName = obj.getString("name");
                                LeagueModel model = new LeagueModel(leagueName, String.valueOf(currentSeason), countryName, league_id);
                                selectLeaguelist.add(model);
                                adapterLeague.setIsLoading(false);

                            }


                        }
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
        ArrayList<LeagueModel> leagues = new ArrayList<>();
        View vm;
        CircleImageView zastava;

        public SelectLeagueViewHolder(View itemView, ArrayList<LeagueModel> leagues) {
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
                } else {


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

                                    Glide.with(ctx).load(countryImage).into(zastava);

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
