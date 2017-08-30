package com.malikbisic.sportapp.activity;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.malikbisic.sportapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WebViewNewsActivity extends AppCompatActivity {

    WebView webView;
    TextView bodyTextbview;
    ImageView allNewsImage;
    String bodyTextString;
    String imageString;
    TextView titleTextview;
    String titleGet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_news);

        bodyTextbview = (TextView) findViewById(R.id.body_text_all_news);
        allNewsImage = (ImageView) findViewById(R.id.image_in_allnews);
titleTextview = (TextView) findViewById(R.id.title_text);
        Intent intent = getIntent();
        bodyTextString = intent.getStringExtra("bodyText");
 String[] words = bodyTextString.split("\\.");

        for (int i = 0; i < words.length;i++){
            StringBuilder builder = new StringBuilder();
            for (String s: words) {
                builder.append(s);
                builder.append(". ");
                builder.append("\n");
                builder.append("\n");
            }

            bodyTextbview.setText(builder.toString());
        }


        imageString = intent.getStringExtra("image");
        titleGet = intent.getStringExtra("title");
        Picasso.with(this).load(imageString).into(allNewsImage);

        titleTextview.setText(titleGet);



/*
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewController());
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(url);*/
    }

}
