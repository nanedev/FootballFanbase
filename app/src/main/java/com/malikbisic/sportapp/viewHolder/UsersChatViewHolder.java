package com.malikbisic.sportapp.viewHolder;

import android.content.Context;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;
import com.google.android.exoplayer2.C;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.SearchableCountry;
import com.malikbisic.sportapp.model.SvgDrawableTranscoder;
import com.squareup.picasso.Picasso;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by korisnik on 03/09/2017.
 */

public class UsersChatViewHolder extends ChildViewHolder {

    View view;
    TextView usernameUser;
    CircleImageView flagUser;
    CircleImageView profileImageUser;

    public UsersChatViewHolder(View itemView) {
        super(itemView);

        view = itemView;
        usernameUser = (TextView) view.findViewById(R.id.usernameUsers);
        flagUser = (CircleImageView) view.findViewById(R.id.flagUsers);
        profileImageUser = (CircleImageView) view.findViewById(R.id.profileUsers);
    }

    public void setUsername (String username){
        usernameUser.setText(username);
    }

    public void setFlag(Context ctx, String flag){

        GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;

        requestBuilder = Glide
                .with(ctx)
                .using(Glide.buildStreamModelLoader(Uri.class, ctx), InputStream.class)
                .from(Uri.class)
                .as(SVG.class)
                .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new FileToStreamDecoder<SVG>(new SearchableCountry.SvgDecoder()))
                .decoder(new SearchableCountry.SvgDecoder())
                .animate(android.R.anim.fade_in);


        Uri uri = Uri.parse(flag);
        requestBuilder
                // SVG cannot be serialized so it's not worth to cache it
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .load(uri)
                .into(flagUser);

    }

    public void setProfileImage(Context ctx, String proifleImage){
        Picasso.with(ctx).load(proifleImage).into(profileImageUser);
    }
}
