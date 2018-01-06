package com.malikbisic.sportapp.viewHolder.api;

import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
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
import com.malikbisic.sportapp.model.api.LeagueNameAllFixtureModel;
import com.malikbisic.sportapp.model.api.SvgDrawableTranscoder;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by korisnik on 27/12/2017.
 */

public class LeagueNameViewHolder extends GroupViewHolder {

    TextView countryName;
    TextView leagueName;
    CircleImageView zastava;

    public LeagueNameViewHolder(View itemView) {
        super(itemView);

        countryName = (TextView) itemView.findViewById(R.id.countryNameFixtures);
        leagueName = (TextView) itemView.findViewById(R.id.leagueNameFixures);
        zastava = (CircleImageView) itemView.findViewById(R.id.flagCountry);
    }

    public void setLeagueName(ExpandableGroup group){
        if (group instanceof LeagueNameAllFixtureModel) {
            leagueName.setText(group.getTitle());
            countryName.setText(((LeagueNameAllFixtureModel) group).getCountryName());
        }
    }

    public void setFlag(ExpandableGroup group){
        String drzava = ((LeagueNameAllFixtureModel) group).getCountryName();
        if (drzava.equals("England")) {
            zastava.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.england));
        } else if (drzava.equals("Northern Ireland")) {
            zastava.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.northern_ireland));
        } else if (drzava.equals("Scotland")) {
            zastava.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.scotland));
        } else if (drzava.equals("Wales")) {
            zastava.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.welsh_flag));
        }


        String countryURL = "https://restcountries.eu/rest/v2/name/" + drzava;

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
    }
    @Override
    public void expand() {
        super.expand();

    }
}
