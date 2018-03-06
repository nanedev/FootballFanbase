package com.malikbisic.sportapp.activity.firebase;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caverock.androidsvg.SVG;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.activity.BadgeServices;
import com.malikbisic.sportapp.activity.StopAppServices;
import com.malikbisic.sportapp.application.*;
import com.malikbisic.sportapp.listener.EndlessRecyclerViewScrollListener;
import com.malikbisic.sportapp.fragment.firebase.NotificationFragment;
import com.malikbisic.sportapp.listener.OnLoadMoreListener;
import com.malikbisic.sportapp.fragment.firebase.ProfileFragment;
import com.malikbisic.sportapp.activity.api.ChatActivity;
import com.malikbisic.sportapp.activity.api.FootballActivity;
import com.malikbisic.sportapp.adapter.firebase.MainPageAdapter;
import com.malikbisic.sportapp.classes.FreeUser;
import com.malikbisic.sportapp.classes.PremiumUsers;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.firebase.Post;
import com.malikbisic.sportapp.model.firebase.UsersModel;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

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
import java.util.concurrent.ExecutionException;

import cn.jzvd.JZVideoPlayerStandard;
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
    private FirebaseFirestore mReference, postingDatabase, likesReference, dislikeReference;
    private ImageView backgroundHeader;
    private ImageView userProfileImage;
    private TextView usernameuser;
    private RelativeLayout galleryIcon;
    private RelativeLayout videoIcon;
    private RelativeLayout audioIcon;
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
    ProgressDialog pDialog;

    TextView notificationCounterNumber;
    TextView messageCounterNumber;
    FirebaseFirestore notificationReference, chatNotificationReference;
    NavigationView navigationView;

    List<Post> itemSize = new ArrayList<>();
    MainPageAdapter adapter;


    private static final int TOTAL_ITEM_LOAD = 5;
    int itemPosPremium = 4;
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
    boolean fromMainPage=true;

    PremiumUsers premiumUsers;
    FreeUser freeUser;
    String postKey;
    ActionBarDrawerToggle toggle;
    int id;

    DocumentSnapshot lastVisible;
    DocumentSnapshot prevItemVisible;
    SwipeRefreshLayout swipeRefreshLayoutPost;
    int size;
    public static int CAMERA_REQUEST = 32;
    int numberLikes;
    int numberDisliks;
    int totalLikes, totalDislikes, pointsTotalCurrentMonth, pointsTotalPrevMonth;
    int currentScoreLike = 0;
    int prevMonthScoreLike = 0;
    int currentScoreDisike = 0;
    int prevMonthScoreDisike = 0;
    String lastMonthUpdate = "noData";
    String countryName;
    RelativeLayout captureImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_main_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent closeAPP = new Intent(this, StopAppServices.class);
        startService(closeAPP);

        postingDatabase = FirebaseFirestore.getInstance();
        //postingDatabase.collection("Posting");
        profileUsers = FirebaseDatabase.getInstance().getReference();
        userProfileImage = (ImageView) findViewById(R.id.userProfilImage);
        usernameuser = (TextView) findViewById(R.id.user_username);
        galleryIcon = (RelativeLayout) findViewById(R.id.gallery_icon_content_main);
        videoIcon = (RelativeLayout) findViewById(R.id.vide_icon_content_main);
        audioIcon = (RelativeLayout) findViewById(R.id.talk_icon_content_main);
        postText = (EditText) findViewById(R.id.postOnlyText);
        backgroundUserPost = (RelativeLayout) findViewById(R.id.relativeLayout);
        calendar = Calendar.getInstance();
        postingDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        wallList = (RecyclerView) findViewById(R.id.wall_rec_view);
        wallList.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //linearLayoutManager.setStackFromEnd(true);
        //linearLayoutManager.setReverseLayout(true);
        captureImage = (RelativeLayout) findViewById(R.id.takephotolayout);
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
        adapter = new MainPageAdapter(itemSize, getApplicationContext(), MainPage.this, wallList, postKey);
        wallList.setAdapter(adapter);
        swipeRefreshLayoutPost = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_post);
        itemSize.clear();


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
        videoIcon.setOnClickListener(this);
        audioIcon.setOnClickListener(this);

