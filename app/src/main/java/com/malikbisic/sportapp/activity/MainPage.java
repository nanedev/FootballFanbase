package com.malikbisic.sportapp.activity;

import android.annotation.TargetApi;
import android.app.*;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.caverock.androidsvg.SVG;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.malikbisic.sportapp.model.*;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.Notification;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import me.leolin.shortcutbadger.Badger;
import me.leolin.shortcutbadger.ShortcutBadgeException;
import me.leolin.shortcutbadger.ShortcutBadger;


public class MainPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, TextWatcher {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    static String uid;
    private TextView username;
    private TextView email;
    TextView nameSurname;
    String nameSurnameString;
    private ImageView profile;
    CircleImageView countryHeader;
    CircleImageView clubHeader;

    Bitmap logoBitmap;
    public static String clubHeaderString;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference, postingDatabase, likesReference, dislikeReference;

    private ImageView backgroundHeader;
    private ImageView userProfileImage;
    private TextView usernameuser;

    private ImageView galleryIcon;
    private TextView galleryText;
    private ImageView videoIcon;
    private TextView videoText;
    private ImageView audioIcon;
    private TextView audioText;
    private EditText postText;
    MenuItem myMenu;
    private ProgressDialog postingDialog;
    private RecyclerView wallList;
    public static boolean photoSelected;
    public static String usernameInfo;
    public static String profielImage;
    RelativeLayout backgroundUserPost;

    TextView numberofLikes;
    public static String country;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    Date nowDate;
    DateFormat nowDateFormat;
    DateFormat trialDateFormat;
    String trialDateString;
    Date trialDate;
    DateFormat dateFormat;
    String dateOfFirstLogin;
    private boolean pauseAudioPressed = false;
    private boolean audiofinished = false;
    private int pausePosition;
    Calendar calendar;
    private static final int PHOTO_OPEN = 1;
    private static final int VIDEO_OPEN = 2;
    boolean pause_state;
    boolean play_state;
    boolean stop_state;

    Date currentDateOfUserMainPage;
    String getDateFromDatabaseMainPage;
    boolean like_process = false;
    boolean dislike_process = false;

    LinearLayoutManager linearLayoutManager;

    int positionRec;

    DatabaseReference profileUsers;
    UsersModel model;

    boolean isPremium;
    static boolean isNotificationClicked = false;
    static  String myClubName;

    TextView notificationCounterNumber;
    DatabaseReference notificationReference;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_main_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        postingDatabase = FirebaseDatabase.getInstance().getReference().child("Posting");
        profileUsers = FirebaseDatabase.getInstance().getReference();
        notificationReference = FirebaseDatabase.getInstance().getReference().child("Notification");
        userProfileImage = (ImageView) findViewById(R.id.userProfilImage);
        usernameuser = (TextView) findViewById(R.id.user_username);
        galleryIcon = (ImageView) findViewById(R.id.gallery_icon_content_main);
        galleryText = (TextView) findViewById(R.id.galleryText);
        videoIcon = (ImageView) findViewById(R.id.vide_icon_content_main);
        videoText = (TextView) findViewById(R.id.videoText);
        audioIcon = (ImageView) findViewById(R.id.talk_icon_content_main);
        audioText = (TextView) findViewById(R.id.audioText);
        postText = (EditText) findViewById(R.id.postOnlyText);
        backgroundUserPost = (RelativeLayout) findViewById(R.id.relativeLayout);
        calendar = Calendar.getInstance();
        postingDialog = new ProgressDialog(this);
        wallList = (RecyclerView) findViewById(R.id.wall_rec_view);
        wallList.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        wallList.setLayoutManager(linearLayoutManager);
        wallList.setItemViewCacheSize(20);
        wallList.setDrawingCacheEnabled(true);
        wallList.setItemAnimator(new DefaultItemAnimator());
        trialDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        nowDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        nowDate = new Date();


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String action = intent.getAction();

        if (Intent.ACTION_SEND.equals(action)) {
            if (extras.containsKey(Intent.EXTRA_STREAM)) {

                Intent openPhoto = new Intent(MainPage.this, AddPhotoOrVideo.class);
                startActivity(openPhoto);
            }
        }
        postText.addTextChangedListener(this);
        postText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if (i == KeyEvent.KEYCODE_DEL) {
                }

                return false;
            }
        });


        galleryIcon.setOnClickListener(this);
        galleryText.setOnClickListener(this);
        videoIcon.setOnClickListener(this);
        videoText.setOnClickListener(this);
        audioIcon.setOnClickListener(this);
        audioText.setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        likesReference = mDatabase.getReference().child("Likes");
        dislikeReference = mDatabase.getReference().child("Dislikes");
        likesReference.keepSynced(true);
        dislikeReference.keepSynced(true);


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    uid = user.getUid();

                    Log.i("proba", uid);

                    mReference = mDatabase.getReference().child("Users").child(uid);
                    mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            if (dataSnapshot.exists()) {
                                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();


                                profielImage = String.valueOf(value.get("profileImage"));
                                Picasso.with(getApplicationContext())
                                        .load(profielImage)
                                        .into(profile);
                                username.setText(String.valueOf(value.get("username")));
                                usernameuser.setText(String.valueOf(value.get("username")));
                                usernameInfo = usernameuser.getText().toString().trim();
                                nameSurname.setText(String.valueOf(value.get("name") + " " + String.valueOf(value.get("surname"))));


                                Picasso.with(getApplicationContext())
                                        .load(profielImage)
                                        .into(userProfileImage);

                                country = String.valueOf(value.get("flag"));
                                clubHeaderString = String.valueOf(value.get("favoriteClubLogo"));
                                myClubName = String.valueOf(value.get("favoriteClub"));
                                Picasso.with(getApplicationContext())
                                        .load(clubHeaderString)
                                        .into(clubHeader);
                                getDateFromDatabaseMainPage = String.valueOf(value.get("premiumDate"));
                                try {
                                    currentDateOfUserMainPage = dateFormat.parse(getDateFromDatabaseMainPage);
                                    calendar.setTime(currentDateOfUserMainPage);

                                } catch (java.text.ParseException e) {
                                    e.printStackTrace();
                                }

                                calendar.add(Calendar.DAY_OF_MONTH, 15);
                                trialDate = calendar.getTime();
                                trialDateString = dateFormat.format(trialDate);
                                mReference.child("trialPremiumDate").setValue(trialDateString);


                                if (nowDate.equals(trialDate) || nowDate.after(trialDate)) {
                                    mReference.child("premium").setValue(false);
                                }

                                Log.i("country", country);

                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null)
                                    email.setText(user.getEmail());
                                backgroundImage();


                                // SVG cannot be serialized so it's not worth to cache it




