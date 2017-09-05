package com.malikbisic.sportapp.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.SvgDrawableTranscoder;
import com.mukesh.countrypicker.fragments.CountryPicker;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class EnterUsernameForApp extends AppCompatActivity implements View.OnClickListener {

    private EditText enterUsername;
    private Button continueBtn;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private boolean valid = true;
    private Spinner genderItems;
    private List<String> spinnerArray;
    private ArrayAdapter<String> adapter;
    private int selectYear;
    private int currentYear;
    private int realYear;
    private Calendar minAdultAge;
    private ImageView addImage;
    private TextView favoriteClub;
    private TextView usernameErrorTxt;
    private TextView birthdayErrorTxt;
    private TextView clubError;
    private TextView countryError;
    private TextView selectCountry;
    private CircleImageView clubLogoImage;
    String googleUser_id;
    String googleFirstName;
    String googleLastName;
    private String gender;
    String loginUserid;
    private Uri imageUri;
    private String username;
    private String userDate;
    private String favoriteClubString;
    private String countryString;

    private ProgressDialog mDialog;
    private static final String TAG = "EnterUsernameForApp";


    private boolean hasSetProfileImage = false;
    private EditText birthday;
    private ArrayList<String> usernameList;
    private static final int GALLERY_REQUEST = 2;
    private static final int RESULT_COUNTRY = 5;
    private static final int RESULT_CLUB = 6;
    String uid;
    Intent  getClubNameAndLogo;
    CircleImageView countryImage;
    private StorageReference mFilePath;
    private FirebaseStorage mStorage;
    private StorageReference profileImageRef;
    private StorageReference countryFlag;
    private Uri resultUri = null;
    String todayDateTime;
    CountryPicker picker;
    String imageOfCountry;
    String nameOfCountry;

    String clubName;
    String clubLogo;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_username);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        enterUsername = (EditText) findViewById(R.id.usernameSetUp);
        birthday = (EditText) findViewById(R.id.dateSetUp);
        genderItems = (Spinner) findViewById(R.id.genderSetUp);
        usernameErrorTxt = (TextView) findViewById(R.id.input_usernameError);
        birthdayErrorTxt = (TextView) findViewById(R.id.input_BirthdayError);
        favoriteClub = (TextView) findViewById(R.id.favoriteClubEnterId);
        clubError = (TextView) findViewById(R.id.input_cluberror);
        countryError = (TextView) findViewById(R.id.input_counryError);
        usernameList = new ArrayList<>();
        continueBtn = (Button) findViewById(R.id.continueToMainPage);
        selectCountry = (TextView) findViewById(R.id.countrySelect);
        clubLogoImage = (CircleImageView) findViewById(R.id.clubLogo);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDialog = new ProgressDialog(this);
        addImage = (ImageView) findViewById(R.id.addImage);
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        countryImage = (CircleImageView) findViewById(R.id.country_image_get);
        favoriteClub.setOnClickListener(this);

        getClubNameAndLogo = getIntent();
        clubName = getClubNameAndLogo.getStringExtra("clubName");
        clubLogo = getClubNameAndLogo.getStringExtra("clubLogo");

        favoriteClub.setText(clubName);
        Picasso.with(this).load(clubLogo).into(clubLogoImage);

        Locale locale = Locale.ENGLISH;
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getApplicationContext().getApplicationContext().getResources().updateConfiguration(config, null);



        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
        dateFormat.setLenient(false);
        Date today = new Date();
        todayDateTime = dateFormat.format(today);


        mFilePath = FirebaseStorage.getInstance().getReference(); //mStorage.getReferenceFromUrl("gs://sportapp-11328.appspot.com");

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    uid = user.getUid();

                }

            }
        };

        selectCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnterUsernameForApp.this, SearchableCountry.class);
                startActivityForResult(intent, RESULT_COUNTRY);

            }
        });

        spinnerArray = new ArrayList<>();
        spinnerArray.add("Male");
        spinnerArray.add("Female");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderItems.setAdapter(adapter);
        continueBtn.setOnClickListener(this);
        birthday.setOnClickListener(this);
        minAdultAge = new GregorianCalendar();
        googleUser_id = LoginActivity.gUserId;
        googleFirstName = LoginActivity.gFirstName;
        googleLastName = LoginActivity.gLastName;
        loginUserid = LoginActivity.userIdLogin;

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGallery = new Intent(Intent.ACTION_GET_CONTENT);
                openGallery.setType("image/*");
                startActivityForResult(openGallery, GALLERY_REQUEST);
            }
        });

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Usernames");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject object : objects) {
                        usernameList.add(String.valueOf(object.get("username")));
                    }
                }
            }
        });



        genderItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gender = String.valueOf(adapterView.getItemAtPosition(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            addImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            addImage.setAlpha(254);


            imageUri = data.getData();
            addImage.setImageURI(imageUri);


            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                Picasso.with(getApplicationContext()).load(resultUri)
                        .placeholder(R.drawable.profilimage).error(R.mipmap.ic_launcher)
                        .into(addImage);
                hasSetProfileImage = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (requestCode == RESULT_COUNTRY && resultCode == RESULT_OK) {


            nameOfCountry = data.getStringExtra("countryName");
            imageOfCountry = data.getStringExtra("countryImg");
            selectCountry.setText(nameOfCountry);

            GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;

            requestBuilder = Glide
                    .with(getApplicationContext())
                    .using(Glide.buildStreamModelLoader(Uri.class, getApplicationContext()), InputStream.class)
                    .from(Uri.class)
                    .as(SVG.class)
                    .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                    .sourceEncoder(new StreamEncoder())
                    .cacheDecoder(new FileToStreamDecoder<SVG>(new SearchableCountry.SvgDecoder()))
                    .decoder(new SearchableCountry.SvgDecoder())
                    .animate(android.R.anim.fade_in);
            if (imageOfCountry != null) {

                Uri uri = Uri.parse(imageOfCountry);

                requestBuilder
                        // SVG cannot be serialized so it's not worth to cache it
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .load(uri)
                        .into(countryImage);

            }


        }

        if (requestCode == RESULT_CLUB && resultCode == RESULT_OK) {

            clubName = data.getStringExtra("clubName");
            clubLogo = data.getStringExtra("clubLogo");

            favoriteClub.setText(clubName);
            Picasso.with(this).load(clubLogo).into(clubLogoImage);
        }
    }


    private void updateLabel() {

        String myFormat = "dd/MMMM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        birthday.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.continueToMainPage) {

            signup();

        } else if (v.getId() == R.id.dateSetUp) {
            new DatePickerDialog(EnterUsernameForApp.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();

        }

        if (v.getId() == R.id.favoriteClubEnterId) {

            Intent openLeague = new Intent(EnterUsernameForApp.this, SelectLeagueActivity.class);
            startActivityForResult(openLeague, RESULT_CLUB);

        }

    }


    public boolean valid() {
        valid = true;

        if (!hasSetProfileImage) {
            Toast.makeText(EnterUsernameForApp.this, "You need to set profile image", Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            valid = true;
        }

        String username = enterUsername.getText().toString().trim();
        if (TextUtils.isEmpty(enterUsername.getText().toString())) {
            usernameErrorTxt.setText("Field can not be blank");
            usernameErrorTxt.setVisibility(View.VISIBLE);
            valid = false;
        } else if (usernameList.contains(username)) {
            usernameErrorTxt.setText("Username already exists");
            usernameErrorTxt.setVisibility(View.VISIBLE);
            valid = false;
        } else if (username.length() < 3 || username.length() > 8) {

            usernameErrorTxt.setText("Username must be between 3 and 8 characters!");
            usernameErrorTxt.setVisibility(View.VISIBLE);
        } else {
            usernameErrorTxt.setText("");
            usernameErrorTxt.setVisibility(View.GONE);

        }

        if (selectCountry.getText().toString().isEmpty()) {
            countryError.setText("Field can not be empty");
            countryError.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            countryError.setText("");
            countryError.setVisibility(View.GONE);
        }

        if (realYear < 13) {

            birthdayErrorTxt.setText("You must be older than 13!");
            birthdayErrorTxt.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            usernameErrorTxt.setText("");
            birthdayErrorTxt.setVisibility(View.GONE);
        }

        if (favoriteClub.getText().toString().isEmpty()) {
            clubError.setText("Field can not be blank");
            clubError.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            clubError.setText("");
            clubError.setVisibility(View.GONE);
        }

        return valid;
    }

    public void googleEnterDatabase() {


        username = enterUsername.getText().toString().trim();
        userDate = birthday.getText().toString().trim();
        favoriteClubString = favoriteClub.getText().toString().trim();
        countryString = selectCountry.getText().toString().trim();


        profileImageRef = mFilePath.child("Profile_Image").child(resultUri.getLastPathSegment());
        mDialog.setMessage("Registering...");
        mDialog.show();


        profileImageRef.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                final Uri downloadUrl = taskSnapshot.getDownloadUrl();


                mReference = mDatabase.getReference().child("Users").child(uid);
                mReference.child("name").setValue(googleFirstName);
                mReference.child("surname").setValue(googleLastName);
                mReference.child("username").setValue(username);
                mReference.child("date").setValue(userDate);
                mReference.child("gender").setValue(gender);
                if (downloadUrl != null)
                mReference.child("profileImage").setValue(downloadUrl.toString());
                mReference.child("country").setValue(countryString);
                mReference.child("flag").setValue(imageOfCountry);
                mReference.child("favoriteClub").setValue(favoriteClubString);
                mReference.child("favoriteClubLogo").setValue(clubLogo);
                mReference.child("userID").setValue(uid);
                mReference.child("premium").setValue(true);
                mReference.child("premiumDate").setValue(todayDateTime);

                DatabaseReference usersChat = FirebaseDatabase.getInstance().getReference().child("UsersChat").child(favoriteClubString).child(uid);

                usersChat.child("username").setValue(username);
                if (downloadUrl != null)
                    mReference.child("profileImage").setValue(downloadUrl.toString());
                usersChat.child("country").setValue(countryString);
                usersChat.child("flag").setValue(imageOfCountry);
                usersChat.child("favoriteClub").setValue(favoriteClubString);
                usersChat.child("favoriteClubLogo").setValue(clubLogo);
                usersChat.child("userID").setValue(uid);
                usersChat.child("online").setValue(true);



                ParseObject object = new ParseObject("Usernames");
                object.put("username", username);
                object.saveInBackground();
                mDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(EnterUsernameForApp.this, e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });


    }

    public void signup() {
        Log.d(TAG, "RegisterActivity");
        if (!valid()) {

            return;
        }

        continueBtn.setEnabled(false);

        mDialog = new ProgressDialog(EnterUsernameForApp.this,
                R.style.AppTheme_Dark_Dialog);
        mDialog.setIndeterminate(true);
        mDialog.setMessage("Creating Account...");
        mDialog.show();

        if (LoginActivity.checkGoogleSignIn) {
            googleEnterDatabase();

        }
        if (LoginActivity.checkLoginPressed) {

            loginEnterDatabase();
        }


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {


                        onSignupSuccess();

                        mDialog.dismiss();
                    }
                }, 4000);
    }

    public void onSignupSuccess() {
        continueBtn.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(EnterUsernameForApp.this, MainPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void loginEnterDatabase() {
        username = enterUsername.getText().toString().trim();
        userDate = birthday.getText().toString().trim();
        favoriteClubString = favoriteClub.getText().toString().trim();
        countryString = selectCountry.getText().toString().trim();

        profileImageRef = mFilePath.child("Profile_Image").child(resultUri.getLastPathSegment());
        mDialog.setMessage("Registering...");
        mDialog.show();


        profileImageRef.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                final Uri downloadUrl = taskSnapshot.getDownloadUrl();


                mReference = mDatabase.getReference().child("Users").child(uid);
                mReference.child("username").setValue(username);
                mReference.child("date").setValue(userDate);
                mReference.child("gender").setValue(gender);
                if (downloadUrl != null)
                mReference.child("profileImage").setValue(downloadUrl.toString());
                mReference.child("country").setValue(countryString);
                mReference.child("flag").setValue(imageOfCountry);
                mReference.child("favoriteClub").setValue(clubName);
                mReference.child("favoriteClubLogo").setValue(clubLogo);
                mReference.child("userID").setValue(uid);
                mReference.child("premium").setValue(true);
                mReference.child("premiumDate").setValue(todayDateTime);

                DatabaseReference usersChat = FirebaseDatabase.getInstance().getReference().child("UsersChat").child(favoriteClubString).child(uid);

                usersChat.child("username").setValue(username);
                if (downloadUrl != null)
                    mReference.child("profileImage").setValue(downloadUrl.toString());
                usersChat.child("country").setValue(countryString);
                usersChat.child("flag").setValue(imageOfCountry);
                usersChat.child("favoriteClub").setValue(favoriteClubString);
                usersChat.child("favoriteClubLogo").setValue(clubLogo);
                usersChat.child("userID").setValue(uid);
                usersChat.child("online").setValue(true);

                ParseObject object = new ParseObject("Usernames");
                object.put("username", username);
                object.saveInBackground();
                mDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(EnterUsernameForApp.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent backLogin = new Intent(EnterUsernameForApp.this, LoginActivity.class);
        startActivity(backLogin);
    }
}
