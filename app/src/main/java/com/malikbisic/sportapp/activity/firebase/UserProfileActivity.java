package com.malikbisic.sportapp.activity.firebase;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.api.SearchableCountry;
import com.malikbisic.sportapp.adapter.firebase.PlayerFirebaseAdapter;
import com.malikbisic.sportapp.adapter.firebase.UserProfileAdapter;
import com.malikbisic.sportapp.fragment.firebase.ProfileFragment;
import com.malikbisic.sportapp.model.FootballPlayer;
import com.malikbisic.sportapp.model.api.PlayerModel;
import com.malikbisic.sportapp.model.api.SvgDrawableTranscoder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.Orientation;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.SOURCE;

public class UserProfileActivity extends AppCompatActivity implements DiscreteScrollView.OnItemChangedListener {

    private ImageView profile;
    private ImageView flag;
    private TextView username;
    private TextView gender;
    private TextView birthday;
    private TextView country;
    private TextView club;
ImageView genderImageUser;

    private Calendar minAdultAge;
    private BitmapDrawable obwer;
    private ProgressBar loadProfile_image;
    static String uid;
    private static final int GALLERY_REQUEST = 134;

    private FirebaseFirestore mReference;
    boolean hasSetProfileImage = false;
    int selectYear;
    int currentYear;
    int realYear;
    ArrayList<String> usernameList;
    Uri resultUri;
    StorageReference mFilePath;
    StorageReference profileImageUpdate;
    ProgressDialog dialog;
    String name;

    String profileImage;
    String flagImageFirebase;
    Uri imageUri;

    CircleImageView logoClub;
    String clubLogoFirebase;
    Intent myIntent;
    RelativeLayout seeUserPosts;
    ImageView backgroundFlagUsers;

    List<PlayerModel> list;
    FootballPlayer player;
  //  RecyclerView rec;
    //UserProfileAdapter adapter;
    //RecyclerView.LayoutManager layoutManager;

    TextView userPointsTextView;
    int numberLikes;
    int numberDisliks;
    int totalLikes, totalDislikes, pointsTotal;

    TextView thisMonhtNumberLikes;
    TextView thisMonthNumberDislikes;
    TextView postNumber;
RelativeLayout showInfo;
TextView totalPointsTextview;
    private DiscreteScrollView itemPicker;
    private InfiniteScrollAdapter infiniteAdapter;
    String playerID;
    int number;
    int pos = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        list = new ArrayList<>();
        myIntent = getIntent();
        uid = myIntent.getStringExtra("userID");
        mReference = FirebaseFirestore.getInstance();
        profile = (ImageView) findViewById(R.id.get_profile_image_idUser);
        username = (TextView) findViewById(R.id.user_usernameUser);
        seeUserPosts = (RelativeLayout) findViewById(R.id.postlayoutUsers);
        backgroundFlagUsers = (ImageView) findViewById(R.id.user_countryFlagUsersUsers);

        thisMonhtNumberLikes = (TextView) findViewById(R.id.likesinprofilefragmentUsers);
        thisMonthNumberDislikes = (TextView) findViewById(R.id.dislikesinporiflefragmentUsers);
        showInfo = (RelativeLayout) findViewById(R.id.inforelativeUsers);
        postNumber = (TextView) findViewById(R.id.postsNumberUsers);

        numberPost();
        totalUserPoints();
        playerWinner();
        seeUserPosts.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(UserProfileActivity.this,SeeUsersPostsActivity.class);
        intent.putExtra("userProfileUid",uid);
        startActivity(intent);
    }
});

        usernameList = new ArrayList<>();
        mFilePath = FirebaseStorage.getInstance().getReference();
        dialog = new ProgressDialog(this);
        loadProfile_image = (ProgressBar) findViewById(R.id.loadingProfileImageProgressBarUser);
        minAdultAge = new GregorianCalendar();
        userPointsTextView = (TextView) findViewById(R.id.user_points_textview);
        totalPointsTextview = (TextView) findViewById(R.id.totalpointsnumberUsers);
        usersPoint(UserProfileActivity.this);

        showInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
