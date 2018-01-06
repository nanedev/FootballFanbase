package com.malikbisic.sportapp.fragment.firebase;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;

import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;

import com.caverock.androidsvg.SVG;

import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
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
import com.malikbisic.sportapp.activity.firebase.MainPage;
import com.malikbisic.sportapp.adapter.firebase.PlayerFirebaseAdapter;
import com.malikbisic.sportapp.adapter.firebase.ProfileFragmentAdapter;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.SOURCE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends AppCompatActivity  implements DiscreteScrollView.OnItemChangedListener, View.OnKeyListener {

    private ImageView profile;
    private ImageView flag;
    private TextView username;
    private TextView gender;
    private TextView birthday;
    private TextView country;
    //private TextView club;
    TextView addBackground;
    private TextView name_surname;
    ImageView genderImage;
    //TextView myPosts;
    Intent myIntent;
    TextView thisMonhtNumberLikes;
    TextView thisMonthNumberDislikes;

    private Calendar minAdultAge;
    private ProgressBar loadProfile_image;
    private String uid;
    private static final int GALLERY_REQUEST = 134;

    private FirebaseDatabase mDatabase;
    private FirebaseFirestore mReference;
    boolean hasSetProfileImage = false;
    int selectYear;
    int currentYear;
    int realYear;
    ArrayList<String> usernameList;
    Uri resultUri;
    StorageReference mFilePath;
    StorageReference profileImageUpdate;
    StorageReference backgroundUpdate;
    ProgressDialog dialog;
    String name;
    TextView editProfilePicture;
    String profileImage;
    String flagImageFirebase;
    Uri imageUri;
    Uri backgroundUri;
    ImageView backgroundImage;
    //ImageView logoClub;
    String clubLogoFirebase;
    ImageView backarrow;
    View premiumLinija;
    RelativeLayout premiumLayout;

    //RecyclerView rec;
    ProfileFragmentAdapter adapter;
    //RecyclerView.LayoutManager layoutManager;

    String myUid;
    boolean checkOpenActivity;
    DocumentReference mReference2;

    TextView userPointsTextView;
    int numberLikes;
    int numberDisliks;
    int totalLikes, totalDislikes, pointsTotal;
    RelativeLayout showInfo;
    TextView hideInfo;
    RelativeLayout winnerImage;
    RelativeLayout pointsLayoutWinner;
    RelativeLayout usersLayoutWinner;
    RelativeLayout countryLayoutWinner;
    RelativeLayout clubLayoutWinner;
    boolean firstImageClick = true;
    boolean secondImageClick = false;

    List<PlayerModel> list;
    FootballPlayer player;

    private DiscreteScrollView itemPicker;
    private InfiniteScrollAdapter infiniteAdapter;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        setContentView(R.layout.fragment_profile);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getSupportActionBar().hide();


        player = player.get();
        list = player.getData();
        itemPicker = (DiscreteScrollView) findViewById(R.id.picker);
        itemPicker.setOrientation(Orientation.HORIZONTAL);
        itemPicker.addOnItemChangedListener(this);
        infiniteAdapter = InfiniteScrollAdapter.wrap(new PlayerFirebaseAdapter(list));
        itemPicker.setAdapter(infiniteAdapter);
        itemPicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
        onItemChanged(list.get(0));

        mDatabase = FirebaseDatabase.getInstance();
        mReference = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();


            myUid = getIntent().getStringExtra("myUid");
            checkOpenActivity = getIntent().getBooleanExtra("openFromFanBaseTable", false);


        if (checkOpenActivity) {
            mReference2 = FirebaseFirestore.getInstance().collection("Users").document(myUid);
        } else {
            mReference2 = mReference.collection("Users").document(uid);

        }

        Log.i("myUID", myUid + " |uid" + uid);
        profile = (ImageView) findViewById(R.id.get_profile_image_id);
        //  name_surname = (TextView) view.findViewById(R.id.name_surname);
        flag = (ImageView) findViewById(R.id.user_countryFlag);
        username = (TextView) findViewById(R.id.user_username);
        gender = (TextView) findViewById(R.id.user_gender);
        birthday = (TextView) findViewById(R.id.user_date);
        country = (TextView) findViewById(R.id.user_country);
        showInfo = (RelativeLayout) findViewById(R.id.inforelative);
        backarrow = (ImageView) findViewById(R.id.backarrow);
        thisMonhtNumberLikes = (TextView) findViewById(R.id.likesinprofilefragment) ;
        thisMonthNumberDislikes = (TextView) findViewById(R.id.dislikesinporiflefragment);
        winnerImage = (RelativeLayout) findViewById(R.id.layoutForImageOFWinner);
        pointsLayoutWinner = (RelativeLayout) findViewById(R.id.pointsofWinner);
        usersLayoutWinner = (RelativeLayout) findViewById(R.id.usersOfWinner);
        countryLayoutWinner = (RelativeLayout) findViewById(R.id.playerCountry);
        clubLayoutWinner = (RelativeLayout) findViewById(R.id.playerTeam);
        winnerImage = (RelativeLayout) findViewById(R.id.layoutForImageOFWinner);
        winnerImage = (RelativeLayout) findViewById(R.id.layoutForImageOFWinner);

        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileFragment.this,MainPage.class);
                startActivity(intent);
            }
        });



        showInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertInfo();
            }
        });


        winnerImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                if (firstImageClick) {
                    firstImageClick = false;
                    secondImageClick = true;
                    usersLayoutWinner.setVisibility(View.VISIBLE);
                    countryLayoutWinner.setVisibility(View.VISIBLE);
                    clubLayoutWinner.setVisibility(View.VISIBLE);
                    pointsLayoutWinner.setVisibility(View.VISIBLE);

                }else  if (secondImageClick){
                    firstImageClick = true;
                    secondImageClick = false;
                    usersLayoutWinner.setVisibility(View.GONE);
                    countryLayoutWinner.setVisibility(View.GONE);
                    clubLayoutWinner.setVisibility(View.GONE);
                    pointsLayoutWinner.setVisibility(View.GONE);
                }
            }
        });


        //  club = (TextView)  findViewById(R.id.user_club);
        userPointsTextView = (TextView) findViewById(R.id.user_points_textview);
        usersPoint(this);

        editProfilePicture = (TextView)  findViewById(R.id.edit_profile_image);


        //logoClub = (ImageView)  findViewById(R.id.club_logo_profile);

        usernameList = new ArrayList<>();
        mFilePath = FirebaseStorage.getInstance().getReference();
        dialog = new ProgressDialog(ProfileFragment.this);
        loadProfile_image = (ProgressBar)  findViewById(R.id.loadingProfileImageProgressBar);
        premiumLinija =  findViewById(R.id.sixthline);
        genderImage = (ImageView)  findViewById(R.id.gender_image);
        //      rec = (RecyclerView)  findViewById(R.id.hhhhhh);
        //    layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