galleryIcon.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
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
    }
});
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        likesReference = FirebaseFirestore.getInstance();
        likesReference.collection("Likes");
        dislikeReference = FirebaseFirestore.getInstance();
        dislikeReference.collection("Dislikes");
        //likesReference.keepSynced(true);
        //dislikeReference.keepSynced(true);


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    uid = user.getUid();

                    Log.i("proba", uid);

                    final FirebaseFirestore mReferenceInfo = FirebaseFirestore.getInstance();
                    mReferenceInfo.collection("Users").document(uid)
                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                                    if (documentSnapshot.exists()) {
                                        Map<String, Object> value = (Map<String, Object>) documentSnapshot.getData();


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
                                        countryName = String.valueOf(value.get("country"));
                                        clubHeaderString = String.valueOf(value.get("favoriteClubLogo"));
                                        myClubName = String.valueOf(value.get("favoriteClub"));
                                        Picasso.with(getApplicationContext())
                                                .load(clubHeaderString)
                                                .into(clubHeader);
                                        getDateFromDatabaseMainPage = String.valueOf(value.get("premiumDate"));
                                        try {
                                            currentDateOfUserMainPage = dateFormat.parse(getDateFromDatabaseMainPage);
                                            calendar.setTime(currentDateOfUserMainPage);

                                        } catch (java.text.ParseException ee) {
                                            ee.printStackTrace();
                                        }

                                        Map onlie = new HashMap();
                                        onlie.put("online", true);


                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        db.collection("UsersChat").document(myClubName).collection("user-id").document(uid).update("online", "true");


                                        calendar.add(Calendar.DAY_OF_MONTH, 15);
                                        trialDate = calendar.getTime();
                                        trialDateString = dateFormat.format(trialDate);
                                        Map trialMap = new HashMap();
                                        trialMap.put("trialPremiumDate", trialDateString);

                                        FirebaseFirestore trialPremiumRef = FirebaseFirestore.getInstance();
                                        trialPremiumRef.collection("Users").document(uid).update(trialMap);

                               /* if (nowDate.equals(trialDate) || nowDate.after(trialDate)) {
                                    mReference.child("premium").setValue(false);
                                }*/

                                        Log.i("country", country);

                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null)
                                            email.setText(user.getEmail());
                                        backgroundImage();
                                        usersChatInfo();

                                    }
                                }
                            });


                }


            }
        };

