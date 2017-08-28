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

public class WebViewNewsActivity extends AppCompatActivity {

    WebView webView;
    TextView bodyTextbview;
    ImageView allNewsImage;
    String bodyTextString;
    String imageString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_news);
        webView = (WebView)findViewById(R.id.webview);
bodyTextbview = (TextView) findViewById(R.id.body_text_all_news);
        allNewsImage = (ImageView) findViewById(R.id.image_in_allnews);

        Intent intent = getIntent();
        bodyTextString = intent.getStringExtra("bodyText");
        imageString = intent.getStringExtra("image");

        Picasso.with(this).load(imageString).into(allNewsImage);
        bodyTextbview.setText(bodyTextString);



/*
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewController());
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(url);*/
    }

}
