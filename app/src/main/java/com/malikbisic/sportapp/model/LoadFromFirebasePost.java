package com.malikbisic.sportapp.model;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.CommentsActivity;
import com.malikbisic.sportapp.activity.FullScreenImage;
import com.malikbisic.sportapp.activity.MainPage;
import com.malikbisic.sportapp.activity.ProfileFragment;
import com.malikbisic.sportapp.activity.SinglePostViewActivity;
import com.malikbisic.sportapp.activity.UserProfileActivity;
import com.malikbisic.sportapp.activity.Username_Dislikes_Activity;
import com.malikbisic.sportapp.activity.Username_Likes_Activity;

import java.util.Timer;
import java.util.TimerTask;



/**
 * Created by korisnik on 20/08/2017.
 */

public class LoadFromFirebasePost {


    DatabaseReference postingDatabase = FirebaseDatabase.getInstance().getReference().child("Posting");

}
