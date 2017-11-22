package com.malikbisic.sportapp.activity;

import android.annotation.SuppressLint;
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
import android.os.Handler;
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
import android.widget.Toast;

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
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.malikbisic.sportapp.adapter.MainPageAdapter;
import com.malikbisic.sportapp.classes.FreeUser;
import com.malikbisic.sportapp.classes.PremiumUsers;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

import me.leolin.shortcutbadger.ShortcutBadger;


public class MainPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, TextWatcher {


    private FirebaseAuth.AuthStateListener mAuthStateListener;
    static String uid;
    private TextView username;
    private TextView email;
    TextView nameSurname;
    private ImageView profile;
    CircleImageView countryHeader;
    CircleImageView clubHeader;
    DatabaseReference mUsersReference;
    FirebaseAuth mAuth;
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
    public static String country;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    Date nowDate;
    DateFormat nowDateFormat;
    DateFormat trialDateFormat;
    String trialDateString;
    Date trialDate;
    DateFormat dateFormat;
    Calendar calendar;
    private static final int PHOTO_OPEN = 1;
    private static final int PHOTO_OPEN_ON_OLDER_PHONES = 3;
    private static final int VIDEO_OPEN = 2;
    boolean pause_state;
    boolean play_state;
    TextView loadMorePress;

    Date currentDateOfUserMainPage;
    String getDateFromDatabaseMainPage;
    boolean like_process = false;
    boolean dislike_process = false;
    LinearLayoutManager linearLayoutManager;
    DatabaseReference profileUsers;
    UsersModel model;
    DatabaseReference mUsers;
    boolean isPremium;
    static boolean isNotificationClicked = false;
    public static String myClubName;

    TextView notificationCounterNumber;
    DatabaseReference notificationReference;
    NavigationView navigationView;

    List<Post> itemSize = new ArrayList<>();
    MainPageAdapter adapter;
    public static String postKey;


    private static final int TOTAL_ITEM_LOAD = 5;
    int itemPosPremium = 0;
    int itemPosFree = 0;
    String lastkeyPremium = "";
    String mPreviousKeyPremium = "";
    String lastkeyFree = "";
    int br = 0;
    private int loaditem;
    private String firstKey = "";
    protected Handler handler;
    private int countItem;
    boolean firstTimeOpened = true;

    PremiumUsers premiumUsers;
    FreeUser freeUser;

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
        handler = new Handler();
        premiumUsers = new PremiumUsers();
        freeUser = new FreeUser();

        mUsersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();
        mUsersReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {

                    String clubName = String.valueOf(dataSnapshot.child("favoriteClub").getValue());

                    mUsers = FirebaseDatabase.getInstance().getReference().child("UsersChat").child(clubName).child(mAuth.getCurrentUser().getUid());
                    mUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mUsers.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
                            mUsers.child("online").setValue("true");


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        final String action = intent.getAction();

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
                                userProfileImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                                userProfileImage.setAlpha(0.9f);

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

                                DatabaseReference setOfline = FirebaseDatabase.getInstance().getReference().child("UsersChat").child(myClubName).child(uid);
                                setOfline.child("online").setValue(true);


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


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }


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

        String useram = intent.getStringExtra("username");

        if (useram != null) {
            Log.i("chatUsername", intent.getStringExtra("username"));
            Log.i("chatflag", intent.getStringExtra("flag"));
            Log.i("chatdate", intent.getStringExtra("date"));
        }

        FirebaseUser user = mAuth.getCurrentUser();
        final String myUserId = user.getUid();

        Boolean isFirstTime = getSharedPreferences("check first time", MODE_PRIVATE).getBoolean("isFirstTime", true);

