package com.malikbisic.sportapp.viewHolder.api;

import android.content.Context;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.malikbisic.sportapp.model.api.SvgDrawableTranscoder;
import com.malikbisic.sportapp.model.api.TeamModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nane on 24.10.2017.
 */

public class TeamSquadViewHolder  extends RecyclerView.ViewHolder{
 TextView teamPositionTextview;
 CircleImageView playerImage;
 TextView playerNameTextview;
 TextView playerFromTextview;
 TextView shirtNumberPlayer;
 TextView positionNameTextview;
 Context context;
CircleImageView playerFlag;




    public TeamSquadViewHolder(View itemView) {
        super(itemView);


playerImage = (CircleImageView) itemView.findViewById(R.id.player_image);
playerNameTextview = (TextView) itemView.findViewById(R.id.player_name);
playerFlag = (CircleImageView) itemView.findViewById(R.id.playerCountryImage);
shirtNumberPlayer = (TextView) itemView.findViewById(R.id.shirt_number);
context = itemView.getContext();




    }


    public void updateUI (TeamModel team, Context context) {


        Picasso.with(context).load(team.getPlayerImage()).into(playerImage);
        playerNameTextview.setText(team.getCommonName());

        shirtNumberPlayer.setText(String.valueOf(team.getNumberId()));


        if (team.getNationality().equals("England")) {
            playerFlag.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.england));
        } else if (team.getNationality().equals("Northern Ireland")) {
            playerFlag.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.northern_ireland));
        } else if (team.getNationality().equals("Scotland")) {
            playerFlag.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.scotland));
        } else if (team.getNationality().equals("Wales")) {
            playerFlag.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.welsh_flag));
        } else {

            String countryURL = "https://restcountries.eu/rest/v2/name/" + team.getNationality();

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

playerFlag.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
playerFlag.setAlpha(0.9f);
                            Uri uri = Uri.parse(countryImage);

                            requestBuilder
                                    // SVG cannot be serialized so it's not worth to cache it
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .load(uri)
                                    .into(playerFlag);


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

    }
}
