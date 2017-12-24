package com.malikbisic.sportapp.activity;


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
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.LeagueAdapter;
import com.malikbisic.sportapp.adapter.SelectLeagueAdapter;
import com.malikbisic.sportapp.model.LeagueModel;
import com.malikbisic.sportapp.model.SvgDrawableTranscoder;

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
    final String URL_APIKEY = "?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s";

    RecyclerView selectLeagueRecyclerView;
    ArrayList<LeagueModel> selectLeaguelist;
    String leagueName;
    int currentSeason;
  String countryName;
    SelectLeagueAdapter adapterLeague;
    SearchView mSearchView;
    JSONObject extra;
    int currentPage = 1;
    LinearLayoutManager linearLayoutManager;
    public FragmentAllMatches() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_matches,container,false);
        selectLeagueRecyclerView = (RecyclerView) view.findViewById(R.id.search_league_recyclerview);
        selectLeaguelist = new ArrayList<>();
        adapterLeague = new SelectLeagueAdapter(selectLeaguelist, view.getContext(), getActivity(), selectLeagueRecyclerView);
        selectLeagueRecyclerView.setAdapter(adapterLeague);

        mSearchView = (SearchView) view.findViewById(R.id.search_for_league);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        selectLeagueRecyclerView.setLayoutManager(linearLayoutManager);
        setupSearchView();
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

    public void loadData(){
        final String url = URL_BASE + URL_APIKEY + "&include=leagues" + "&page=" + currentPage;

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {

                try {
                    final JSONArray arrayCountry = response.getJSONArray("data");

                    for (int i = 0; i < arrayCountry.length(); i++) {

                        JSONObject objectCountry = arrayCountry.getJSONObject(i);

                        countryName    = objectCountry.getString("name");
                        String countryID = objectCountry.getString("id");

                        JSONObject jsonObject = objectCountry.getJSONObject("leagues");




                        JSONArray jsonArray = jsonObject.getJSONArray("data");


                        for (int j = 0; j < jsonArray.length(); j++) {

                            JSONObject obj = jsonArray.getJSONObject(j);

                            currentSeason = obj.getInt("current_season_id");
                            leagueName = obj.getString("name");
                            LeagueModel model = new LeagueModel(leagueName, String.valueOf(currentSeason), countryName);
                            selectLeaguelist.add(model);
                            adapterLeague.notifyDataSetChanged();
                            adapterLeague.setIsLoading(false);
                    }


                    }

                } catch (JSONException e) {
                    Log.e("Excpetion JSON", "Err: " + e.getLocalizedMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LEAGUE", "Err: " + error.getLocalizedMessage());
            }
        });

        Volley.newRequestQueue(getActivity()).add(request);
    }

    private void setupSearchView() {

            mSearchView.setIconifiedByDefault(false);
            mSearchView.setOnQueryTextListener(this);
       getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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


    public static class SelectLeagueViewHolder extends RecyclerView.ViewHolder{

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

        public void updateUI(LeagueModel model){

            leagueName.setText(model.getName().toUpperCase());
            countryName.setText(model.getCountry_name().toUpperCase() + ":");



            if (model.getCountry_name().equals("England")){
                zastava.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.england));
            } else if (model.getCountry_name().equals("Northern Ireland")){
                zastava.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.northern_ireland));
            } else if (model.getCountry_name().equals("Scotland")){
                zastava.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.scotland));
            } else if (model.getCountry_name().equals("Wales")){
                zastava.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.welsh_flag));
            }


            String countryURL = "https://restcountries.eu/rest/v2/name/" + model.getCountry_name();

            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, countryURL, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.i("json", response.toString());
                    try {
                        for (int i = 0; i < response.length(); i++) {

                            JSONObject object = response.getJSONObject(i);
                            String countryName = object.getString("name");
                            String countryImage = object.getString("flag");

                            System.out.println("country flag" + countryImage);
                            GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;

                            requestBuilder = Glide
                                    .with(itemView.getContext())
                                    .using(Glide.buildStreamModelLoader(Uri.class, itemView.getContext()), InputStream.class)
                                    .from(Uri.class)
                                    .as(SVG.class)
                                    .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                                    .sourceEncoder(new StreamEncoder())
                                    .cacheDecoder(new FileToStreamDecoder<SVG>(new SearchableCountry.SvgDecoder()))
                                    .decoder(new SearchableCountry.SvgDecoder())
                                    .animate(android.R.anim.fade_in);


                            Uri uri = Uri.parse(countryImage);

                            requestBuilder
                                    // SVG cannot be serialized so it's not worth to cache it
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .load(uri)
                                    .into(zastava);

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

            Log.i("country: ", model.getCountry_name() + " , league: " + model.getName());

        }
    }

}
