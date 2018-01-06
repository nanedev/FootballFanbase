package com.malikbisic.sportapp.viewHolder.api;

import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.api.SearchableCountry;
import com.malikbisic.sportapp.model.api.LeagueModel;
import com.malikbisic.sportapp.model.api.SvgDrawableTranscoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by korisnik on 23/10/2017.
 */

public class AllFixturesViewHolder extends RecyclerView.ViewHolder {
    public TextView leagueName;
    public TextView countryName;
    ArrayList<LeagueModel> leagues = new ArrayList<>();
    View vm;
    CircleImageView zastava;
    String idFixture;

String countryImageForIntent;

    public AllFixturesViewHolder(View itemView) {
        super(itemView);

        vm = itemView;
        leagueName = (TextView) vm.findViewById(R.id.leagueNameInMatchesfixtures);
        countryName = (TextView) vm.findViewById(R.id.countryNameInMatchesfixtures);
        zastava = (CircleImageView) vm.findViewById(R.id.zastavadrzavefixtures);
        vm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



    }

    public void updateUI(LeagueModel model) {

        leagueName.setText(model.getName().toUpperCase());

        countryName.setText(model.getCountry_name().toUpperCase() + ":");


        if (model.getCountry_name().equals("England")) {
            zastava.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.england));
        } else if (model.getCountry_name().equals("Northern Ireland")) {
            zastava.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.northern_ireland));
        } else if (model.getCountry_name().equals("Scotland")) {
            zastava.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.scotland));
        } else if (model.getCountry_name().equals("Wales")) {
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
public void setFixtureId (int fixtureId){
      idFixture = String.valueOf(fixtureId);
}
}