captureImage.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }
});

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
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
        messageCounterNumber = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_message));

        String useram = intent.getStringExtra("username");

        if (useram != null) {
            Log.i("chatUsername", intent.getStringExtra("username"));
            Log.i("chatflag", intent.getStringExtra("flag"));
            Log.i("chatdate", intent.getStringExtra("date"));
        }

        FirebaseUser user = mAuth.getCurrentUser();
        final String myUserId = user.getUid();
        getSupportActionBar().show();

        Boolean isFirstTime = getSharedPreferences("check first time", MODE_PRIVATE).getBoolean("isFirstTime", true);

        if (isFirstTime) {
            setNumberClubFans();
            getSharedPreferences("check first time", MODE_PRIVATE).edit().putBoolean("isFirstTime", false).commit();
        }

        usersPoint(this,  mAuth.getCurrentUser().getUid());

        FirebaseFirestore iikkk = FirebaseFirestore.getInstance();
        iikkk.collection("UsersChat").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                Log.i("clubID", String.valueOf(documentSnapshots.getDocuments().size()));
                for (DocumentSnapshot snapshot : documentSnapshots.getDocuments()){
                    Log.i("clubID", snapshot.getId());
                }
            }
        });


    }

    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void usersChatInfo(){
      /*  final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

        Map<String,String> userChatInfo = new HashMap<>();
        userChatInfo.put("username", usernameInfo);
        userChatInfo.put("date", currentDate);
            userChatInfo.put("profileImage", profielImage);
        userChatInfo.put("country", countryName);
        userChatInfo.put("flag", country);
        userChatInfo.put("favoriteClub", myClubName);
        userChatInfo.put("favoriteClubLogo", clubHeaderString);
        userChatInfo.put("userID", uid);
        userChatInfo.put("online", "true");

        Map<String, Object> clubName = new HashMap<>();
        clubName.put("clubName", myClubName);
        FirebaseFirestore usersChat = FirebaseFirestore.getInstance();
        usersChat.collection("UsersChat").document(myClubName).set(clubName);
        usersChat.collection("UsersChat").document(myClubName).collection(uid).add(userChatInfo).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                Log.i("Successfully written",task.getResult().toString());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Error",e.getLocalizedMessage());
            }
        });*/
    }

    public void usersPoint(final Activity activity, final String uid) {
        currentScoreLike = 0;
        currentScoreDisike = 0;
        prevMonthScoreLike = 0;
        prevMonthScoreDisike = 0;
        pointsTotalCurrentMonth = 0;
        pointsTotalPrevMonth = 0;
        DateFormat currentDateFormat = new SimpleDateFormat("MMMM");
        final Date currentDate = new Date();
        Log.i("Month", currentDateFormat.format(currentDate));

        final String currentMonth = currentDateFormat.format(currentDate);

        DateTime prevDate = new DateTime().minusMonths(1);
        final String prevMonth = prevDate.toString("MMMM");


        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query usersPost = db.collection("Posting").whereEqualTo("uid", uid);
        usersPost.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {

            if (task.getException() == null){

                    for (final DocumentSnapshot snapshot : task.getResult()) {
                        final String postID = snapshot.getId();

                        Log.i("postID", postID);

                        CollectionReference likeNumber = db.collection("Likes").document(postID).collection("like-id");
                        likeNumber.get().addOnCompleteListener(activity, new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@Nullable Task<QuerySnapshot> task1) {

                                    DateFormat likeDateFormat = new SimpleDateFormat("MMMM",Locale.getDefault());
                                    for (DocumentSnapshot snap : task1.getResult()) {
                                        String documentuid = snap.getId();
                                        if (snap.getDate("timestamp") != null) {
                                            final Date likeDate = snap.getDate("timestamp");
                                            String likeMonth = likeDateFormat.format(likeDate);

                                            if (likeMonth.equals(currentMonth) && !documentuid.equals(uid)) {
                                                currentScoreLike++;
                                            } else if (likeMonth.equals(prevMonth) && !documentuid.equals(uid)) {
                                                prevMonthScoreLike++;
                                            }


                                        } else {
                                            Log.i("null", postID + " " + snap.getId());
                                        }
                                    }
                                    CollectionReference dislikeNumber = db.collection("Dislikes").document(postID).collection("dislike-id");
                                    dislikeNumber.get().addOnCompleteListener(activity, new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@Nullable Task<QuerySnapshot> task2) {


                                            if (task2.getException() == null) {
                                                numberDisliks = task2.getResult().size();
                                                DateFormat dislikeFormatDate = new SimpleDateFormat("MMMM");
                                                for (DocumentSnapshot snapshot1 : task2.getResult()) {
                                                    String docUID = snapshot1.getId();
                                                    if (snapshot1.getDate("timestamp") != null) {
                                                        final Date dislikeDate = snapshot1.getDate("timestamp");
                                                        String dislikeMonth = dislikeFormatDate.format(dislikeDate);

                                                        if (dislikeMonth.equals(currentMonth) && !docUID.equals(uid)) {
                                                            currentScoreDisike++;
                                                        } else if (dislikeMonth.equals(prevMonth) && !docUID.equals(uid)) {
                                                            prevMonthScoreDisike++;
                                                        }
                                                    } else {
                                                        Log.i("nullDIS", postID + " " + snapshot1.getId());
                                                    }
                                                }


                                                pointsTotalPrevMonth = prevMonthScoreLike - prevMonthScoreDisike;
                                                pointsTotalCurrentMonth = currentScoreLike - currentScoreDisike;
                                                if (pointsTotalPrevMonth < 0) {
                                                    pointsTotalPrevMonth = 0;
                                                }

                                                if (pointsTotalCurrentMonth < 0){
                                                    pointsTotalCurrentMonth = 0;
                                                }
                                                final FirebaseFirestore db = FirebaseFirestore.getInstance();

                                                Map<String, Object> currentPointsMap = new HashMap<>();
                                                currentPointsMap.put("likePoints", currentScoreLike);
                                                currentPointsMap.put("dislikePoints", currentScoreDisike);
                                                currentPointsMap.put("totalPoints", pointsTotalCurrentMonth);

                                                Map<String, Object> prevPointsMap = new HashMap<>();
                                                prevPointsMap.put("likePoints", prevMonthScoreLike);
                                                prevPointsMap.put("dislikePoints", prevMonthScoreDisike);
                                                prevPointsMap.put("totalPoints", pointsTotalPrevMonth);

                                                final Map<String, Object> pointsMap = new HashMap<>();
                                                pointsMap.put("uid", uid);
                                                pointsMap.put("prevMonthPoints", prevPointsMap);
                                                pointsMap.put("currentMonthPoints", currentPointsMap);
                                                pointsMap.put("prevMonthUpdate", prevMonth);

                                                final Map<String, Object> pointsMapCurrentMonth = new HashMap<>();
                                                pointsMapCurrentMonth.put("uid", uid);
                                                pointsMapCurrentMonth.put("currentMonthPoints", currentPointsMap);

                                                DocumentReference pointsColl = db.collection("Points").document(uid);
                                                pointsColl.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task4) {
                                                        if (task4.getResult().exists()) {
                                                            lastMonthUpdate = String.valueOf(task4.getResult().get("prevMonthUpdate"));
                                                        }
                                                            if (!lastMonthUpdate.equals(prevMonth)) {

                                                                if (task4.getResult().exists()) {
                                                                    DocumentReference ref = db.collection("Points").document(uid);
                                                                    ref.update(pointsMap).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.e("errUpdate", e.getLocalizedMessage());
                                                                        }
                                                                    });
                                                                } else {
                                                                    DocumentReference ref = db.collection("Points").document(uid);
                                                                    ref.set(pointsMap).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.e("errSet", e.getLocalizedMessage());
                                                                        }
                                                                    });
                                                                }
                                                            } else {
                                                                DocumentReference ref = db.collection("Points").document(uid);
                                                                ref.update(pointsMapCurrentMonth).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.e("errUpdateCurr", e.getLocalizedMessage());
                                                                    }
                                                                });
                                                            }
                                                        }
                                                });

                                            }
                                        }

                                });


                            }
                        });
                }


            }
        }
    });

}

    public void setNumberClubFans() {

        /*DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
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
        }); */
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


            } else    if (requestCode == CAMERA_REQUEST) {
                Bitmap image = null;
                Uri imageData = null;
                if (resultCode == RESULT_OK) {
                    image = (Bitmap) data.getExtras().get("data");
                    imageData = data.getData();
                    Intent openCameraSend = new Intent(MainPage.this, CaptureImageSendChatActivity.class);
                    openCameraSend.setData(imageData);
                    openCameraSend.putExtra("imagedata", image);
                    openCameraSend.putExtra("userIDFromMainPage", mAuth.getCurrentUser().getUid());
                    openCameraSend.putExtra("fromMainPage",fromMainPage);
                    openCameraSend.putExtra("username",usernameInfo);
                    openCameraSend.putExtra("profileImage",profielImage);
                    openCameraSend.putExtra("country",country);
                    openCameraSend.putExtra("clubHeader",clubHeaderString);
                    openCameraSend.putExtra("clubName",MainPage.myClubName);
                    openCameraSend.putExtra("postkey",postKey);
                    startActivityForResult(openCameraSend, 1);
                }
            }else {

                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        getSupportActionBar().show();
        navigationView.getMenu().findItem(id).setChecked(false);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (cn.jzvd.JZVideoPlayerStandard.backPress()) {
            return;
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.executePendingTransactions();
        if (fragmentManager.getBackStackEntryCount() < 1) {
            super.onBackPressed();
        } else {
            fragmentManager.executePendingTransactions();
            fragmentManager.popBackStack();
            fragmentManager.executePendingTransactions();
            if (fragmentManager.getBackStackEntryCount() < 1) {
                toggle.setDrawerIndicatorEnabled(true);
            }
        }

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


            Map<String, Object> textMap = new HashMap<>();
            textMap.put("desc", textPost);
            textMap.put("username", MainPage.usernameInfo);
            textMap.put("profileImage", MainPage.profielImage);
            textMap.put("uid", mAuth.getCurrentUser().getUid());
            textMap.put("country", country);
            textMap.put("time", FieldValue.serverTimestamp());
            textMap.put("clubLogo", clubLogo);
            textMap.put("favoritePostClub", MainPage.myClubName);
            postingDatabase.collection("Posting").add(textMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        String key = task.getResult().getId();
                        Map<String, Object> keyUpdate = new HashMap<>();
                        keyUpdate.put("key", key);
                        postingDatabase.collection("Posting").document(key).update(keyUpdate);
                        postingDialog.dismiss();
                        postText.setText("");
                    }
                }
            });


            /*newPost.updateChildren(textMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e("textError", databaseError.getMessage().toString());
                    } else {
                        postingDialog.dismiss();
                        postText.setText("");
                    }
                }
            }); */

            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        if (toggle.isDrawerIndicatorEnabled() &&
                toggle.onOptionsItemSelected(item)) {
            return true;
        }

        id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
  /*            ProfileFragment profileFragment = new ProfileFragment();

            FragmentTransaction manager = getSupportFragmentManager().beginTransaction();

            manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                    R.anim.push_left_out, R.anim.push_left_out).
                    replace(R.id.mainpage_fragment, profileFragment, profileFragment.getTag())
                    .addToBackStack(MainPage.class.getName())
                    .commit(); */

            Intent intent = new Intent(MainPage.this, ProfileFragment.class);
            startActivity(intent);
        } else if (id == R.id.nav_message) {

            Intent intent = new Intent(MainPage.this, ChatActivity.class);
            startActivity(intent);

            chatNotificationReference.collection("NotificationChat").document(mAuth.getCurrentUser().getUid()).collection("notif-id").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot snapshot : task.getResult()){
                        String docID = snapshot.getId();

                        chatNotificationReference.collection("NotificationChat").document(mAuth.getCurrentUser().getUid()).collection("notif-id").document(docID).update("seen", true);
                    }
                }
            });

        } else if (id == R.id.nav_football) {
            Intent intent = new Intent(MainPage.this, FootballActivity.class);
            startActivity(intent);

        }else if (id == R.id.nav_rankings){

            Intent intent = new Intent(MainPage.this,RankingsActivity.class);
            startActivity(intent);
            

        }else if (id == R.id.nav_notifications) {
            NotificationFragment notificationFragment = new NotificationFragment();

            FragmentTransaction manager = getSupportFragmentManager().beginTransaction();

            manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                    R.anim.push_left_out, R.anim.push_left_out).
                    replace(R.id.mainpage_fragment, notificationFragment, notificationFragment.getTag())
                    .addToBackStack(null)
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
        notificationReference = FirebaseFirestore.getInstance();

        CollectionReference getNumberNotification = notificationReference.collection("Notification").document(myUserId).collection("notif-id");

        com.google.firebase.firestore.Query query = getNumberNotification.whereEqualTo("seen", false);

        query.get().addOnCompleteListener(MainPage.this, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> querySnapshot) {


                int number = (int) querySnapshot.getResult().size();

                if (number == 0) {

                    ShortcutBadger.removeCount(getApplicationContext());


                } else {
                    startService(
                            new Intent(MainPage.this, BadgeServices.class).putExtra("badgeCount", number)
                    );
                }
            }

        });

        Intent intent2 = new Intent(Intent.ACTION_MAIN);
        intent2.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent2, PackageManager.MATCH_DEFAULT_ONLY);
        String currentHomePackage = resolveInfo.activityInfo.packageName;
    }

    public void checkIsLoadAll() {

        com.google.firebase.firestore.Query next = FirebaseFirestore.getInstance().collection("Posting")
                .orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING);
        next.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                if (e == null) {

                    size = querySnapshot.size();


                }
            }

        });

    }

    public void loadPremium() {
        Log.i("premium users", "YEEEEEES");


        CollectionReference db = FirebaseFirestore.getInstance().collection("Posting");

        db.orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING).limit(10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                itemSize.clear();
                if (task.getException() == null) {

                    for (DocumentSnapshot snapshot : task.getResult()) {


                        Post model = snapshot.toObject(Post.class).withId(snapshot.getId());
                        itemSize.add(model);
                        adapter.notifyDataSetChanged();


                        Log.i("itemCount", String.valueOf(adapter.getItemCount()));


                        if (swipeRefreshLayoutPost.isRefreshing()) {
                            swipeRefreshLayoutPost.setRefreshing(false);
                        }

                        lastVisible = task.getResult().getDocuments()
                                .get(task.getResult().size() - 1);
                    }

                    com.google.firebase.firestore.Query next = FirebaseFirestore.getInstance().collection("Posting")
                            .orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING);
                    next.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent (@NonNull QuerySnapshot querySnapshot2, FirebaseFirestoreException e) {
                            if (e == null) {

                                if (querySnapshot2.size() != 0 && !querySnapshot2.isEmpty() && querySnapshot2.size() >= 2) {

                                    prevItemVisible = querySnapshot2.getDocuments().get(querySnapshot2.size() - 1);

                                    if (prevItemVisible.getId().equals(lastVisible.getId())) {
                                        adapter.isFullLoaded(true);
                                    } else {
                                        adapter.isFullLoaded(false);
                                    }
                                }


                            }
                        }

                    });

                } else {
                    Log.e("errorPremium", task.getException().getLocalizedMessage());
                }


            }
        });
    }

    //bb
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);


        initializeCountDrawer();
        messageCounter();
        launcerCounter();


        FirebaseUser user = mAuth.getCurrentUser();
        final String myUserId = user.getUid();

        FirebaseFirestore checkPremiumUser = FirebaseFirestore.getInstance();
        checkPremiumUser.collection("Users").document(myUserId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    model = task.getResult().toObject(UsersModel.class);

                    isPremium = model.isPremium();


                    if (isPremium) {

                        loadPremium();


                        adapter.setOnLoadMore(new OnLoadMoreListener() {
                            @Override
                            public void onLoadMore() {


                                wallList.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        itemSize.add(null);
                                        adapter.notifyItemInserted(itemSize.size() - 1);
                                    }
                                });

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        premiumUsersLoadMore();


                                    }
                                }, 5000);

                            }
                        });

                        swipeRefreshLayoutPost.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {

                                itemSize.clear();
                                loadPremium();
                            }
                        });


                    } else {
                        freeUser.freeUsers(wallList, getApplicationContext(), MainPage.this, model);
                    }
                }
            }
        });

        firstTimeOpened = false;


    }


    public void premiumUsersLoadMore() {


        Log.i("load", String.valueOf(adapter.getItemCount()));
        com.google.firebase.firestore.Query next = FirebaseFirestore.getInstance().collection("Posting")
                .orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(10);
        next.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.getException() == null) {

                    for (DocumentSnapshot snapshot : task.getResult()) {

                        Post model = snapshot.toObject(Post.class).withId(snapshot.getId());
                        itemSize.add(model);
                        adapter.notifyDataSetChanged();
                        adapter.refreshAdapter(EndlessRecyclerViewScrollListener.loading, itemSize);
                        lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        adapter.setIsLoading(false);

                    }

                    com.google.firebase.firestore.Query next = FirebaseFirestore.getInstance().collection("Posting")
                            .orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING);
                    next.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task2) {

                            if (task2.getException() == null) {

                                if (task2.getResult().size() != 0) {
                                    prevItemVisible = task2.getResult().getDocuments().get(task2.getResult().size() - 1);

                                    if (prevItemVisible.getId().equals(lastVisible.getId())) {
                                        adapter.isFullLoaded(true);
                                    } else {
                                        adapter.isFullLoaded(false);
                                    }


                                    Log.i("postTOTALCOUNT", String.valueOf(lastVisible.getId()));
                                    Log.i("postLIST", String.valueOf(prevItemVisible.getId()));
                                }
                            }
                        }

                    });


                    Log.i("itemCount loadmore", String.valueOf(linearLayoutManager.findLastVisibleItemPosition()));

                } else {
                    Log.e("errorLoadMore", task.getException().getLocalizedMessage());
                }
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

        notificationReference = FirebaseFirestore.getInstance();

        CollectionReference getNumberNotification = notificationReference.collection("Notification").document(myUserId).collection("notif-id");

        com.google.firebase.firestore.Query query = getNumberNotification.whereEqualTo("seen", false);

        query.addSnapshotListener(MainPage.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                int number = documentSnapshots.getDocuments().size();

                if (number == 0) {
                    notificationCounterNumber.setText("");
//                    ShortcutBadger.removeCount(MainPage.this);


                } else {
                    notificationCounterNumber.setText(" " + String.valueOf(number));
//                    startService(
//                            new Intent(MainPage.this, BadgeServices.class).putExtra("badgeCount", number)
//                    );
                }
            }
        });
    }

    public void messageCounter(){
        FirebaseUser user = mAuth.getCurrentUser();
        final String myUserId = user.getUid();
        messageCounterNumber.setGravity(Gravity.CENTER_VERTICAL);
        messageCounterNumber.setTypeface(null, Typeface.BOLD);
        messageCounterNumber.setTextColor(getResources().getColor(R.color.redError));

        messageCounterNumber.setGravity(Gravity.CENTER_VERTICAL);

        chatNotificationReference = FirebaseFirestore.getInstance();

        CollectionReference getNumberNotification = chatNotificationReference.collection("NotificationChat").document(myUserId).collection("notif-id");

        com.google.firebase.firestore.Query query = getNumberNotification.whereEqualTo("seen", false);

        query.addSnapshotListener(MainPage.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                int number = documentSnapshots.getDocuments().size();

                if (number == 0) {
                    messageCounterNumber.setText("");
//                    ShortcutBadger.removeCount(MainPage.this);


                } else {
                    messageCounterNumber.setText(" " + String.valueOf(number));
//                    startService(
//                            new Intent(MainPage.this, BadgeServices.class).putExtra("badgeCount", number)
//                    );
                }
            }
        });
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
        JZVideoPlayerStandard.releaseAllVideos();
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


        if (view.getId() == R.id.gallery_icon_content_main) {


        } else if (view.getId() == R.id.vide_icon_content_main) {
            Intent intent = new Intent();
            photoSelected = false;
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), VIDEO_OPEN);

        } else if (view.getId() == R.id.talk_icon_content_main) {

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