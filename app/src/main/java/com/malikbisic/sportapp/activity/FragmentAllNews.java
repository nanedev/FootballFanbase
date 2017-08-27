package com.malikbisic.sportapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.AllNewsAdapter;
import com.malikbisic.sportapp.model.AllNewsModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nane on 27.8.2017.
 */

public class FragmentAllNews extends Fragment {
RecyclerView allNewsRecyclerView;
ArrayList<AllNewsModel> arrayList = new ArrayList<>();
    AllNewsAdapter adapter;
    final String ALL_NEWS_URL = "https://skysportsapi.herokuapp.com/sky/getnews/football/v1.0/";
String url;
    String urlJson;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_news,container,false);

       allNewsRecyclerView = (RecyclerView) view.findViewById(R.id.all_news_recycler_view);
        adapter = new AllNewsAdapter(arrayList,getContext());

        allNewsRecyclerView.setHasFixedSize(false);
        allNewsRecyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        allNewsRecyclerView.setLayoutManager(manager);

url = ALL_NEWS_URL;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++){

                    try {
                        JSONObject object = response.getJSONObject(i);
                        String title = object.getString("title");
                        String shortdesc = object.getString("shortdesc");
                        String image = object.getString("imgsrc");
                       String url = object.getString("link");

                        AllNewsModel model = new AllNewsModel(title,shortdesc,image,url);


                        arrayList.add(model);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    adapter.notifyDataSetChanged();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
Volley.newRequestQueue(getContext()).add(jsonArrayRequest);
        return view;
    }


    public static class AllNewsViewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView;
        ImageView allNewsImage;
        TextView descriptionTextview;
String urlFromJson;

        public AllNewsViewHolder(final View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.all_news_title);
            allNewsImage = (ImageView) itemView.findViewById(R.id.all_news_image);
            descriptionTextview = (TextView) itemView.findViewById(R.id.all_news_shrt_description);
itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(itemView.getContext(),WebViewNewsActivity.class);
        intent.putExtra("url",urlFromJson);
      itemView.getContext().startActivity(intent);
    }
});


        }

       public void setTitle(String title) {
           if (title != null){

               titleTextView.setText(title);
           }
        }
        public void setShortDesc(String shortDesc) {
            if (shortDesc != null){

                descriptionTextview.setText(shortDesc);
            }
        }
        public void setImgsrc(Context context,String imgsrc) {
            if (imgsrc != null){
                Picasso.with(context).load(imgsrc).into(allNewsImage);

            }
        }
        public void setUrl(String url){
            if (url != null)
            urlFromJson = url;
        }


    }
}
