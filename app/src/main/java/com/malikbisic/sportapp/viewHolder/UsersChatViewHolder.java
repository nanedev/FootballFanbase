package com.malikbisic.sportapp.viewHolder;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.MainPage;
import com.malikbisic.sportapp.activity.SearchableCountry;
import com.malikbisic.sportapp.adapter.ClubNameChatAdapter;
import com.malikbisic.sportapp.model.SvgDrawableTranscoder;
import com.malikbisic.sportapp.model.UserChat;
import com.malikbisic.sportapp.model.UserChatGroup;
import com.squareup.picasso.Picasso;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by korisnik on 03/09/2017.
 */

public class UsersChatViewHolder extends ChildViewHolder {

    View view;
    public TextView usernameUser;
    CircleImageView flagUser;
    CircleImageView profileImageUser;
    public ImageView onlineImage;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    FirebaseAuth mAuth;
    String currentUserDate;
    UserChat chat;

    public UsersChatViewHolder(final View itemView) {
        super(itemView);

        view = itemView;
        usernameUser = (TextView) view.findViewById(R.id.usernameUsers);
        flagUser = (CircleImageView) view.findViewById(R.id.flagUsers);
        profileImageUser = (CircleImageView) view.findViewById(R.id.profileUsers);
        onlineImage = (ImageView) view.findViewById(R.id.onlineStatus);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("UsersChat");


    }

    public void setOnlineImage() {
        Log.i("online", String.valueOf(MainPage.myClubName));
        Log.i("online", String.valueOf(mAuth.getCurrentUser().getUid()));

    }

     public void setDate (String date){
         if (date != null)
         currentUserDate = date;

     }


        public void setUsername(String username) {
                usernameUser.setText(username);
            }

            public void setFlag(Context ctx, String flag) {

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

            public void setProfileImage(Context ctx, String proifleImage) {
                Picasso.with(ctx).load(proifleImage).into(profileImageUser);
            }
}

