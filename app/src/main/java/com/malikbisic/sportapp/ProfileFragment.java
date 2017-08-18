package com.malikbisic.sportapp;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.widget.DatePicker;

import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;

import com.caverock.androidsvg.SVG;

import com.caverock.androidsvg.SVGParseException;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
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

import static android.app.Activity.RESULT_OK;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.SOURCE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private ImageView profile;
    private CircleImageView flag;
    private TextView username;
    private TextView gender;
    private TextView birthday;
    private TextView country;
    private TextView club;
    TextView addBackground;
    private TextView name_surname;
    CircleImageView genderImage;

    private Calendar minAdultAge;
    private ProgressBar loadProfile_image;
    private String uid;
    private static final int GALLERY_REQUEST = 134;
    private static final int BACKGROUND_REQUEST = 137;
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
    StorageReference backgroundUpdate;
    ProgressDialog dialog;
    String name;
    TextView editProfilePicture;
    String profileImage;
    String flagImageFirebase;
    Uri imageUri;
    Uri backgroundUri;
    ImageView backgroundImage;
    ImageView logoClub;
    String clubLogoFirebase;
    DateFormat dateFormat;
    Calendar calendar;
    Date currentDateOfUser;
    String getDateFromDatabase;
    TextView premiumTrialDate;
    Date trialDate;
    String trialDateString;
    Date dateRightNow;
    TextView premiumTextview;
    View premiumLinija;
    RelativeLayout premiumLayout;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setHasOptionsMenu(true);

        mDatabase = FirebaseDatabase.getInstance();
        uid = MainPage.uid;
        mReference = mDatabase.getReference().child("Users").child(uid);
        profile = (ImageView) view.findViewById(R.id.get_profile_image_id);
        //  name_surname = (TextView) view.findViewById(R.id.name_surname);
        flag = (CircleImageView) view.findViewById(R.id.user_countryFlag);
        username = (TextView) view.findViewById(R.id.user_username);
        gender = (TextView) view.findViewById(R.id.user_gender);
        birthday = (TextView) view.findViewById(R.id.user_date);
        country = (TextView) view.findViewById(R.id.user_country);
        club = (TextView) view.findViewById(R.id.user_club);
        premiumTrialDate = (TextView) view.findViewById(R.id.user_premium_date);
        editProfilePicture = (TextView) view.findViewById(R.id.edit_profile_image);
        backgroundImage = (ImageView) view.findViewById(R.id.backgroundProfile);
        logoClub = (ImageView) view.findViewById(R.id.club_logo_profile);
        calendar = Calendar.getInstance();
        usernameList = new ArrayList<>();
        mFilePath = FirebaseStorage.getInstance().getReference();
        dialog = new ProgressDialog(getContext());
        loadProfile_image = (ProgressBar) view.findViewById(R.id.loadingProfileImageProgressBar);
        premiumLinija = view.findViewById(R.id.sixthline);
        premiumLayout = (RelativeLayout) view.findViewById(R.id.premium_layout);
        premiumTextview = (TextView) view.findViewById(R.id.your_premium);
        genderImage = (CircleImageView) view.findViewById(R.id.gender_image);


        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateRightNow = new Date();

        minAdultAge = new GregorianCalendar();
        editProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = {"Open gallery"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dark_Dialog);
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (items[i].equals("Open gallery")) {
                            Intent openGallery = new Intent(Intent.ACTION_GET_CONTENT);
                            openGallery.setType("image/*");
                            getActivity().startActivityForResult(openGallery, GALLERY_REQUEST);
                        }
                    }
                });
                dialog.create();
                dialog.show();
            }
        });


        loadProfile_image.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(getContext(), R.color.redError), PorterDuff.Mode.SRC_IN);
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();


                profileImage = String.valueOf(value.get("profileImage"));
                Picasso.with(getActivity())
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

                name = value.get("name") + " " + value.get("surname");
//                name_surname.setText(name);
                username.setText(String.valueOf(value.get("username")));
                gender.setText(String.valueOf(value.get("gender")));
                if (String.valueOf(value.get("gender")).equals("Male")) {
                    genderImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    genderImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.male, null));
                } else if (String.valueOf(value.get("gender")).equals("Female")) {
                    genderImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    genderImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.female, null));
                }


                birthday.setText(String.valueOf(value.get("date")));
                club.setText(String.valueOf(value.get("favoriteClub")));

                getDateFromDatabase = String.valueOf(value.get("premiumDate"));
                try {
                    currentDateOfUser = dateFormat.parse(getDateFromDatabase);
                    calendar.setTime(currentDateOfUser);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calendar.add(Calendar.DAY_OF_MONTH, 15);
                trialDate = calendar.getTime();
                trialDateString = dateFormat.format(trialDate);
                mReference.child("trialPremiumDate").setValue(trialDateString);

                premiumTrialDate.setText(trialDateString);


                if (dateRightNow.equals(trialDate) || dateRightNow.after(trialDate)) {
                    premiumTextview.setVisibility(View.GONE);
                    premiumLayout.setVisibility(View.GONE);
                    premiumLinija.setVisibility(View.GONE);
                    mReference.child("premium").setValue(false);

                    Log.i("proba", "isti su");
                }


                flagImageFirebase = String.valueOf(value.get("flag"));
                Log.i("flag uri", flagImageFirebase);

                clubLogoFirebase = String.valueOf(value.get("favoriteClubLogo"));

                flag.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                logoClub.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;

                requestBuilder = Glide
                        .with(getActivity())
                        .using(Glide.buildStreamModelLoader(Uri.class, getActivity()), InputStream.class)
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


                Picasso.with(ProfileFragment.this.getActivity()).load(clubLogoFirebase).into(logoClub);

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

                country.setText(String.valueOf(value.get("country")));

                backgroundImage();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;

    }

    public void backgroundImage() {

   /* HttpImageRequestTask task = new HttpImageRequestTask();
        try {
            task.execute(flagImageFirebase).get();
            backgroundImage.setLayerType(View.LAYER_TYPE_SOFTWARE,null);


        } catch (InterruptedException e) {
            Log.e("error1",e.getLocalizedMessage());
        } catch (ExecutionException e) {
            Log.e("error2", e.getLocalizedMessage());
        }*/

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
            profile.setAlpha(0.9f);
            imageUri = data.getData();
            profile.setImageURI(imageUri);

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(getContext(), this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                Picasso.with(getActivity()).load(resultUri)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.profilimage).error(R.mipmap.ic_launcher)
                        .into(profile);
                profileImageUpdate = mFilePath.child("Profile_Image").child(resultUri.getLastPathSegment());
                profileImageUpdate.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri = taskSnapshot.getDownloadUrl();

                        mReference = mDatabase.getReference().child("Users").child(uid);
                        if (downloadUri != null)
                            mReference.child("profileImage").setValue(downloadUri.toString());
                    }
                });
                hasSetProfileImage = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.i("errorCrop", String.valueOf(error));
            }
        }
        if (requestCode == BACKGROUND_REQUEST && resultCode == RESULT_OK) {

            backgroundImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            backgroundImage.setAlpha(0.9f);
            backgroundUri = data.getData();
            backgroundImage.setImageURI(backgroundUri);
        }


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//kom
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.profile_menu, menu);
        menu.clear();

        super.onCreateOptionsMenu(menu, inflater);
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
}


