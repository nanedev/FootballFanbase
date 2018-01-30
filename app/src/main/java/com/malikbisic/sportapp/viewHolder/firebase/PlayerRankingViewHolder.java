package com.malikbisic.sportapp.viewHolder.firebase;

import android.app.Activity;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.api.SearchableCountry;
import com.malikbisic.sportapp.fragment.firebase.ProfileFragment;
import com.malikbisic.sportapp.model.api.PlayerModel;
import com.malikbisic.sportapp.model.api.SvgDrawableTranscoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nane on 27.1.2018.
 */

public class PlayerRankingViewHolder extends RecyclerView.ViewHolder {
    public CircleImageView playerImageRankingMonth;
    public TextView playerPositionRankingMonth;
    public TextView playerPointsRankingMonth;
    public TextView numberVotesRankingMonth;
    public TextView playerName;
    public CircleImageView clubLogoImage;
    public CircleImageView countryLogo;
public Button votePlayer;

    int number;

    public PlayerRankingViewHolder(View itemView) {

        super(itemView);

        playerImageRankingMonth = (CircleImageView) itemView.findViewById(R.id.playerRankingImage);
        playerPositionRankingMonth = (TextView) itemView.findViewById(R.id.playerRankingposition);
        playerPointsRankingMonth = (TextView) itemView.findViewById(R.id.playerRankingPints);
        numberVotesRankingMonth = (TextView) itemView.findViewById(R.id.playerRAnkingFansVotedNmbr);
        playerName = (TextView) itemView.findViewById(R.id.playerRankingName);
        clubLogoImage = (CircleImageView) itemView.findViewById(R.id.playerRankingClub);
        countryLogo = (CircleImageView) itemView.findViewById(R.id.playerRankingCOuntry);
        votePlayer = (Button) itemView.findViewById(R.id.voteplayerButton);


    }

    public void setCountryClub(PlayerModel model) {

        String urlClubCountryPlayer = "https://soccer.sportmonks.com/api/v2.0/players/" + model.getPlayerID() + "?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s&include=team";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlClubCountryPlayer, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    String country = data.getString("nationality");

                    JSONObject teamObj = data.getJSONObject("team");
                    JSONObject dataTeam = teamObj.getJSONObject("data");
                    String clubLogo = dataTeam.getString("logo_path");
                    String clubName = dataTeam.getString("name");
                    Glide.with(itemView.getContext()).load(clubLogo).into(clubLogoImage);


                    if (country.equals("England")) {
                        countryLogo.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.england));

                    } else if (country.equals("Northern Ireland")) {
                        countryLogo.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.northern_ireland));

                    } else if (country.equals("Scotland")) {

                        countryLogo.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.scotland));
                    } else if (country.equals("Wales")) {

                        countryLogo.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.welsh_flag));
                    } else {

                        String countryURL = "https://restcountries.eu/rest/v2/name/" + country;

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
                                                .into(countryLogo);


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
                } catch (JSONException e) {
                    Log.e("errJSOn", e.getLocalizedMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("vollError", error.getLocalizedMessage());
            }
        });
        Volley.newRequestQueue(itemView.getContext()).add(request);


    }


    public void setNumbervotes(PlayerModel model) {
        DateFormat currentDateFormat = new SimpleDateFormat("MMMM");
        final Date currentDate = new Date();

        final String currentMonth = currentDateFormat.format(currentDate);
        FirebaseFirestore.getInstance().collection("PlayerPoints").document(currentMonth).collection("player-id").document(model.getPlayerID()).collection("usersVote").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                number = documentSnapshots.size();
                numberVotesRankingMonth.setText(String.valueOf(number));
            }
        });
    }

}