/*
                                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Flags");
                                query.whereEqualTo("country", country);
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        if (e == null)
                                            for (ParseObject object : objects) {

                                                ParseFile file = (ParseFile) object.get("flag");
                                                file.getDataInBackground(new GetDataCallback() {
                                                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                                                    @Override
                                                    public void done(byte[] data, ParseException e) {
                                                        if (e == null) {


                                                            Bitmap bmp1 = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                            obwer = new BitmapDrawable(getResources(), bmp1);
                                                            layout.setBackground(obwer);
                                                        } else {
                                                            Log.d("test", "There was a problem downloading the data.");
                                                        }
                                                    }
                                                });
                                            }
                                    }
                                });*/
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }

                BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
                navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
                Menu menu = navigation.getMenu();
                MenuItem menuItem = menu.getItem(0);
                menuItem.setChecked(true);

            }
        };


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        profile = (ImageView) header.findViewById(R.id.nav_header_profil_image);
        username = (TextView) header.findViewById(R.id.header_username);
        email = (TextView) header.findViewById(R.id.header_email);
        countryHeader = (CircleImageView) header.findViewById(R.id.country_header_imageview);
        clubHeader = (CircleImageView) header.findViewById(R.id.clubheader_imageview);
        nameSurname = (TextView) header.findViewById(R.id.name_surname);
        backgroundHeader = (ImageView) header.findViewById(R.id.backgroundImgForHeader);

        notificationCounterNumber = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_notifications));


    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    break;
                case R.id.navigation_dashboard:
                    Intent openNews = new Intent(MainPage.this, NewsActivity.class);
                    startActivity(openNews);
                    break;
                case R.id.navigation_notifications:

                    return true;
            }
            return false;
        }

    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == PHOTO_OPEN && resultCode == RESULT_OK) {

                Uri imageUri = data.getData();
                Intent goToAddPhotoOrVideo = new Intent(MainPage.this, AddPhotoOrVideo.class);
                goToAddPhotoOrVideo.putExtra("image-uri_selected", imageUri.toString());
                goToAddPhotoOrVideo.putExtra("username", usernameInfo);
                goToAddPhotoOrVideo.putExtra("profileImage", profielImage);
                goToAddPhotoOrVideo.putExtra("country", country);
                goToAddPhotoOrVideo.putExtra("clubheader", clubHeaderString);
                startActivity(goToAddPhotoOrVideo);
                Log.i("uri photo", String.valueOf(imageUri));

            } else if (requestCode == VIDEO_OPEN && resultCode == RESULT_OK) {
                Uri videoUri = data.getData();
                Intent goToAddPhotoOrVideo = new Intent(MainPage.this, AddPhotoOrVideo.class);
                goToAddPhotoOrVideo.putExtra("video-uri_selected", videoUri.toString());
                goToAddPhotoOrVideo.putExtra("username", usernameInfo);
                goToAddPhotoOrVideo.putExtra("profileImage", profielImage);
                goToAddPhotoOrVideo.putExtra("country", country);
                goToAddPhotoOrVideo.putExtra("clubheader", clubHeaderString);
                Log.i("uri video", String.valueOf(videoUri));
                startActivity(goToAddPhotoOrVideo);


            } else {

                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        myMenu = menu.findItem(R.id.postText);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.postText) {
            String textPost = postText.getText().toString().trim();
            final String country = MainPage.country;
            final String clubLogo = MainPage.clubHeaderString;
            postingDialog.setMessage("Posting...");
            postingDialog.show();
            DatabaseReference newPost = postingDatabase.push();
            newPost.child("desc").setValue(textPost);
            newPost.child("username").setValue(MainPage.usernameInfo);
            newPost.child("profileImage").setValue(MainPage.profielImage);
            newPost.child("uid").setValue(mAuth.getCurrentUser().getUid());
            newPost.child("country").setValue(country);
            newPost.child("clubLogo").setValue(clubLogo);
            postingDialog.dismiss();
            postText.setText("");
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
            ProfileFragment profileFragment = new ProfileFragment();

            FragmentTransaction manager = getSupportFragmentManager().beginTransaction();

            manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                    R.anim.push_left_out, R.anim.push_left_out).
                    replace(R.id.mainpage_fragment, profileFragment, profileFragment.getTag()).addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_message) {

        } else if (id == R.id.nav_notifications) {
            NotificationFragment notificationFragment = new NotificationFragment();

            FragmentTransaction manager = getSupportFragmentManager().beginTransaction();

            manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                    R.anim.push_left_out, R.anim.push_left_out).
                    replace(R.id.mainpage_fragment, notificationFragment, notificationFragment.getTag()).addToBackStack(null)
                    .commit();

            isNotificationClicked = true;


            final DatabaseReference setSeen = FirebaseDatabase.getInstance().getReference().child("Notification").child(uid);


            setSeen.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.child("seen").getRef().setValue(true);
                        isNotificationClicked = false;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else if (id == R.id.premium_account) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent goToLogin = new Intent(MainPage.this, LoginActivity.class);
            goToLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(goToLogin);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void launcerCounter() {

        FirebaseUser user = mAuth.getCurrentUser();
        final String myUserId = user.getUid();
        DatabaseReference getNumberNotification = notificationReference.child(myUserId);

        Query query = getNumberNotification.orderByChild("seen").equalTo(false);

        query.addValueEventListener(new ValueEventListener() {
            @TargetApi(Build.VERSION_CODES.N_MR1)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int number = (int) dataSnapshot.getChildrenCount();

                if (number == 0){

                    ShortcutBadger.removeCount(getApplicationContext());


                } else {
                    startService(
                            new Intent(MainPage.this, BadgeServices.class).putExtra("badgeCount", number)
                   );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Intent intent2 = new Intent(Intent.ACTION_MAIN);
        intent2.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent2, PackageManager.MATCH_DEFAULT_ONLY);
        String currentHomePackage = resolveInfo.activityInfo.packageName;
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);


        initializeCountDrawer();
        launcerCounter();

        FirebaseUser user = mAuth.getCurrentUser();
        final String myUserId = user.getUid();


        DatabaseReference checkPremiumUser = FirebaseDatabase.getInstance().getReference().child("Users").child(myUserId);
        checkPremiumUser.addValueEventListener(new ValueEventListener() {
            @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                model = dataSnapshot.getValue(UsersModel.class);

                isPremium = model.isPremium();


                if (isPremium) {
                    premiumUsers();
                } else {
                    freeUser();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    public void premiumUsers() {


        FirebaseRecyclerAdapter<Post, MainPage.PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, MainPage.PostViewHolder>(
                Post.class,
                R.layout.wall_row,
                MainPage.PostViewHolder.class,
                postingDatabase
        ) {


            @Override
            protected void populateViewHolder(final MainPage.PostViewHolder viewHolder, final Post model, final int position) {
                final String post_key = getRef(position).getKey();
                final String link_post = getRef(position).toString();
                viewHolder.setDescForAudio(model.getDescForAudio());
                viewHolder.setDescForPhoto(model.getDescForPhoto());
                viewHolder.setDescVideo(model.getDescVideo());
                viewHolder.setProfileImage(getApplicationContext(), model.getProfileImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setPhotoPost(getApplicationContext(), model.getPhotoPost());
                viewHolder.setVideoPost(getApplicationContext(), model.getVideoPost());
                viewHolder.setAudioFile(getApplicationContext(), model.getAudioFile());
                viewHolder.setLikeBtn(post_key);
                viewHolder.setNumberLikes(post_key);
                viewHolder.setDesc(model.getDesc());
                viewHolder.setDislikeBtn(post_key);
                viewHolder.setNumberComments(post_key);
                viewHolder.setNumberDislikes(post_key);
                viewHolder.setClubLogo(getApplicationContext(), model.getClubLogo());
                viewHolder.setCountry(getApplicationContext(), model.getCountry());


                viewHolder.seekBar.setEnabled(true);
                viewHolder.play_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        viewHolder.mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                        try {
                            viewHolder.mPlayer.prepareAsync();
                            viewHolder.mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    viewHolder.mPlayer.start();

                                    viewHolder.seekBar.setMax(viewHolder.mPlayer.getDuration());

                                    new Timer().scheduleAtFixedRate(new TimerTask() {
                                        @Override
                                        public void run() {
                                            viewHolder.seekBar.setProgress(viewHolder.mPlayer.getCurrentPosition());
                                            viewHolder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                @Override
                                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                    if (fromUser) {
                                                        viewHolder.mPlayer.seekTo(progress);
                                                    }
                                                }

                                                @Override
                                                public void onStartTrackingTouch(SeekBar seekBar) {

                                                }

                                                @Override
                                                public void onStopTrackingTouch(SeekBar seekBar) {

                                                }
                                            });
                                        }
                                    }, 0, 100);

                                }
                            });


                            viewHolder.mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    Log.i("finished", "yes");

                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        play_state = true;
                        if (!pause_state) {
                            viewHolder.mPlayer.start();
                            pause_state = false;
                        }

                    }


                });


                viewHolder.pause_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pause_state = true;
                        play_state = false;
                        if (viewHolder.mPlayer.isPlaying() && pause_state)
                            viewHolder.mPlayer.pause();
                        pause_state = false;

                    }
                });

                viewHolder.stop_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (viewHolder.mPlayer != null) {
                            viewHolder.mPlayer.stop();
                            viewHolder.seekBar.setMax(0);


                        }
                    }
                });

                /*viewHolder.openSinglePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent openSinglePost = new Intent(MainPage.this, SinglePostViewActivity.class);
                        openSinglePost.putExtra("post_id", post_key);
                        startActivity(openSinglePost);
                        Log.i("linkPost", link_post);
                    }
                }); */


                viewHolder.numberofLikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent listUsername = new Intent(MainPage.this, Username_Likes_Activity.class);
                        listUsername.putExtra("post_key", post_key);
                        startActivity(listUsername);
                    }
                });

                viewHolder.numberOfDislikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent listUsername = new Intent(MainPage.this, Username_Dislikes_Activity.class);
                        listUsername.putExtra("post_key", post_key);
                        startActivity(listUsername);
                    }
                });


                viewHolder.like_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        like_process = true;


                        likesReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (like_process) {
                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                        likesReference.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        like_process = false;


                                    } else {

                                        final DatabaseReference newPost = likesReference.child(post_key).child(mAuth.getCurrentUser().getUid());

                                        newPost.child("username").setValue(MainPage.usernameInfo);
                                        newPost.child("photoProfile").setValue(MainPage.profielImage);

                                        DatabaseReference getIduserpost = postingDatabase;
                                        getIduserpost.child(post_key).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String userpostUID = String.valueOf(dataSnapshot.child("uid").getValue());

                                                DatabaseReference notifSet = notificationReference.child(userpostUID).push();
                                                notifSet.child("action").setValue("like");
                                                notifSet.child("uid").setValue(uid);
                                                notifSet.child("seen").setValue(false);
                                                notifSet.child("whatIS").setValue("post");
                                                notifSet.child("post_key").setValue(post_key);

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                        like_process = false;


                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                                Log.i("error", databaseError.getMessage());

                            }
                        });

                    }
                });


                viewHolder.post_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent openFullScreen = new Intent(MainPage.this, FullScreenImage.class);
                        String tag = (String) viewHolder.post_photo.getTag();
                        openFullScreen.putExtra("imageURL", tag);
                        startActivity(openFullScreen);
                    }
                });

                viewHolder.dislike_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dislike_process = true;
                        dislikeReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dislike_process) {
                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                        dislikeReference.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        dislike_process = false;


                                    } else {

                                        DatabaseReference newPost = dislikeReference.child(post_key).child(mAuth.getCurrentUser().getUid());

                                        newPost.child("username").setValue(MainPage.usernameInfo);
                                        newPost.child("photoProfile").setValue(MainPage.profielImage);

                                        DatabaseReference getIduserpost = postingDatabase;
                                        getIduserpost.child(post_key).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String userpostUID = String.valueOf(dataSnapshot.child("uid").getValue());

                                                DatabaseReference notifSet = notificationReference.child(userpostUID).push();
                                                notifSet.child("action").setValue("disliked");
                                                notifSet.child("uid").setValue(uid);
                                                notifSet.child("seen").setValue(false);
                                                notifSet.child("whatIS").setValue("post");
                                                notifSet.child("post_key").setValue(post_key);

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                        dislike_process = false;


                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

                viewHolder.comments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent openCom = new Intent(MainPage.this, CommentsActivity.class);
                        openCom.putExtra("keyComment", post_key);
                        openCom.putExtra("profileComment", MainPage.profielImage);
                        openCom.putExtra("username", MainPage.usernameInfo);
                        startActivity(openCom);
                    }
                });

                viewHolder.numberComments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent openCom = new Intent(MainPage.this, CommentsActivity.class);
                        openCom.putExtra("keyComment", post_key);
                        openCom.putExtra("profileComment", MainPage.profielImage);
                        openCom.putExtra("username", MainPage.usernameInfo);
                        startActivity(openCom);
                    }
                });


                viewHolder.openComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent openCom = new Intent(MainPage.this, CommentsActivity.class);
                        openCom.putExtra("keyComment", post_key);
                        openCom.putExtra("profileComment", MainPage.profielImage);
                        openCom.putExtra("username", MainPage.usernameInfo);
                        startActivity(openCom);
                    }
                });

                postingDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child(post_key).child("uid").exists()) {
                            if (mAuth.getCurrentUser().getUid().equals(dataSnapshot.child(post_key).child("uid").getValue().toString())) {

                                viewHolder.arrow_down.setVisibility(View.VISIBLE);

                                viewHolder.arrow_down.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        final String[] items = {"Edit post", "Delete post", "Cancel"};
                                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(viewHolder.mView.getContext());
                                        dialog.setItems(items, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                if (items[i].equals("Edit post")) {
                                                    Intent openSinglePost = new Intent(MainPage.this, SinglePostViewActivity.class);
                                                    openSinglePost.putExtra("post_id", post_key);
                                                    startActivity(openSinglePost);
                                                } else if (items[i].equals("Delete post")) {

                                                    postingDatabase.child(post_key).removeValue();
                                                } else if (items[i].equals("Cancel")) {


                                                }
                                            }
                                        });
                                        dialog.create();
                                        dialog.show();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                viewHolder.post_username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String username = viewHolder.post_username.getText().toString().trim();
                        Log.i("username", username);

                        profileUsers.child("Users").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                    final UsersModel userInfo = dataSnapshot1.getValue(UsersModel.class);

                                    String usernameFirebase = userInfo.getUsername();

                                    if (username.equals(usernameFirebase)) {
                                        final String uid = userInfo.getUserID();
                                        FirebaseUser user1 = mAuth.getCurrentUser();
                                        String myUID = user1.getUid();
                                        Log.i("myUID: ", myUID + ", iz baze uid: " + uid);

                                        if (uid.equals(myUID)) {

                                            ProfileFragment profileFragment = new ProfileFragment();

                                            FragmentTransaction manager = getSupportFragmentManager().beginTransaction();

                                            manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                                                    R.anim.push_left_out, R.anim.push_left_out).replace(R.id.mainpage_fragment, profileFragment, profileFragment.getTag()).addToBackStack(null).commit();
                                            Log.i("tacno", "true");

                                        } else {

                                            DatabaseReference profileInfo = profileUsers.child(uid);

                                            profileInfo.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {


                                                    Intent openUserProfile = new Intent(MainPage.this, UserProfileActivity.class);
                                                    openUserProfile.putExtra("userID", uid);
                                                    startActivity(openUserProfile);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                viewHolder.post_profile_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String username = viewHolder.post_username.getText().toString().trim();
                        Log.i("username", username);

                        profileUsers.child("Users").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                    final UsersModel userInfo = dataSnapshot1.getValue(UsersModel.class);

                                    String usernameFirebase = userInfo.getUsername();

                                    if (username.equals(usernameFirebase)) {
                                        final String uid = userInfo.getUserID();

                                        FirebaseUser user1 = mAuth.getCurrentUser();
                                        String myUID = user1.getUid();
                                        Log.i("myUID: ", myUID + ", iz baze uid: " + uid);

                                        if (uid.equals(myUID)) {

                                            ProfileFragment profileFragment = new ProfileFragment();

                                            FragmentTransaction manager = getSupportFragmentManager().beginTransaction();

                                            manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                                                    R.anim.push_left_out, R.anim.push_left_out).replace(R.id.mainpage_fragment, profileFragment, profileFragment.getTag()).addToBackStack(null).commit();
                                            Log.i("tacno", "true");

                                        } else {

                                            DatabaseReference profileInfo = profileUsers.child(uid);

                                            profileInfo.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {


                                                    Intent openUserProfile = new Intent(MainPage.this, UserProfileActivity.class);
                                                    openUserProfile.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                                                    openUserProfile.putExtra("userID", uid);
                                                    startActivity(openUserProfile);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

            }
        };

        wallList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    public void freeUser() {
        final String myClub = model.getFavoriteClub();

        DatabaseReference checkFavClub = FirebaseDatabase.getInstance().getReference().child("Posting");
        final Query query = checkFavClub
                .limitToFirst(10)
                .orderByChild("favoritePostClub")
                .startAt(myClub);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                FirebaseRecyclerAdapter<Post, PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                        Post.class,
                        R.layout.wall_row,
                        PostViewHolder.class,
                        query
                ) {


                    @Override
                    protected void populateViewHolder(final PostViewHolder viewHolder, final Post model, final int position) {
                        final String post_key = getRef(position).getKey();
                        final String link_post = getRef(position).toString();
                        viewHolder.setDescForAudio(model.getDescForAudio());
                        viewHolder.setDescForPhoto(model.getDescForPhoto());
                        viewHolder.setDescVideo(model.getDescVideo());
                        viewHolder.setProfileImage(getApplicationContext(), model.getProfileImage());
                        viewHolder.setUsername(model.getUsername());
                        viewHolder.setPhotoPost(getApplicationContext(), model.getPhotoPost());
                        viewHolder.setVideoPost(getApplicationContext(), model.getVideoPost());
                        viewHolder.setAudioFile(getApplicationContext(), model.getAudioFile());
                        viewHolder.setLikeBtn(post_key);
                        viewHolder.setNumberLikes(post_key);
                        viewHolder.setDesc(model.getDesc());
                        viewHolder.setDislikeBtn(post_key);
                        viewHolder.setNumberComments(post_key);
                        viewHolder.setNumberDislikes(post_key);
                        viewHolder.setClubLogo(getApplicationContext(), model.getClubLogo());


                        viewHolder.seekBar.setEnabled(true);
                        viewHolder.play_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {

                                viewHolder.mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                                try {
                                    viewHolder.mPlayer.prepareAsync();
                                    viewHolder.mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            viewHolder.mPlayer.start();

                                            viewHolder.seekBar.setMax(viewHolder.mPlayer.getDuration());

                                            new Timer().scheduleAtFixedRate(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    viewHolder.seekBar.setProgress(viewHolder.mPlayer.getCurrentPosition());
                                                    viewHolder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                        @Override
                                                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                            if (fromUser) {
                                                                viewHolder.mPlayer.seekTo(progress);
                                                            }
                                                        }

                                                        @Override
                                                        public void onStartTrackingTouch(SeekBar seekBar) {

                                                        }

                                                        @Override
                                                        public void onStopTrackingTouch(SeekBar seekBar) {

                                                        }
                                                    });
                                                }
                                            }, 0, 100);

                                        }
                                    });


                                    viewHolder.mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mediaPlayer) {
                                            Log.i("finished", "yes");

                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                play_state = true;
                                if (!pause_state) {
                                    viewHolder.mPlayer.start();
                                    pause_state = false;
                                }

                            }


                        });


                        viewHolder.pause_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pause_state = true;
                                play_state = false;
                                if (viewHolder.mPlayer.isPlaying() && pause_state)
                                    viewHolder.mPlayer.pause();
                                pause_state = false;

                            }
                        });

                        viewHolder.stop_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (viewHolder.mPlayer != null) {
                                    viewHolder.mPlayer.stop();
                                    viewHolder.seekBar.setMax(0);


                                }
                            }
                        });

                /*viewHolder.openSinglePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent openSinglePost = new Intent(MainPage.this, SinglePostViewActivity.class);
                        openSinglePost.putExtra("post_id", post_key);
                        startActivity(openSinglePost);
                        Log.i("linkPost", link_post);
                    }
                }); */

                        viewHolder.numberofLikes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent listUsername = new Intent(MainPage.this, Username_Likes_Activity.class);
                                listUsername.putExtra("post_key", post_key);
                                startActivity(listUsername);
                            }
                        });

                        viewHolder.numberOfDislikes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent listUsername = new Intent(MainPage.this, Username_Dislikes_Activity.class);
                                listUsername.putExtra("post_key", post_key);
                                startActivity(listUsername);
                            }
                        });


                        viewHolder.like_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                like_process = true;


                                likesReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (like_process) {
                                            if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                                likesReference.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                                like_process = false;


                                            } else {

                                                DatabaseReference newPost = likesReference.child(post_key).child(mAuth.getCurrentUser().getUid());

                                                newPost.child("username").setValue(MainPage.usernameInfo);
                                                newPost.child("photoProfile").setValue(MainPage.profielImage);
                                                like_process = false;

                                                DatabaseReference getIduserpost = postingDatabase;
                                                getIduserpost.child(post_key).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String userpostUID = String.valueOf(dataSnapshot.child("uid").getValue());

                                                        DatabaseReference notifSet = notificationReference.child(userpostUID).push();
                                                        notifSet.child("action").setValue("like");
                                                        notifSet.child("uid").setValue(uid);
                                                        notifSet.child("seen").setValue(false);
                                                        notifSet.child("whatIS").setValue("post");
                                                        notifSet.child("post_key").setValue(post_key);

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });


                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                        Log.i("error", databaseError.getMessage());

                                    }
                                });

                            }
                        });


                        viewHolder.post_photo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent openFullScreen = new Intent(MainPage.this, FullScreenImage.class);
                                String tag = (String) viewHolder.post_photo.getTag();
                                openFullScreen.putExtra("imageURL", tag);
                                startActivity(openFullScreen);
                            }
                        });

                        viewHolder.dislike_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dislike_process = true;
                                dislikeReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dislike_process) {
                                            if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                                dislikeReference.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                                dislike_process = false;


                                            } else {

                                                DatabaseReference newPost = dislikeReference.child(post_key).child(mAuth.getCurrentUser().getUid());

                                                newPost.child("username").setValue(MainPage.usernameInfo);
                                                newPost.child("photoProfile").setValue(MainPage.profielImage);

                                                dislike_process = false;

                                                DatabaseReference getIduserpost = postingDatabase;
                                                getIduserpost.child(post_key).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String userpostUID = String.valueOf(dataSnapshot.child("uid").getValue());

                                                        DatabaseReference notifSet = notificationReference.child(userpostUID).push();
                                                        notifSet.child("action").setValue("like");
                                                        notifSet.child("uid").setValue(uid);
                                                        notifSet.child("seen").setValue(false);
                                                        notifSet.child("whatIS").setValue("post");
                                                        notifSet.child("post_key").setValue(post_key);


                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });


                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        });

                        viewHolder.comments.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent openCom = new Intent(MainPage.this, CommentsActivity.class);
                                openCom.putExtra("keyComment", post_key);
                                openCom.putExtra("profileComment", MainPage.profielImage);
                                startActivity(openCom);
                            }
                        });

                        viewHolder.numberComments.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent openCom = new Intent(MainPage.this, CommentsActivity.class);
                                openCom.putExtra("keyComment", post_key);
                                openCom.putExtra("profileComment", MainPage.profielImage);
                                startActivity(openCom);
                            }
                        });


                        viewHolder.openComment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent openCom = new Intent(MainPage.this, CommentsActivity.class);
                                openCom.putExtra("keyComment", post_key);
                                openCom.putExtra("profileComment", MainPage.profielImage);
                                startActivity(openCom);
                            }
                        });

                        postingDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.child(post_key).child("uid").exists()) {
                                    if (mAuth.getCurrentUser().getUid().equals(dataSnapshot.child(post_key).child("uid").getValue().toString())) {
                                        viewHolder.arrow_down.setVisibility(View.VISIBLE);

                                        viewHolder.arrow_down.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                final String[] items = {"Edit post", "Delete post", "Cancel"};
                                                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(viewHolder.mView.getContext());
                                                dialog.setItems(items, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        if (items[i].equals("Edit post")) {
                                                            Intent openSinglePost = new Intent(MainPage.this, SinglePostViewActivity.class);
                                                            openSinglePost.putExtra("post_id", post_key);
                                                            startActivity(openSinglePost);
                                                        } else if (items[i].equals("Delete post")) {

                                                            postingDatabase.child(post_key).removeValue();
                                                        } else if (items[i].equals("Cancel")) {


                                                        }
                                                    }
                                                });
                                                dialog.create();
                                                dialog.show();
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        viewHolder.post_username.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String username = viewHolder.post_username.getText().toString().trim();
                                Log.i("username", username);

                                profileUsers.child("Users").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                            final UsersModel userInfo = dataSnapshot1.getValue(UsersModel.class);

                                            String usernameFirebase = userInfo.getUsername();

                                            if (username.equals(usernameFirebase)) {
                                                final String uid = userInfo.getUserID();

                                                FirebaseUser user1 = mAuth.getCurrentUser();
                                                String myUID = user1.getUid();
                                                Log.i("myUID: ", myUID + ", iz baze uid: " + uid);

                                                if (uid.equals(myUID)) {

                                                    ProfileFragment profileFragment = new ProfileFragment();

                                                    FragmentTransaction manager = getSupportFragmentManager().beginTransaction();

                                                    manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                                                            R.anim.push_left_out, R.anim.push_left_out).replace(R.id.mainpage_fragment, profileFragment, profileFragment.getTag()).commit();
                                                    Log.i("tacno", "true");

                                                } else {

                                                    DatabaseReference profileInfo = profileUsers.child(uid);

                                                    profileInfo.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                                            Intent openUserProfile = new Intent(MainPage.this, UserProfileActivity.class);
                                                            openUserProfile.putExtra("userID", uid);
                                                            startActivity(openUserProfile);
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

                        viewHolder.post_profile_image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String username = viewHolder.post_username.getText().toString().trim();
                                Log.i("username", username);

                                profileUsers.child("Users").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                            final UsersModel userInfo = dataSnapshot1.getValue(UsersModel.class);

                                            String usernameFirebase = userInfo.getUsername();

                                            if (username.equals(usernameFirebase)) {
                                                final String uid = userInfo.getUserID();

                                                FirebaseUser user1 = mAuth.getCurrentUser();
                                                String myUID = user1.getUid();
                                                Log.i("myUID: ", myUID + ", iz baze uid: " + uid);

                                                if (uid.equals(myUID)) {

                                                    ProfileFragment profileFragment = new ProfileFragment();

                                                    FragmentTransaction manager = getSupportFragmentManager().beginTransaction();

                                                    manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                                                            R.anim.push_left_out, R.anim.push_left_out).replace(R.id.mainpage_fragment, profileFragment, profileFragment.getTag()).addToBackStack(null).commit();
                                                    Log.i("tacno", "true");

                                                } else {

                                                    DatabaseReference profileInfo = profileUsers.child(uid);

                                                    profileInfo.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {


                                                            Intent openUserProfile = new Intent(MainPage.this, UserProfileActivity.class);
                                                            openUserProfile.putExtra("userID", uid);
                                                            startActivity(openUserProfile);
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

                    }
                };


                wallList.setAdapter(firebaseRecyclerAdapter);
                firebaseRecyclerAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void initializeCountDrawer() {
        //Gravity property aligns the text

        FirebaseUser user = mAuth.getCurrentUser();
        final String myUserId = user.getUid();
        notificationCounterNumber.setGravity(Gravity.CENTER_VERTICAL);
        notificationCounterNumber.setTypeface(null, Typeface.BOLD);
        notificationCounterNumber.setTextColor(getResources().getColor(R.color.redError));

        notificationCounterNumber.setGravity(Gravity.CENTER_VERTICAL);


        DatabaseReference getNumberNotification = notificationReference.child(myUserId);

        Query query = getNumberNotification.orderByChild("seen").equalTo(false);

        query.addValueEventListener(new ValueEventListener() {
            @TargetApi(Build.VERSION_CODES.N_MR1)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int number = (int) dataSnapshot.getChildrenCount();

                if (number == 0) {
                    notificationCounterNumber.setText("");
//                    ShortcutBadger.removeCount(MainPage.this);


                } else {
                    notificationCounterNumber.setText("" + number);
//                    startService(
//                            new Intent(MainPage.this, BadgeServices.class).putExtra("badgeCount", number)
//                    );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
//        String currentHomePackage = resolveInfo.activityInfo.packageName;


    }


    public static class PostViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public Button play_button;
        public MediaPlayer mPlayer;
        JCVideoPlayerStandard videoView;
        ImageView post_photo;
        MediaController mediaController;
        RelativeLayout audioLayout;
        ProgressDialog progressDialog;
        public Button pause_button;
        public SeekBar seekBar;
        public Button stop_button;
        ImageView like_button;
        ImageView dislike_button;
        FirebaseDatabase database;
        DatabaseReference likeReference, dislikeReference;
        DatabaseReference userReference;
        DatabaseReference numberCommentsReference;
        FirebaseAuth mAuth;
        public TextView numberofLikes;
        public TextView numberOfDislikes;
        RelativeLayout layoutPhoto, layoutPhotoText, layoutAudioText, layoutVideoText, single_post_layout;
        FrameLayout layoutVideo;
        ProgressBar loadPhoto;
        TextView openSinglePost;
        ImageView arrow_down;
        TextView openComment;
        TextView post_username;
        ImageView post_profile_image;
        RelativeLayout layoutForPost;
        RelativeLayout backgroundImage;
        ImageView postBackgroundImage;
        CircleImageView post_clubLogo;
        TextView comments;
        TextView numberComments;
        String logo;
        String usersFavClub;
        String myClub;
        boolean isPremium;


        public PostViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            play_button = (Button) mView.findViewById(R.id.play_button);
            pause_button = (Button) mView.findViewById(R.id.pause_button);
            stop_button = (Button) mView.findViewById(R.id.stop_button);

            mediaController = new MediaController(mView.getContext());

            videoView = (JCVideoPlayerStandard) mView.findViewById(R.id.posted_video);
            post_photo = (ImageView) mView.findViewById(R.id.posted_image);
            audioLayout = (RelativeLayout) mView.findViewById(R.id.layout_for_audio_player);
            mPlayer = new MediaPlayer();
            progressDialog = new ProgressDialog(mView.getContext());
            seekBar = (SeekBar) mView.findViewById(R.id.audio_seek_bar);
            like_button = (ImageView) mView.findViewById(R.id.like_button);
            dislike_button = (ImageView) mView.findViewById(R.id.dislike_button);
            numberofLikes = (TextView) mView.findViewById(R.id.number_of_likes);
            numberOfDislikes = (TextView) mView.findViewById(R.id.number_of_dislikes);
            single_post_layout = (RelativeLayout) mView.findViewById(R.id.layout_for_only_post);

            arrow_down = (ImageView) mView.findViewById(R.id.down_arrow);
            layoutPhotoText = (RelativeLayout) mView.findViewById(R.id.layout_for_text_image);
            layoutPhoto = (RelativeLayout) mView.findViewById(R.id.layout_for_image);
            layoutAudioText = (RelativeLayout) mView.findViewById(R.id.layout_audio_textview);
            layoutVideo = (FrameLayout) mView.findViewById(R.id.framelayout);
            //loadPhoto = (ProgressBar) mView.findViewById(R.id.post_photo_bar_load);
            layoutVideoText = (RelativeLayout) mView.findViewById(R.id.layout_for_video_text);
            database = FirebaseDatabase.getInstance();
            likeReference = database.getReference().child("Likes");
            dislikeReference = database.getReference().child("Dislikes");
            userReference = database.getReference().child("Users");
            numberCommentsReference = database.getReference().child("Comments");
            mAuth = FirebaseAuth.getInstance();
            openComment = (TextView) mView.findViewById(R.id.comment_something);
            post_username = (TextView) mView.findViewById(R.id.username_wall);
            post_profile_image = (ImageView) mView.findViewById(R.id.profile_image_wall);
            post_clubLogo = (CircleImageView) mView.findViewById(R.id.clubLogoPost);
            postBackgroundImage = (ImageView) mView.findViewById(R.id.image_post_background);
            comments = (TextView) mView.findViewById(R.id.comments_textview);
            numberComments = (TextView) mView.findViewById(R.id.number_comments);
            likeReference.keepSynced(true);
            dislikeReference.keepSynced(true);


        }

        public void checkPremium() {


        }

        public void onClick(View view) {


        }

        public void setNumberLikes(String post_key) {

            likeReference.child(post_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    long numberLikes = dataSnapshot.getChildrenCount();
                    if (numberLikes == 0) {
                        numberofLikes.setText("");
                    } else {
                        numberofLikes.setText(String.valueOf(numberLikes));
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        public void setNumberComments(String post_key) {

            numberCommentsReference.child(post_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    long numberOfComments = dataSnapshot.getChildrenCount();

                    if (numberOfComments == 0) {

                        comments.setVisibility(View.GONE);
                        numberComments.setText("");
                    } else if (numberOfComments == 1) {

                        comments.setText("Comment");
                        numberComments.setText(String.valueOf(numberOfComments));
                    } else {
                        comments.setText("Comments");
                        numberComments.setText(String.valueOf(numberOfComments));

                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        public void setLikeBtn(final String post_key) {
            likeReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                        dislike_button.setClickable(false);
                        like_button.setActivated(true);

                    } else {
                        dislike_button.setClickable(true);
                        like_button.setActivated(false);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setNumberDislikes(String post_key) {

            dislikeReference.child(post_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Log.i("fffff", String.valueOf(dataSnapshot.getChildrenCount()));
                    long numberDislikes = dataSnapshot.getChildrenCount();
                    if (numberDislikes == 0) {
                        numberOfDislikes.setText("");
                    } else {
                        numberOfDislikes.setText(String.valueOf(numberDislikes));
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        public void setDislikeBtn(final String post_key) {
            dislikeReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                        dislike_button.setActivated(true);
                        like_button.setClickable(false);

                    } else {
                        dislike_button.setActivated(false);
                        like_button.setClickable(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        public void setDesc(String desc) {
            if (desc != null) {
                TextView single_post = (TextView) mView.findViewById(R.id.post_text_main_page);
                single_post_layout.setVisibility(View.VISIBLE);
                single_post.setText(desc);
            } else {
                single_post_layout.setVisibility(View.GONE);
            }
        }


        public void setDescForAudio(String descForAudio) {

            TextView post_desc_for_audio = (TextView) mView.findViewById(R.id.audio_textview);
            if (descForAudio != null) {
                layoutAudioText.setVisibility(View.VISIBLE);
                post_desc_for_audio.setText(descForAudio);
            } else {
                layoutAudioText.setVisibility(View.GONE);
                post_desc_for_audio.setVisibility(View.GONE);
            }
        }

        public void setDescForPhoto(String descForPhoto) {

            TextView post_desc_for_photo = (TextView) mView.findViewById(R.id.text_for_image);

            if (descForPhoto != null) {
                layoutPhotoText.setVisibility(View.VISIBLE);
                post_desc_for_photo.setText(descForPhoto);
            } else {
                layoutPhotoText.setVisibility(View.GONE);
            }
        }

        public void setDescVideo(String descVideo) {
            TextView post_desc_for_video = (TextView) mView.findViewById(R.id.text_for_video);

            if (descVideo != null) {
                layoutVideoText.setVisibility(View.VISIBLE);
                post_desc_for_video.setText(descVideo);
            } else {
                layoutVideoText.setVisibility(View.GONE);
            }

        }


        public void setUsername(String username) {

            post_username.setText(username);


        }

        public void setProfileImage(Context ctx, String profileImage) {

            Picasso.with(ctx).load(profileImage).into(post_profile_image);

        }

        public void setClubLogo(Context ctx, String clubLogo) {

            Picasso.with(ctx).load(clubLogo).into(post_clubLogo);
        }


        public void setCountry(Context ctx, String country) {
            if (country != null) {
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


                Uri uri = Uri.parse(country);
                requestBuilder
                        // SVG cannot be serialized so it's not worth to cache it
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .load(uri)
                        .into(postBackgroundImage);
                postBackgroundImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                postBackgroundImage.setAlpha(0.4f);
            }
        }


        public void setPhotoPost(Context ctx, String photoPost) {

            if (photoPost != null) {
                layoutPhoto.setVisibility(View.VISIBLE);

                Glide.with(ctx)
                        .load(photoPost)
                        .asBitmap()
                        .override(720, 640)
                        .centerCrop()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                post_photo.setImageBitmap(resource);
                            }

                        });
//                Picasso.with(ctx).load(photoPost).into(post_photo, new Callback() {
//                    @Override
//                    public void onSuccess() {
//                        //     loadPhoto.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onError() {
//
//                    }
//                });
                post_photo.setTag(photoPost);
            } else {

                layoutPhoto.setVisibility(View.GONE);
                // loadPhoto.setVisibility(View.GONE);
            }

        }

        public void setVideoPost(Context ctx, String videoPost) {


            if (videoPost != null) {

                try {

                    layoutVideo.setVisibility(View.VISIBLE);
                    videoView.setUp(videoPost, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "proba");
                    videoView.requestFocus();
                } catch (Exception e) {
                    e.getMessage();
                }

            } else {
                layoutVideo.setVisibility(View.GONE);
            }
        }


        public void setAudioFile(Context context, String audioFile) {

            if (audioFile != null) {
                mPlayer.reset();
                audioLayout.setVisibility(View.VISIBLE);
                try {

                    mPlayer.setDataSource(context, Uri.parse(audioFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                audioLayout.setVisibility(View.GONE);
            }

        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            linearLayoutManager.onRestoreInstanceState(listState);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = linearLayoutManager.onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);


    }


    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }

        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = linearLayoutManager.onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.gallery_icon_content_main || view.getId() == R.id.galleryText) {
            Intent openGallery = new Intent(Intent.ACTION_GET_CONTENT);
            photoSelected = true;
            openGallery.setType("image/*");
            startActivityForResult(openGallery, PHOTO_OPEN);
        } else if (view.getId() == R.id.vide_icon_content_main || view.getId() == R.id.videoText) {
            Intent intent = new Intent();
            photoSelected = false;
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), VIDEO_OPEN);

        } else if (view.getId() == R.id.talk_icon_content_main || view.getId() == R.id.audioText) {

            Intent intent = new Intent(MainPage.this, RecordAudio.class);
            startActivity(intent);
        }

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    //neöto
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


        if (myMenu != null) {

            if (!postText.getText().toString().trim().isEmpty() && postText.getText().toString().trim().length() >= 3) {
                myMenu.setEnabled(true);
            } else if (postText.getText().toString().trim().length() < 3) {
                myMenu.setEnabled(false);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


    private class HttpImageRequestTask extends AsyncTask<String, Void, Drawable> {


        @Override
        protected Drawable doInBackground(String... params) {
            try {


                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                SVG svg = SVG.getFromInputStream(inputStream);
                Drawable drawable = new PictureDrawable(svg.renderToPicture());

                return drawable;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            // Update the view
            updateImageView(drawable);


        }

        private void updateImageView(Drawable drawable) {
            if (drawable != null) {

                // Try using your library and adding this layer type before switching your SVG parsing
                countryHeader.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                countryHeader.setImageDrawable(drawable);


            }
        }

    }

    public void backgroundImage() {
        HttpImageRequestTask imageRequestTask = new HttpImageRequestTask();

        try {
            imageRequestTask.execute(country).get();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }


}
