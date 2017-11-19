package com.malikbisic.sportapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.SimpleResource;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGExternalFileResolver;
import com.caverock.androidsvg.SVGParseException;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.SvgDrawableTranscoder;
import com.malikbisic.sportapp.adapter.CountryRecyclerAdapter;
import com.malikbisic.sportapp.model.CountryModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchableCountry extends AppCompatActivity implements SearchView.OnQueryTextListener {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    CountryRecyclerAdapter adapter;
    ArrayList<CountryModel> countryList = new ArrayList<>();
SearchView searchView;
    String url;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable_country);
        recyclerView = (RecyclerView) findViewById(R.id.rec_view_for_search_countries);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new CountryRecyclerAdapter(countryList,this,SearchableCountry.this);
        recyclerView.setAdapter(adapter);
        url = "https://restcountries.eu/rest/v2/all";
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSearchCountry);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search country");

        mDialog = new ProgressDialog(SearchableCountry.this, R.style.AppTheme_Dark_Dialog);
        mDialog.setIndeterminate(true);
        mDialog.setMessage("Loading...");
        mDialog.show();



        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("json", response.toString());
                try {
                    for (int i = 0; i < response.length(); i++) {

                        JSONObject object = response.getJSONObject(i);
                        String countryName = object.getString("name");
                        String countryImage = object.getString("flag");
                        Log.v("json", countryName);
                        Log.v("json", countryImage);
                        CountryModel model = new CountryModel(countryName, countryImage);
                        countryList.add(model);

                    }


                } catch (JSONException e) {
                    Log.v("json", e.getLocalizedMessage());
                }
                adapter.notifyDataSetChanged();
                mDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("json", error.getLocalizedMessage());
            }
        });
        Volley.newRequestQueue(this).add(request);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_club, menu);

        MenuItem search = menu.findItem(R.id.search);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(search);
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
                ArrayList<CountryModel> newList = new ArrayList<>();
                for (CountryModel countryModel : countryList){
                    String name = countryModel.getCountryName().toLowerCase();
                    if (name.contains(newText)){

                        newList.add(countryModel);


                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(SearchableCountry.this));
                adapter = new CountryRecyclerAdapter(newList,SearchableCountry.this,SearchableCountry.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();


                return true;

            }
        });
    }


    public static class CountriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircleImageView flag;
        TextView country_name;
        Context context;
        ArrayList<CountryModel> countries = new ArrayList<>();
        Activity activity;

        public CountriesViewHolder(View itemView,Context context,ArrayList<CountryModel> countries,Activity activity) {
            super(itemView);
            this.countries = countries;
            this.context = context;
            this.activity = activity;
            itemView.setOnClickListener(this);
            country_name = (TextView) itemView.findViewById(R.id.country_name);
            flag = (CircleImageView) itemView.findViewById(R.id.country_image);
            flag.setLayerType(View.LAYER_TYPE_SOFTWARE, null);





        }



        public void updateUI(CountryModel model) throws SVGParseException {


            country_name.setText(model.getCountryName());

             GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;

            requestBuilder = Glide
                    .with(context)
                    .using(Glide.buildStreamModelLoader(Uri.class, context), InputStream.class)
                    .from(Uri.class)
                    .as(SVG.class)
                    .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                    .sourceEncoder(new StreamEncoder())
                    .cacheDecoder(new FileToStreamDecoder<SVG>(new SvgDecoder()))
    .decoder(new SvgDecoder())
                    .animate(android.R.anim.fade_in);


            Uri uri = Uri.parse(model.getCountryImage());
            requestBuilder
                    // SVG cannot be serialized so it's not worth to cache it
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .load(uri)
                    .into(flag);
        }

        @Override
        public void onClick(View v) {
int position = getAdapterPosition();
            CountryModel country = this.countries.get(position);
            Intent intent = new Intent(this.context, EnterUsernameForApp.class);
            intent.putExtra("countryName", country.getCountryName());
            intent.putExtra("countryImg", country.getCountryImage());
            activity.setResult(RESULT_OK, intent);
            activity.finish();
        }
    }


    public static class SvgDecoder implements ResourceDecoder<InputStream, SVG> {
        private SvgFileResolver svgFileResolver;

        public Resource<SVG> decode(InputStream source, int width, int height) throws IOException {
            svgFileResolver = new SvgFileResolver();
            try {
                SVG svg = SVG.getFromInputStream(source);
                svg.registerExternalFileResolver(svgFileResolver);

                return new SimpleResource<SVG>(svg);
            } catch (SVGParseException ex) {
                throw new IOException("Cannot load SVG from stream", ex);
            }
        }

        @Override
        public String getId() {
            return "SvgDecoder.com.bumptech.svgsample.app";
        }
    }

    public static class SvgFileResolver extends SVGExternalFileResolver {
        @Override public Bitmap resolveImage(String filename) {
            return getBitmapFromURL(filename);
        }

        public  Bitmap getBitmapFromURL(String src) {
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
