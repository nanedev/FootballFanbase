package com.malikbisic.sportapp.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.ProfileFragmentAdapter;
import com.malikbisic.sportapp.adapter.UserProfileAdapter;
import com.malikbisic.sportapp.model.SvgDrawableTranscoder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.SOURCE;

public class UserProfileActivity extends AppCompatActivity {

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
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
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
    TextView seeUserPosts;
    ImageView backgroundFlagUsers;


    RecyclerView rec;
    UserProfileAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mDatabase = FirebaseDatabase.getInstance();
        myIntent = getIntent();
        uid = myIntent.getStringExtra("userID");
        mReference = mDatabase.getReference().child("Users").child(uid);
        profile = (ImageView) findViewById(R.id.get_profile_image_idUser);
        flag = (ImageView) findViewById(R.id.user_countryFlagUser);
        username = (TextView) findViewById(R.id.user_usernameUser);
        gender = (TextView) findViewById(R.id.user_genderUser);
        birthday = (TextView) findViewById(R.id.user_dateUser);
        country = (TextView) findViewById(R.id.user_countryUser);
        club = (TextView) findViewById(R.id.user_clubUser);
        seeUserPosts = (TextView) findViewById(R.id.see_user_posts);
        genderImageUser = (ImageView) findViewById(R.id.gender_imageUser);
        backgroundFlagUsers = (ImageView) findViewById(R.id.user_countryFlagUsersUsers);
seeUserPosts.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(UserProfileActivity.this,SeeUsersPostsActivity.class);
        intent.putExtra("userProfileUid",uid);
        startActivity(intent);
    }
});

        logoClub = (CircleImageView) findViewById(R.id.club_logo_profileUser);
        usernameList = new ArrayList<>();
        mFilePath = FirebaseStorage.getInstance().getReference();
        dialog = new ProgressDialog(this);
        loadProfile_image = (ProgressBar) findViewById(R.id.loadingProfileImageProgressBarUser);;
        minAdultAge = new GregorianCalendar();


        rec = (RecyclerView) findViewById(R.id.hhhhhhUser);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rec.setLayoutManager(layoutManager);
        adapter = new UserProfileAdapter(UserProfileActivity.this);
        rec.setAdapter(adapter);




        loadProfile_image.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.redError), PorterDuff.Mode.SRC_IN );
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();


                profileImage = String.valueOf(value.get("profileImage"));
                Picasso.with(UserProfileActivity.this)
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

                username.setText(String.valueOf(value.get("username")));
                gender.setText(String.valueOf(value.get("gender")));
                if (String.valueOf(value.get("gender")).equals("Male")) {
                    genderImageUser.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    genderImageUser.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.maleicon, null));
                } else if (String.valueOf(value.get("gender")).equals("Female")) {
                    genderImageUser.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    genderImageUser.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.femaleicon, null));
                }


                birthday.setText(String.valueOf(value.get("date")));
                club.setText(String.valueOf(value.get("favoriteClub")));

                flagImageFirebase = String.valueOf(value.get("flag"));
                Log.i("flag uri", flagImageFirebase);

                clubLogoFirebase = String.valueOf(value.get("favoriteClubLogo"));

                backgroundFlagUsers.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                logoClub.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
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



                Picasso.with(UserProfileActivity.this).load(clubLogoFirebase).into(logoClub);


                country.setText(String.valueOf(value.get("country")));

                backgroundImage();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

                        mReference = mDatabase.getReference().child("Users").child(uid);
                        mReference.child("profileImage").setValue(downloadUri.toString());
                    }
                });
                hasSetProfileImage = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.i("errorCrop", String.valueOf(error));
            }
        }
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
}
