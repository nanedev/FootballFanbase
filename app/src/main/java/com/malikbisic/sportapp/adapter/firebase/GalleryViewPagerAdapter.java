package com.malikbisic.sportapp.adapter.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.firebase.ChatMessageActivity;
import com.malikbisic.sportapp.activity.firebase.FullScreenImageFromChat;
import com.malikbisic.sportapp.activity.firebase.GalleryImageFullScreen;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by malikbisic on 16/02/2018.
 */

public class GalleryViewPagerAdapter extends PagerAdapter {
    Activity activity;
    ArrayList<String> images;
    LayoutInflater layoutInflater;

    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();

    public GalleryViewPagerAdapter(Activity activity, ArrayList<String> images) {
        this.activity = activity;
        this.images = images;
        this.layoutInflater = LayoutInflater.from(activity);
    }
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        View view = layoutInflater.inflate(R.layout.gallery_fullscreen_images, container, false);

        final ImageView imageViewPreview = (ImageView) view.findViewById(R.id.imagefromChat);


        Glide.with(activity).load(images.get(position))
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageViewPreview);

        container.addView(view);

        imageViewPreview.buildDrawingCache();


        return view;
    }
    public Bitmap StringToBitMap(String image){
        try{
            byte [] encodeByte=Base64.decode(image, Base64.DEFAULT);

            InputStream inputStream  = new ByteArrayInputStream(encodeByte);
            Bitmap bitmap  = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }
    @Override
    public int getCount() {
        return images.size();
    }
    public void saveFile(Bitmap b) {
        try {

            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/FootballFanBase/");

            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }

            File imageFile = File.createTempFile(
                    String.valueOf(Calendar.getInstance().getTimeInMillis()),
                    ".jpeg",                     /* suffix */
                    storageDir                   /* directory */
            );


            FileOutputStream writeStream = new FileOutputStream(imageFile);

            b.compress(Bitmap.CompressFormat.JPEG, 100, writeStream);
            writeStream.flush();
            writeStream.close();
            Toast.makeText(activity,"Image saved",Toast.LENGTH_SHORT).show();

            addPicToGallery(imageFile);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void addPicToGallery(File imageFile) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
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
