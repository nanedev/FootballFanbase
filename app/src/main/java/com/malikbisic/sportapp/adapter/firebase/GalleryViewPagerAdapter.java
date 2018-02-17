package com.malikbisic.sportapp.adapter.firebase;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.malikbisic.sportapp.R;

import java.util.ArrayList;

/**
 * Created by malikbisic on 16/02/2018.
 */

public class GalleryViewPagerAdapter extends PagerAdapter {
    Activity activity;
    ArrayList<String> images;
    LayoutInflater layoutInflater;

    public GalleryViewPagerAdapter(Activity activity, ArrayList<String> images) {
        this.activity = activity;
        this.images = images;
        this.layoutInflater = LayoutInflater.from(activity);
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = layoutInflater.inflate(R.layout.gallery_fullscreen_images, container, false);

        ImageView imageViewPreview = (ImageView) view.findViewById(R.id.imagefromChat);


        Glide.with(activity).load(images.get(position))
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageViewPreview);

        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == ((View) obj);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