showAlertInfo();
            }
        });
        player = player.get();
        itemPicker = (DiscreteScrollView) findViewById(R.id.pickerUsers);
        itemPicker.setNestedScrollingEnabled(false);
        itemPicker.setOrientation(Orientation.HORIZONTAL);
        itemPicker.addOnItemChangedListener(this);
        infiniteAdapter = InfiniteScrollAdapter.wrap(new PlayerFirebaseAdapter(list,UserProfileActivity.this));
        itemPicker.setAdapter(infiniteAdapter);
        itemPicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());


      //  rec = (RecyclerView) findViewById(R.id.hhhhhhUser);
        //layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        rec.setLayoutManager(layoutManager);
        //adapter = new UserProfileAdapter(UserProfileActivity.this);
        //rec.setAdapter(adapter);

        Log.i("userID", uid);



        updateListPlayer();
        loadProfile_image.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.redError), PorterDuff.Mode.SRC_IN );
        mReference.collection("Users").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {


                Map<String, Object> value = dataSnapshot.getData();

                profileImage = String.valueOf(value.get("profileImage"));
                Glide.with(UserProfileActivity.this)
                        .load(profileImage)
                        .into(profile);
                    loadProfile_image.setVisibility(View.GONE);

                username.setText(String.valueOf(value.get("username")));
            /*    gender.setText(String.valueOf(value.get("gender")));
                if (String.valueOf(value.get("gender")).equals("Male")) {
                    genderImageUser.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    genderImageUser.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.maleicon, null));
                } else if (String.valueOf(value.get("gender")).equals("Female")) {
                    genderImageUser.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    genderImageUser.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.femaleicon, null));
                }


                birthday.setText(String.valueOf(value.get("date")));
                club.setText(String.valueOf(value.get("favoriteClub")));
*/
                flagImageFirebase = String.valueOf(value.get("flag"));
                Log.i("flag uri", flagImageFirebase);
/*
                clubLogoFirebase = String.valueOf(value.get("favoriteClubLogo"));*/

                backgroundFlagUsers.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//                logoClub.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;

                requestBuilder = Glide
                        .with(UserProfileActivity.this)
                        .using(Glide.buildStreamModelLoader(Uri.class, UserProfileActivity.this), InputStream.class)
                        .from(Uri.class)
                        .as(SVG.class)
                        .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                        .sourceEncoder(new StreamEncoder())
                        .cacheDecoder(new FileToStreamDecoder<SVG>(new SearchableCountry.SvgDecoder()))
                        .decoder(new SearchableCountry.SvgDecoder())
                        .animate(android.R.anim.fade_in);


                Uri uri = Uri.parse(flagImageFirebase);
                requestBuilder
                        // SVG cannot be serialized so it's not worth to cache it
                        .diskCacheStrategy(SOURCE)
                        .load(uri)
                        .into(backgroundFlagUsers);



            /*    Picasso.with(UserProfileActivity.this).load(clubLogoFirebase).into(logoClub);


                country.setText(String.valueOf(value.get("country")));*/

                backgroundImage();
            }

        });


    }

    public void usersPoint(final Activity activity) {


        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference points = db.collection("Points").document(uid);
        points.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                long likeNumber = task.getResult().getLong("currentMonthPoints.likePoints");
                long dislikeNumber = task.getResult().getLong("currentMonthPoints.dislikePoints");
                long totalNumber = task.getResult().getLong("currentMonthPoints.totalPoints");

                if (likeNumber == 0) {
                    thisMonhtNumberLikes.setText("0");
                }
                if (dislikeNumber == 0) {
                    thisMonthNumberDislikes.setText("0");
                }
                thisMonhtNumberLikes.setText(String.valueOf(likeNumber));
                thisMonthNumberDislikes.setText(String.valueOf(dislikeNumber));

                if (totalNumber <= 0) {
                    userPointsTextView.setText("0");
                }
                if (totalNumber > 0) {
                    userPointsTextView.setText(String.valueOf(totalNumber));
                }


            }
        });
    }


    public void backgroundImage() {

        HttpImageRequestTask task = new HttpImageRequestTask();
        try {
            task.execute(flagImageFirebase).get();



        } catch (InterruptedException e) {
            Log.e("error1",e.getLocalizedMessage());
        } catch (ExecutionException e) {
            Log.e("error2", e.getLocalizedMessage());
        }

    }

    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            selectYear = year;
            currentYear = minAdultAge.get(Calendar.YEAR);
            Log.i("Year select", String.valueOf(selectYear));
            Log.i("Year current", String.valueOf(currentYear));

            realYear = currentYear - selectYear;
            Log.i("Year check", String.valueOf(realYear));


            updateLabel();
        }

    };

    private void updateLabel() {

        String myFormat = "dd/MMMM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        birthday.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            profile.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            profile.setAlpha(254);
            imageUri = data.getData();
            profile.setImageURI(imageUri);

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(UserProfileActivity.this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                Picasso.with(UserProfileActivity.this).load(resultUri)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.profilimage).error(R.mipmap.ic_launcher)
                        .into(profile);
                profileImageUpdate = mFilePath.child("Profile_Image").child(resultUri.getLastPathSegment());
                profileImageUpdate.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri = taskSnapshot.getDownloadUrl();

                        //mReference = mDatabase.getReference().child("Users").child(uid);
                        //mReference.child("profileImage").setValue(downloadUri.toString());
                    }
                });
                hasSetProfileImage = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.i("errorCrop", String.valueOf(error));
            }
        }
    }

    public void showAlertInfo() {


        AlertDialog.Builder playerVoteDialogBuilder = new AlertDialog.Builder(UserProfileActivity.this);
        View viewDialog = LayoutInflater.from(UserProfileActivity.this).inflate(R.layout.user_info_alert_dialog, null);
        playerVoteDialogBuilder.setView(viewDialog);

        final CircleImageView fromImg = (CircleImageView) viewDialog.findViewById(R.id.fromimg);
        final ImageView genderImg = (ImageView) viewDialog.findViewById(R.id.gender_image);
        final CircleImageView logoClubImg = (CircleImageView) viewDialog.findViewById(R.id.userClubLogo);
        ImageView dateImg = (ImageView) viewDialog.findViewById(R.id.date_image);
        fromImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_black_24dp));
        dateImg.setImageDrawable(getResources().getDrawable(R.drawable.birthdayicon));
        final TextView clubtextview = (TextView) viewDialog.findViewById(R.id.clubtext);

        final TextView fromImgTextview = (TextView) viewDialog.findViewById(R.id.user_country);
        final TextView genderText = (TextView) viewDialog.findViewById(R.id.user_gender);
        final TextView dateText = (TextView) viewDialog.findViewById(R.id.user_date);

        mReference.collection("Users").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> value = dataSnapshot.getData();
                    String flagUser;
                    String logoClubUser;
                    flagUser = String.valueOf(value.get("flag"));
                    logoClubUser = String.valueOf(value.get("favoriteClubLogo"));
                    logoClubImg.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    clubtextview.setText(String.valueOf(value.get("favoriteClub")));
                    Picasso.with(UserProfileActivity.this).load(logoClubUser).into(logoClubImg);
                    genderText.setText(String.valueOf(value.get("gender")));
                    if (String.valueOf(value.get("gender")).equals("Male")) {
                        genderImg.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                        genderImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.maleicon, null));
                    } else if (String.valueOf(value.get("gender")).equals("Female")) {
                        genderImg.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                        genderImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.femaleicon, null));
                    }


                    dateText.setText(String.valueOf(value.get("date")));


                    Log.i("flag uri", flagImageFirebase);


                    fromImg.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

                    GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;

                    requestBuilder = Glide
                            .with(UserProfileActivity.this)
                            .using(Glide.buildStreamModelLoader(Uri.class, UserProfileActivity.this), InputStream.class)
                            .from(Uri.class)
                            .as(SVG.class)
                            .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                            .sourceEncoder(new StreamEncoder())
                            .cacheDecoder(new FileToStreamDecoder<SVG>(new SearchableCountry.SvgDecoder()))
                            .decoder(new SearchableCountry.SvgDecoder())
                            .animate(android.R.anim.fade_in);


                    Uri uri = Uri.parse(flagUser);
                    requestBuilder
                            // SVG cannot be serialized so it's not worth to cache it
                            .diskCacheStrategy(SOURCE)
                            .load(uri)
                            .into(fromImg);


                    fromImgTextview.setText(String.valueOf(value.get("country")));

                }
            }
        });

        playerVoteDialogBuilder.setNegativeButton("Back", null);

        playerVoteDialogBuilder.create();
        playerVoteDialogBuilder.show();

    }
    public void numberPost() {
        final CollectionReference numberPostRef = FirebaseFirestore.getInstance().collection("Posting");

        final com.google.firebase.firestore.Query query = numberPostRef.whereEqualTo("uid", uid);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                number = documentSnapshots.size();
                postNumber.setText(String.valueOf(number));
            }
        });
    }
    public void totalUserPoints() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query usersPost = db.collection("Posting").whereEqualTo("uid", uid);
        usersPost.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                if (task.getException() == null) {

                    for (DocumentSnapshot snapshot : task.getResult()) {
                        final String postID = snapshot.getId();

                        Log.i("postID", postID);

                        CollectionReference likeNumber = db.collection("Likes").document(postID).collection("like-id");
                        likeNumber.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                if (task2.getException() == null) {

                                    numberLikes = task2.getResult().size();

                                    totalLikes+=numberLikes;

                                    CollectionReference dislikeNumber = db.collection("Dislikes").document(postID).collection("dislike-id");
                                    dislikeNumber.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task3) {

                                            if (task3.getException() == null) {
                                                numberDisliks = task3.getResult().size();

                                                totalDislikes+=numberDisliks;


                                                pointsTotal = totalLikes - totalDislikes;
                                                if (pointsTotal == 0) {
                                                    totalPointsTextview.setText("0");
                                                }
                                                if (pointsTotal > 0) {
                                                    totalPointsTextview.setText(String.valueOf(pointsTotal));
                                                }


                                            }
                                        }

                                    });


                                }
                            }

                        });


                    }


                }
            }
        });

    }

    public void playerWinner(){
        final TextView playerNameTextview = (TextView) findViewById(R.id.playernamewinner);
        final ImageView playerImageImageview = (ImageView) findViewById(R.id.playerImageWinner);
        final TextView playerPointsTextview = (TextView) findViewById(R.id.pointsNumberWinner);
        final TextView playerVoteTextview = (TextView) findViewById(R.id.userVotesNumberWinner);
        final ImageView playerClubLogoImageview = (ImageView) findViewById(R.id.userClubLogo);
        final ImageView playerCountryFlagImageview = (ImageView) findViewById(R.id.usercountry);

        DateTime prevDate = new DateTime().minusMonths(1);
        final String prevMonth = prevDate.toString("MMMM");
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Query documentReference = db.collection("PlayerPoints").document(prevMonth).collection("player-id").orderBy("playerPoints", Query.Direction.DESCENDING).limit(1);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@Nullable Task<QuerySnapshot> task) {
                String playName;
                String playerImage;
                long playerPoints;
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    if (documentSnapshot.exists()) {
                        playerID = documentSnapshot.getId();
                        playName = documentSnapshot.getString("playerName");
                        playerImage = documentSnapshot.getString("playerImage");
                        playerPoints = documentSnapshot.getLong("playerPoints");

                        playerNameTextview.setText(playName);
                        Glide.with(UserProfileActivity.this).load(playerImage).into(playerImageImageview);
                        playerPointsTextview.setText(String.valueOf(playerPoints));

                    }

                    String urlClubCountryPlayer = "https://soccer.sportmonks.com/api/v2.0/players/"+playerID+"?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s&include=team";
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlClubCountryPlayer, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject data = response.getJSONObject("data");
                                String country = data.getString("nationality");

                                JSONObject teamObj = data.getJSONObject("team");
                                JSONObject dataTeam = teamObj.getJSONObject("data");
                                String clubLogo = dataTeam.getString("logo_path");
                                Glide.with(UserProfileActivity.this).load(clubLogo).into(playerClubLogoImageview);

                                if (country.equals("England")) {
                                    playerCountryFlagImageview.setImageDrawable(getResources().getDrawable(R.drawable.england));
                                } else if (country.equals("Northern Ireland")) {
                                    playerCountryFlagImageview.setImageDrawable(getResources().getDrawable(R.drawable.northern_ireland));
                                } else if (country.equals("Scotland")) {
                                    playerCountryFlagImageview.setImageDrawable(getResources().getDrawable(R.drawable.scotland));
                                } else if (country.equals("Wales")) {
                                    playerCountryFlagImageview.setImageDrawable(getResources().getDrawable(R.drawable.welsh_flag));
                                } else {

                                    String countryURL = "https://restcountries.eu/rest/v2/name/" + country;

                                    JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, countryURL, null, new Response.Listener<JSONArray>() {
                                        @Override
                                        public void onResponse(JSONArray response) {
                                            Log.i("json", response.toString());
                                            try {
                                                for (int i = 0; i < response.length(); i++) {

                                                    JSONObject object = response.getJSONObject(i);
                                                    String countryName = object.getString("name");
                                                    String countryImage = object.getString("flag");
                                                    System.out.println("country flag" + countryImage);
                                                    GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;

                                                    requestBuilder = Glide
                                                            .with(UserProfileActivity.this)
                                                            .using(Glide.buildStreamModelLoader(Uri.class, UserProfileActivity.this), InputStream.class)
                                                            .from(Uri.class)
                                                            .as(SVG.class)
                                                            .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                                                            .sourceEncoder(new StreamEncoder())
                                                            .cacheDecoder(new FileToStreamDecoder<SVG>(new SearchableCountry.SvgDecoder()))
                                                            .decoder(new SearchableCountry.SvgDecoder())
                                                            .animate(android.R.anim.fade_in);


                                                    Uri uri = Uri.parse(countryImage);

                                                    requestBuilder
                                                            // SVG cannot be serialized so it's not worth to cache it
                                                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                                            .load(uri)
                                                            .into(playerCountryFlagImageview);


                                                }

                                            } catch (JSONException e) {
                                                Log.v("json", e.getLocalizedMessage());
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            //Log.v("json", error.getLocalizedMessage());
                                        }
                                    });
                                    Volley.newRequestQueue(UserProfileActivity.this).add(request);
                                }
                            } catch (JSONException e) {
                                Log.e("errJSOn", e.getLocalizedMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("vollError", error.getLocalizedMessage());
                        }
                    });
                    Volley.newRequestQueue(UserProfileActivity.this).add(request);

                    final Query usersVoteRef = db.collection("PlayerPoints").document(prevMonth).collection("player-id").document(playerID).collection("usersVote");

                    usersVoteRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            long numberVotes = task.getResult().size();

                            playerVoteTextview.setText(String.valueOf(numberVotes));
                        }
                    });

                }
            }
        });
    }

    public void updateListPlayer() {
        DateFormat currentDateFormat = new SimpleDateFormat("MMMM");
        final Date currentDate = new Date();

        final String currentMonth = currentDateFormat.format(currentDate);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Query documentReference = db.collection("PlayerPoints").document(currentMonth).collection("player-id").orderBy("playerPoints", Query.Direction.DESCENDING).limit(10);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@Nullable Task<QuerySnapshot> task) {
                String playName;
                String playerImage;
                long playerPoints;
                int id = 0;
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    if (documentSnapshot.exists()) {
                        id++;
                        playerID = documentSnapshot.getId();
                        playName = documentSnapshot.getString("playerName");
                        playerImage = documentSnapshot.getString("playerImage");
                        playerPoints = documentSnapshot.getLong("playerPoints");
                        list.add(new PlayerModel(id, playName, playerImage, playerPoints, playerID));
                        pos++;
                        infiniteAdapter.notifyDataSetChanged();
                    }
                    onItemChanged(list.get(0));

                }
            }
        });
    }
    private void onItemChanged(PlayerModel item) {
        TextView namePlayer = (TextView) findViewById(R.id.playerName);
        namePlayer.setText(item.getName());

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
        private void updateImageView(Drawable drawable){
            if(drawable != null){

                // Try using your library and adding this layer type before switching your SVG parsing
                backgroundFlagUsers.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
backgroundFlagUsers.setImageDrawable(drawable);


            }
        }
    }




    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {

        int positionInDataSet = infiniteAdapter.getRealPosition(adapterPosition);
        onItemChanged(list.get(positionInDataSet));
    }

}
