package com.malikbisic.sportapp.activity;

import android.content.Context;
import android.content.Intent;
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
    final String ALL_NEWS_URL = "http://content.guardianapis.com/search?section=football&order-by=newest&show-fields=headline%2Cthumbnail%2CtrailText%2CbodyText&page-size=20&api-key=c04727e7-5abc-498d-ad70-d6b4e53730f2";
    String url;
    String urlJson;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_news, container, false);

        allNewsRecyclerView = (RecyclerView) view.findViewById(R.id.all_news_recycler_view);
        adapter = new AllNewsAdapter(arrayList, getContext());

        allNewsRecyclerView.setHasFixedSize(false);
        allNewsRecyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        allNewsRecyclerView.setLayoutManager(manager);

        url = ALL_NEWS_URL;


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject jsonObject = response.getJSONObject("response");
                   JSONArray results = jsonObject.getJSONArray("results");
                    for (int i = 0; i<results.length();i++){
                        JSONObject object = results.getJSONObject(i);
                        JSONObject fields = object.getJSONObject("fields");
                        String headline = fields.getString("headline");
                        String trailText = fields.getString("trailText");
                        String thumbnail = fields.getString("thumbnail");
                        String bodyText = fields.getString("bodyText");

                        AllNewsModel model = new AllNewsModel(headline,trailText,thumbnail,bodyText);
                        arrayList.add(model);



                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.v("nestonijeured",e.getLocalizedMessage());
                }
adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(getContext()).add(request);

        return view;
    }


    public static class AllNewsViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView allNewsImage;
        TextView descriptionTextview;
        String bodyTextString;
        String imageString;
        String titlePass;

        public AllNewsViewHolder(final View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.all_news_title);
            allNewsImage = (ImageView) itemView.findViewById(R.id.all_news_image);
            descriptionTextview = (TextView) itemView.findViewById(R.id.all_news_shrt_description);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), WebViewNewsActivity.class);
                    intent.putExtra("bodyText", bodyTextString);
                    intent.putExtra("image",imageString);
                    intent.putExtra("title",titlePass);
                    itemView.getContext().startActivity(intent);
                }
            });


        }

        public void setHeadline(String headline) {
            if (headline != null) {
                titlePass = headline;
                titleTextView.setText(headline);
            }
        }


        public void setThumbnail(Context context, String thumbnail) {
            if (thumbnail != null) {
                imageString = thumbnail;
                Picasso.with(context).load(thumbnail).into(allNewsImage);


            }
        }

        public void setBodyText(String bodyText) {
            if (bodyText != null)
                bodyTextString = bodyText;

        }


    }
}
