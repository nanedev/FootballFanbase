package com.malikbisic.sportapp.activity.api;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Intent;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;

import android.util.Log;
import android.view.View;
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
import com.malikbisic.sportapp.activity.StopAppServices;
import com.malikbisic.sportapp.activity.firebase.EnterUsernameForApp;
import com.malikbisic.sportapp.adapter.api.LeagueAdapter;
import com.malikbisic.sportapp.model.api.LeagueModel;
import com.malikbisic.sportapp.model.api.SvgDrawableTranscoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class SelectLeagueActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    final String URL_BASE = "https://soccer.sportmonks.com/api/v2.0/leagues";
    final String URL_APIKEY = "?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s";

    RecyclerView leagueListView;
    ArrayList<LeagueModel> arrayListLeague;

    LeagueAdapter adapterLeague;
    SearchView mSearchView;
    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_league);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSearchLeague);
        setSupportActionBar(toolbar);
      getSupportActionBar().setTitle("Search league");
        Intent closeAPP = new Intent(this, StopAppServices.class);
        startService(closeAPP);

        leagueListView = (RecyclerView) findViewById(R.id.league_list);
        arrayListLeague = new ArrayList<>();
        adapterLeague = new LeagueAdapter(arrayListLeague, this, this);
        leagueListView.setAdapter(adapterLeague);
        leagueListView.setLayoutManager(new LinearLayoutManager(this));

        mDialog = new SpotsDialog(SelectLeagueActivity.this,"Loading...", R.style.StyleLogin);


        mDialog.show();



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
                                        int league_id = countryObject.getInt("id");

                                        LeagueModel model = new LeagueModel(leagueName, leagueID, countryName, league_id);
                                        arrayListLeague.add(model);
                                        adapterLeague.notifyDataSetChanged();
                                        mDialog.dismiss();


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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_club, menu);

        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);

        return super.onCreateOptionsMenu(menu);
    }
    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

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
    }





    public static class LeagueViewHolder extends RecyclerView.ViewHolder{

        public TextView leagueName;
        public TextView countryName;
        ArrayList<LeagueModel> leagues = new ArrayList<>();
        View vm;
public CircleImageView zastava;
        public LeagueViewHolder(View itemView, ArrayList<LeagueModel> leagues) {
            super(itemView);
            vm = itemView;
            leagueName = (TextView) vm.findViewById(R.id.leagueName);
            countryName = (TextView) vm.findViewById(R.id.countryName);
            zastava = (CircleImageView) vm.findViewById(R.id.zastavadrzave2);
            this.leagues = leagues;
        }

        public void updateUI(LeagueModel model){

            leagueName.setText(model.getName());
            countryName.setText(model.getCountry_name() + ": ");

            Log.i("country: ", model.getCountry_name() + " , league: " + model.getName());


            if (model.getCountry_name().equals("England")) {
                zastava.setImageDrawable(vm.getContext().getResources().getDrawable(R.drawable.england));
            } else if (model.getCountry_name().equals("Northern Ireland")) {
                zastava.setImageDrawable(vm.getContext().getResources().getDrawable(R.drawable.northern_ireland));
            } else if (model.getCountry_name().equals("Scotland")) {
                zastava.setImageDrawable(vm.getContext().getResources().getDrawable(R.drawable.scotland));
            } else if (model.getCountry_name().equals("Wales")) {
                zastava.setImageDrawable(vm.getContext().getResources().getDrawable(R.drawable.welsh_flag));
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
                                    .with(vm.getContext())
                                    .using(Glide.buildStreamModelLoader(Uri.class, vm.getContext()), InputStream.class)
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
            Volley.newRequestQueue(vm.getContext()).add(request);

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
