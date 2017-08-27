package com.malikbisic.sportapp.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.MyClubsNewsAdapter;
import com.malikbisic.sportapp.model.MyClubNews;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nane on 27.8.2017.
 */

public class FragmentMyClubNews extends Fragment {



    String URL_BASE = "https://skysportsapi.herokuapp.com";
    String URL_NEWS = "/sky/football/getteamnews/";
    String URL_CODE = "/v1.0/";

    private ArrayList<MyClubNews> myClubNewsesModel = new ArrayList<>();
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_club_news,container,false);

        recyclerView = (RecyclerView) view.findViewById(R.id.my_club_news_recView);

        final MyClubsNewsAdapter adapter = new MyClubsNewsAdapter(myClubNewsesModel, getContext());

        recyclerView.setAdapter(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        String myClub = MainPage.myClubName.toLowerCase();

        String url = URL_BASE + URL_NEWS + myClub + URL_CODE;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        String title = object.getString("title");
                        String shortdesc = object.getString("shortdesc");
                        String image = object.getString("imgsrc");
                        String url = object.getString("link");

                        MyClubNews model = new MyClubNews(shortdesc, title, image, url);

                        myClubNewsesModel.add(model);

                    } catch (JSONException e) {
                        Log.e("Error", e.getLocalizedMessage());
                    }

                    adapter.notifyDataSetChanged();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(getContext()).add(request);

        return view;
    }


    public static class MyClubNewsViewHolder extends RecyclerView.ViewHolder{

        public View view;
        TextView title, desc;
        public ImageView image;
        public MyClubNewsViewHolder(View itemView) {
            super(itemView);

            view = itemView;

            title = (TextView) view.findViewById(R.id.myClubNewsTitle);
            desc = (TextView) view.findViewById(R.id.myClubNewsDesc);
            image = (ImageView) view.findViewById(R.id.myClubNewsImage);
        }

        public void updateUI(MyClubNews myClubNews, Context ctx){
            title.setText(myClubNews.getTitle());
            desc.setText(myClubNews.getShortdesc());

            Picasso.with(ctx).load(myClubNews.getImgsrc()).into(image);
            image.setTag(myClubNews.getUrl());
        }
    }
}