        if (isFirstTime){
        setNumberClubFans();
            getSharedPreferences("check first time", MODE_PRIVATE).edit().putBoolean("isFirstTime", false).commit();
        }


    }

    public void setNumberClubFans() {

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final UsersModel model = dataSnapshot.getValue(UsersModel.class);

                final String myClub = model.getFavoriteClub();

                Log.e("MY CLUB", myClub);
                final DatabaseReference clubReference = FirebaseDatabase.getInstance().getReference().child("ClubTable").child(myClub);
                clubReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(myClub).exists()) {
                            Log.e("numbersFan root", String.valueOf(dataSnapshot.getRef()));
                            ClubTable modelclub = dataSnapshot.getValue(ClubTable.class);
                            int numbersFans = modelclub.getNumbersFans();
                            int addNewFan = numbersFans + 1;

                            Map setClubNumbers = new HashMap();
                            setClubNumbers.put("clubName", model.getFavoriteClub());
                            setClubNumbers.put("clubLogo", model.getFavoriteClubLogo());
                            setClubNumbers.put("numbersFans", addNewFan);
                            clubReference.updateChildren(setClubNumbers, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError != null) {
                                        Log.e("numbersFan Error", databaseError.getMessage());
                                    }
                                }
                            });
                            Log.i("numbersFans", String.valueOf(numbersFans));
                        } else {
                            Map setClubNumbers = new HashMap();
                            setClubNumbers.put("clubName", model.getFavoriteClub());
                            setClubNumbers.put("clubLogo", model.getFavoriteClubLogo());
                            setClubNumbers.put("numbersFans", 1);
                            clubReference.updateChildren(setClubNumbers, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError != null) {
                                        Log.e("numbersFan Error", databaseError.getMessage());
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == PHOTO_OPEN || requestCode == PHOTO_OPEN_ON_OLDER_PHONES && resultCode == RESULT_OK) {

                Uri imageUri = data.getData();
                Intent goToAddPhotoOrVideo = new Intent(MainPage.this, AddPhotoOrVideo.class);
                goToAddPhotoOrVideo.setData(imageUri);
                goToAddPhotoOrVideo.putExtra("username", usernameInfo);
                goToAddPhotoOrVideo.putExtra("profileImage", profielImage);
                goToAddPhotoOrVideo.putExtra("country", country);
                goToAddPhotoOrVideo.putExtra("clubheader", clubHeaderString);
                startActivity(goToAddPhotoOrVideo);
                Log.i("uri photo", String.valueOf(imageUri));

            } else if (requestCode == VIDEO_OPEN && resultCode == RESULT_OK) {
                Uri videoUri = data.getData();
                Intent goToAddPhotoOrVideo = new Intent(MainPage.this, AddPhotoOrVideo.class);
                goToAddPhotoOrVideo.setData(videoUri);
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
        } else if (cn.jzvd.JZVideoPlayerStandard.backPress()) {
            return;
        }
        finish();
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
            String key = newPost.getKey();

            Map textMap = new HashMap();
            textMap.put("desc", textPost);
            textMap.put("username", MainPage.usernameInfo);
            textMap.put("profileImag", MainPage.profielImage);
            textMap.put("uid", mAuth.getCurrentUser().getUid());
            textMap.put("country", country);
            textMap.put("clubLogo", clubLogo);
            textMap.put("favoritePostClub", MainPage.myClubName);
            textMap.put("key", key);
            newPost.updateChildren(textMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e("textError", databaseError.getMessage().toString());
                    } else {
                        postingDialog.dismiss();
                        postText.setText("");
                    }
                }
            });

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
                    replace(R.id.mainpage_fragment, profileFragment, profileFragment.getTag())
                    .commit();
        } else if (id == R.id.nav_news) {

            Intent openNEWS = new Intent(MainPage.this, NewsActivity.class);
            startActivity(openNEWS);

        } else if (id == R.id.nav_message) {

            Intent intent = new Intent(MainPage.this, ChatActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_football) {
            Intent intent = new Intent(MainPage.this, FootballActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_notifications) {
            NotificationFragment notificationFragment = new NotificationFragment();

            FragmentTransaction manager = getSupportFragmentManager().beginTransaction();

            manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                    R.anim.push_left_out, R.anim.push_left_out).
                    replace(R.id.mainpage_fragment, notificationFragment, notificationFragment.getTag())
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
            mUsers.child("online").setValue(ServerValue.TIMESTAMP);
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

                if (number == 0) {

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
        checkAllLoaded();


        FirebaseUser user = mAuth.getCurrentUser();
        final String myUserId = user.getUid();

        DatabaseReference checkPremiumUser = FirebaseDatabase.getInstance().getReference().child("Users").child(myUserId);
        checkPremiumUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                model = dataSnapshot.getValue(UsersModel.class);

                isPremium = model.isPremium();


                if (isPremium) {
                    premiumUsers.premiumUser(wallList, getApplicationContext(), MainPage.this);

                } else {
                    freeUser.freeUsers(wallList, getApplicationContext(), MainPage.this, model);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        firstTimeOpened = false;

    }

    private void checkAllLoaded() {

        postingDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                countItem = (int) dataSnapshot.getChildrenCount();
                loaditem++;
                if (loaditem == 1) {
                    firstKey = dataSnapshot.getKey();
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void premiumUsersLoadMore() {


        DatabaseReference newPostlload = FirebaseDatabase.getInstance().getReference().child("Posting");
        Query queryMorePremiumPost = newPostlload.orderByKey().endAt(lastkeyPremium).limitToLast(TOTAL_ITEM_LOAD);
        queryMorePremiumPost.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (!lastkeyPremium.equals(firstKey)) {


                    Post model = dataSnapshot.getValue(Post.class);

                    String getLastKey = dataSnapshot.getKey();
                    br = (int) dataSnapshot.getChildrenCount();
                    Log.e("br postova", String.valueOf(br));

                    if (!mPreviousKeyPremium.equals(getLastKey)) {
                        itemSize.add(itemPosPremium++, model);
                    } else {
                        mPreviousKeyPremium = getLastKey;
                    }

                    if (itemPosPremium == 1) {

                        lastkeyPremium = getLastKey;
                        Log.e("lastKey MORE METHOD", lastkeyPremium);
                    }


                    adapter.notifyItemInserted(itemSize.size());

                } else {
                    Toast.makeText(getApplicationContext(), "No more posts", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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
        cn.jzvd.JZVideoPlayerStandard.releaseAllVideos();
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
            if (Build.VERSION.SDK_INT < 19) {
                Intent openGallery = new Intent(Intent.ACTION_GET_CONTENT);
                photoSelected = true;
                openGallery.setType("image/*");
                startActivityForResult(openGallery, PHOTO_OPEN_ON_OLDER_PHONES);
            } else {
                photoSelected = true;
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, PHOTO_OPEN);

            }

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


    private  class HttpImageRequestTask extends AsyncTask<String, Void, Drawable> {


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
