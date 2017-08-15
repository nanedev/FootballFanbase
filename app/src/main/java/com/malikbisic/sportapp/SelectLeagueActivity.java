package com.malikbisic.sportapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class SelectLeagueActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    final String URL_BASE = "https://soccer.sportmonks.com/api/v2.0/leagues";
    final String URL_APIKEY = "?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s";

    RecyclerView leagueListView;
    ArrayList<LeagueModel> arrayListLeague;

    LeagueAdapter adapterLeague;
    SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_league);


        leagueListView = (RecyclerView) findViewById(R.id.league_list);
        arrayListLeague = new ArrayList<>();
        adapterLeague = new LeagueAdapter(arrayListLeague, this, this);
        leagueListView.setAdapter(adapterLeague);
        leagueListView.setLayoutManager(new LinearLayoutManager(this));
        mSearchView = (SearchView) findViewById(R.id.search_league);


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
                for (LeagueModel leagueModel : arrayListLeague) {
                    String name = leagueModel.getName().toLowerCase();
                    if (name.contains(newText)) {

                        newList.add(leagueModel);


                    }
                }
                leagueListView.setLayoutManager(new LinearLayoutManager(SelectLeagueActivity.this));
                adapterLeague = new LeagueAdapter(newList, SelectLeagueActivity.this, SelectLeagueActivity.this);
                leagueListView.setAdapter(adapterLeague);
                adapterLeague.notifyDataSetChanged();


                return true;


            }
        });

        final String url = URL_BASE + URL_APIKEY;

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {

                try {
                    final JSONArray arrayLeague = response.getJSONArray("data");

                    for (int i = 0; i < arrayLeague.length(); i++) {

                        JSONObject objectLeague = arrayLeague.getJSONObject(i);

                        final String leagueName = objectLeague.getString("name");
                        final String leagueID = objectLeague.getString("current_season_id");
                        String countryID = objectLeague.getString("country_id");

                        String urlCountry = "https://soccer.sportmonks.com/api/v2.0/countries/"+countryID+URL_APIKEY;

                        JsonObjectRequest requestCountry = new JsonObjectRequest(Request.Method.GET, urlCountry, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response2) {

                                try {
                                    JSONObject countryObject = response2.getJSONObject("data");


                                        String countryName = countryObject.getString("name");

                                        LeagueModel model = new LeagueModel(leagueName, leagueID, countryName);
                                        arrayListLeague.add(model);
                                        adapterLeague.notifyDataSetChanged();


                                } catch (JSONException e) {
                                    Log.e("countryError", e.getLocalizedMessage());
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });

                        Volley.newRequestQueue(SelectLeagueActivity.this).add(requestCountry);




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

        Volley.newRequestQueue(this).add(request);


    }

    //nesto
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        adapterLeague.onActivityResult(requestCode, resultCode, data);

        if (requestCode == adapterLeague.OPEN_CLUB && resultCode == RESULT_OK){
            String clubName = data.getStringExtra("clubNameLeague");
            String clubLogo = data.getStringExtra("clubLogoLeague");

            Intent backToEnterusername = new Intent(SelectLeagueActivity.this, EnterUsernameForApp.class);
            backToEnterusername.putExtra("clubName", clubName);
            backToEnterusername.putExtra("clubLogo", clubLogo);
            setResult(Activity.RESULT_OK, backToEnterusername);
            finish();
        }
    }

    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mSearchView.clearFocus();
        mSearchView.setQueryHint("Search league");
    }

    public static class LeagueViewHolder extends RecyclerView.ViewHolder{

        TextView leagueName;
        TextView countryName;
        ArrayList<LeagueModel> leagues = new ArrayList<>();
        View vm;

        public LeagueViewHolder(View itemView, ArrayList<LeagueModel> leagues) {
            super(itemView);
            vm = itemView;
            leagueName = (TextView) vm.findViewById(R.id.leagueName);
            countryName = (TextView) vm.findViewById(R.id.countryName);
            this.leagues = leagues;
        }

        public void updateUI(LeagueModel model){

            leagueName.setText(model.getName());
            countryName.setText(model.getCountry_name());

            Log.i("country: ", model.getCountry_name() + " , league: " + model.getName());

        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return  true;
    }
}
