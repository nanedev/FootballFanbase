package com.malikbisic.sportapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SelectLeagueActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    final String URL_BASE = "https://soccer.sportmonks.com/api/v2.0/leagues";
    final String URL_APIKEY = "?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s";

    ListView leagueListView;
    ArrayList<String> arrayListLeague;
    ArrayAdapter adapterLeague;
    SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_league);

        leagueListView = (ListView) findViewById(R.id.league_list);
        arrayListLeague = new ArrayList<>();
        adapterLeague = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListLeague);
        leagueListView.setAdapter(adapterLeague);
        mSearchView = (SearchView) findViewById(R.id.search_league);

        leagueListView.setTextFilterEnabled(true);
        setupSearchView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;


            }
        });

        final String url = URL_BASE + URL_APIKEY;

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    final JSONArray arrayLeague = response.getJSONArray("data");

                    for (int i = 0; i < arrayLeague.length(); i++) {

                        JSONObject objectLeague = arrayLeague.getJSONObject(i);

                        String leagueName = objectLeague.getString("name");
                        arrayListLeague.add(leagueName);
                        adapterLeague.notifyDataSetChanged();

                        leagueListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

                                try {
                                    JSONObject seasonID = arrayLeague.getJSONObject(pos);


                                    String leagueID = seasonID.getString("current_season_id");


                                Intent cnt = new Intent(SelectLeagueActivity.this, SelectClubActivity.class);
                                cnt.putExtra("leagueID", leagueID);
                                startActivity(cnt);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });


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

    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mSearchView.clearFocus();
        mSearchView.setQueryHint("Search league");
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            adapterLeague.getFilter().filter(null);
        } else {
            adapterLeague.getFilter().filter(newText);
        }
        return true;
    }
}
