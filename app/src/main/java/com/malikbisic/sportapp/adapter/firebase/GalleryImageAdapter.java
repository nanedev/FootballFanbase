package com.malikbisic.sportapp.adapter.firebase;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.firebase.ChatMessageActivity;
import com.malikbisic.sportapp.model.firebase.GalleryImageModel;
import com.malikbisic.sportapp.utils.RoundedTransformation;
import com.malikbisic.sportapp.viewHolder.firebase.GalleryMessageViewHolder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by malikbisic on 14/02/2018.
 */

public class GalleryImageAdapter extends RecyclerView.Adapter<GalleryMessageViewHolder> {

    ArrayList<String> galleryImageModelArrayList;
    Activity activity;

    public GalleryImageAdapter(ArrayList<String> galleryImageModelArrayList, Activity activity) {
        this.galleryImageModelArrayList = galleryImageModelArrayList;
        this.activity = activity;
    }

    @Override
    public GalleryMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent,false);
        return new GalleryMessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final GalleryMessageViewHolder holder, final int position) {
        Log.i("gallery", galleryImageModelArrayList.get(position));
        Picasso.with(activity).setIndicatorsEnabled(false);

        Picasso.with(activity)
                .load(galleryImageModelArrayList.get(position))
                .resize(100,100)
                .centerCrop()

                .into(holder.galleryImage, new Callback() {
                    @Override
                    public void onSuccess() {


                    }

                    @Override
                    public void onError() {

                        Picasso.with(activity)
                                .load(galleryImageModelArrayList.get(position))
                                .transform(new RoundedTransformation(20, 3))
                                .fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .centerCrop()
                                .into(holder.galleryImage);

                    }
                });


    }

    @Override
    public int getItemCount() {
        return galleryImageModelArrayList.size();
    }
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private GalleryImageAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final GalleryImageAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