//        rec.setLayoutManager(layoutManager);
        // adapter = new ProfileFragmentAdapter(getActivity());
        //   rec.setAdapter(adapter);


        minAdultAge = new GregorianCalendar();
        editProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = {"Open gallery"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(ProfileFragment.this, R.style.AppTheme_Dark_Dialog);
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (items[i].equals("Open gallery")) {
                            Intent openGallery = new Intent(Intent.ACTION_GET_CONTENT);
                            openGallery.setType("image/*");
                           startActivityForResult(openGallery, GALLERY_REQUEST);
                        }
                    }
                });
                dialog.create();
                dialog.show();
            }
        });

        Activity activity = ProfileFragment.this;
        if (activity != null) {
            loadProfile_image.getIndeterminateDrawable()
                    .setColorFilter(ContextCompat.getColor(ProfileFragment.this, R.color.redError), PorterDuff.Mode.SRC_IN);
            mReference2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {
                    if (dataSnapshot.exists()) {
                        Map<String, Object> value = dataSnapshot.getData();


                        profileImage = String.valueOf(value.get("profileImage"));
                        profile.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                        Picasso.with(ProfileFragment.this)
                                .load(profileImage)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .into(profile, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        loadProfile_image.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });

                        //                   name = value.get("name") + " " + value.get("surname");
                        //         name_surname.setText(name);
                        username.setText(String.valueOf(value.get("username")));
                        //               gender.setText(String.valueOf(value.get("gender")));
                        //             if (String.valueOf(value.get("gender")).equals("Male")) {
                        //             genderImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                        //             genderImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.maleicon, null));
                        //       } else if (String.valueOf(value.get("gender")).equals("Female")) {
                        //         genderImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                        //      genderImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.femaleicon, null));
                        // }


                        //birthday.setText(String.valueOf(value.get("date")));
                        //club.setText(String.valueOf(value.get("favoriteClub")));


                        flagImageFirebase = String.valueOf(value.get("flag"));
                        Log.i("flag uri", flagImageFirebase);



                        flag.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

                        GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;

                        requestBuilder = Glide
                                .with(ProfileFragment.this)
                                .using(Glide.buildStreamModelLoader(Uri.class, ProfileFragment.this), InputStream.class)
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
                                .into(flag);




                               /*Picasso.with(ProfileFragment.this.getActivity())
                        .load(flagImageFirebase)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(flag, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                            try {

                            }catch (Exception e)
                            {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            }
                        });*/

//                        country.setText(String.valueOf(value.get("country")));

                    }
                }
            });
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
    private void onItemChanged(PlayerModel item) {
        TextView namePlayer = (TextView) findViewById(R.id.playerName);
        namePlayer.setText(item.getName());

    }

    public void usersPoint(final Activity activity) {


        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query usersPost = db.collection("Posting").whereEqualTo("uid", uid);
        usersPost.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                if (e == null) {

                    for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                        final String postID = snapshot.getId();

                        Log.i("postID", postID);

                        CollectionReference likeNumber = db.collection("Likes").document(postID).collection("like-id");
                        likeNumber.addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {


                                if (e == null) {
                                    numberLikes = documentSnapshots.size();

                                    totalLikes += numberLikes;

                                    CollectionReference dislikeNumber = db.collection("Dislikes").document(postID).collection("dislike-id");
                                    dislikeNumber.addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                                            if (e == null) {
                                                numberDisliks = documentSnapshots.size();

                                                totalDislikes += numberDisliks;
                                                if (totalLikes == 0){
                                                    thisMonhtNumberLikes.setText("0");
                                                }
                                                if (totalDislikes == 0){
                                                    thisMonthNumberDislikes.setText("0");
                                                }
                                                thisMonhtNumberLikes.setText(String.valueOf(totalLikes));
                                                thisMonthNumberDislikes.setText(String.valueOf(totalDislikes));
                                                pointsTotal = totalLikes - totalDislikes;
                                                if (pointsTotal <= 0){
                                                    userPointsTextView.setText("0");
                                                }
                                                if (pointsTotal > 0) {
                                                    userPointsTextView.setText(String.valueOf(pointsTotal));
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

    private void updateLabel() {

        String myFormat = "dd/MMMM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        //   birthday.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            profile.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            profile.setAlpha(0.9f);
            imageUri = data.getData();
            profile.setImageURI(imageUri);

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(ProfileFragment.this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();

                try {
                    File imagePath = new File(resultUri.getPath());
                    Log.i("imagePath", imagePath.getPath());


                    Bitmap imageCompressBitmap = new Compressor(ProfileFragment.this)
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(75)
                            .compressToBitmap(imagePath);


                    Picasso.with(ProfileFragment.this).load(String.valueOf(imageCompressBitmap))
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .resize(300, 300)
                            .placeholder(R.drawable.profilimage).error(R.mipmap.ic_launcher)
                            .into(profile);
                    profileImageUpdate = mFilePath.child("Profile_Image").child(resultUri.getLastPathSegment());

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imageCompressBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] changeImageData = baos.toByteArray();

                    UploadTask uploadTask = profileImageUpdate.putBytes(changeImageData);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            final Uri downloadUri = taskSnapshot.getDownloadUrl();

                            DocumentReference mDoc = mReference.collection("Users").document(uid);
                            if (downloadUri != null) {
                                Map<String, Object> updateProfile = new HashMap<>();
                                updateProfile.put("profileImage", downloadUri.toString());

                                mDoc.update(updateProfile);

                                final DocumentReference postingImageUpdate = mReference.collection("Posting").document(uid);
                                //com.google.firebase.firestore.Query query = postingImageUpdate.whereEqualTo("uid",uid);

                                postingImageUpdate.update(updateProfile);
                                hasSetProfileImage = true;

                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.i("errorCrop", String.valueOf(error));
            }

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        menu.clear();

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);


    }
    public void showAlertInfo(){


        AlertDialog.Builder playerVoteDialogBuilder = new AlertDialog.Builder(ProfileFragment.this);
        View viewDialog = LayoutInflater.from(ProfileFragment.this).inflate(R.layout.user_info_alert_dialog, null);
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

        mReference2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
                    Picasso.with(ProfileFragment.this).load(logoClubUser).into(logoClubImg);
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
                            .with(ProfileFragment.this)
                            .using(Glide.buildStreamModelLoader(Uri.class, ProfileFragment.this), InputStream.class)
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

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                String cameback = "CameBack";
                Intent intent = new Intent(ProfileFragment.this, MainPage.class);
                startActivity(intent);
                return true;
        }
        return false;
    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
        int positionInDataSet = infiniteAdapter.getRealPosition(adapterPosition);
        onItemChanged(list.get(positionInDataSet));
    }
}

