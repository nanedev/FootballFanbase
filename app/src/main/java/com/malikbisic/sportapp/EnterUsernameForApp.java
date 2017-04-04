package com.malikbisic.sportapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mukesh.countrypicker.fragments.CountryPicker;
import com.mukesh.countrypicker.interfaces.CountryPickerListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class EnterUsernameForApp extends AppCompatActivity implements View.OnClickListener {
    private EditText enterUsername;
    private Button contunueBtn;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private String value;
    private boolean valid = true;

    private Spinner genderItems;
    private List<String> spinnerArray;
    private ArrayAdapter<String> adapter;
    private int selectYear;
    private int currentYear;
    private int realYear;
    private Calendar minAdultAge;
    private ImageView addImage;
    private EditText favoriteClub;

    private TextView usernameErrorTxt;
    private TextView birthdayErrorTxt;
    private EditText selectCountry;

    private String googleUser_id;
    private String googleFirstName;
    private String googleLastName;
    private String gender;
    private String loginUserid;

    private ProgressDialog mDialog;

    private String fbFirstName;
    private String fbLastName;

    private boolean hasSetProfileImage = false;
    private EditText birthday;
    private ArrayList<String> usernameList;
    private static final int GALLERY_REQUEST = 2;
    String uid;

    private StorageReference mFilePath;
    private FirebaseStorage mStorage;
    private Uri resultUri = null;
    Bitmap bitmap;
    CountryPicker picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_username);
        enterUsername = (EditText) findViewById(R.id.usernameSetUp);
        birthday = (EditText) findViewById(R.id.dateSetUp);
        genderItems = (Spinner) findViewById(R.id.genderSetUp);
        usernameErrorTxt = (TextView) findViewById(R.id.input_usernameError);
        birthdayErrorTxt = (TextView) findViewById(R.id.input_BirthdayError);
        favoriteClub = (EditText) findViewById(R.id.favoriteClubEnterId);
        usernameList = new ArrayList<>();
        contunueBtn = (Button) findViewById(R.id.continueToMainPage);
        selectCountry = (EditText) findViewById(R.id.countrySelect);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDialog = new ProgressDialog(this);
        addImage = (ImageView) findViewById(R.id.addImage);
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();

        mFilePath = FirebaseStorage.getInstance().getReference(); //mStorage.getReferenceFromUrl("gs://sportapp-11328.appspot.com");

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    uid = user.getUid();
                    String name = user.getDisplayName();
                    String provider = user.getProviderId();
                    String email = user.getEmail();


                    Log.i("proba", uid);
                    // Log.i("proba", name);
                    //Log.i("proba", provider);
                    Log.i("proba", email);
                } else {

                }

            }
        };

        selectCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker = picker = CountryPicker.newInstance("Select Country");
                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
                picker.setListener(new CountryPickerListener() {
                    @Override
                    public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                        picker.dismiss();
                        int imageCountry = flagDrawableResID;
                        Log.i("image", String.valueOf(imageCountry));
                        selectCountry.setCompoundDrawablesWithIntrinsicBounds(imageCountry, 0, 0, 0);
                        selectCountry.setText(name);
                        // Implement your code here

                        bitmap = BitmapFactory.decodeResource(getResources(), imageCountry);


                    }
                });
            }
        });

        spinnerArray = new ArrayList<>();
        spinnerArray.add("Male");
        spinnerArray.add("Female");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        genderItems.setAdapter(adapter);

        contunueBtn.setOnClickListener(this);
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


            Uri imageUri = data.getData();
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
    }


    private void updateLabel() {

        String myFormat = "dd/MMMM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        birthday.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.continueToMainPage) {

            if (valid()) {
                if (LoginActivity.checkgoogleSignIn) {
                    googleEnterDatabase();
                    Intent intent = new Intent(EnterUsernameForApp.this, MainPage.class);
                    startActivity(intent);
                }
                if (LoginActivity.checkLoginPressed) {

                    loginEnterDatabase();
                }


            } else {
                mDialog.dismiss();
            }

        } else if (v.getId() == R.id.dateSetUp) {
            new DatePickerDialog(EnterUsernameForApp.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();

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
            usernameErrorTxt.setText("Username already exists,can not continue!");
            usernameErrorTxt.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            usernameErrorTxt.setText("");
            usernameErrorTxt.setVisibility(View.INVISIBLE);

        }

        if (realYear < 13) {

            birthdayErrorTxt.setText("You must be older than 13!");
            birthdayErrorTxt.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            usernameErrorTxt.setText("");
            birthdayErrorTxt.setVisibility(View.INVISIBLE);
        }

        return valid;
    }

    public void googleEnterDatabase() {

        final String username = enterUsername.getText().toString().trim();
        final String userDate = birthday.getText().toString().trim();
        final String favoriteClubString = favoriteClub.getText().toString().trim();
        final String countryString = selectCountry.getText().toString().trim();

        StorageReference imageRef = mFilePath.child("Profile_Image").child(resultUri.getLastPathSegment());
        mDialog.setMessage("Registering...");
        mDialog.show();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] data = baos.toByteArray();

        imageRef.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                StorageReference countryFlag = mFilePath.child("Country_Flag").child(String.valueOf(bitmap.getGenerationId()));

                UploadTask uploadTask = countryFlag.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadFlagUri = taskSnapshot.getDownloadUrl();

                        mReference = mDatabase.getReference().child("Users").child(uid);
                        mReference.child("username").setValue(username);
                        mReference.child("date").setValue(userDate);
                        mReference.child("gender").setValue(gender);
                        mReference.child("profileImage").setValue(downloadUrl.toString());
                        mReference.child("country").setValue(countryString);
                        mReference.child("flag").setValue(downloadFlagUri.toString());
                        mReference.child("favoriteClub").setValue(favoriteClubString);


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
        });


    }

    public void loginEnterDatabase() {
        final String username = enterUsername.getText().toString().trim();
        final String userDate = birthday.getText().toString().trim();
        final String favoriteClubString = favoriteClub.getText().toString().trim();
        final String countryString = selectCountry.getText().toString().trim();

        StorageReference imageRef = mFilePath.child("Profile_Image").child(resultUri.getLastPathSegment());
        mDialog.setMessage("Registering...");
        mDialog.show();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] data = baos.toByteArray();

        imageRef.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                StorageReference countryFlag = mFilePath.child("Country_Flag").child(String.valueOf(bitmap.getGenerationId()));

                UploadTask uploadTask = countryFlag.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadFlagUri = taskSnapshot.getDownloadUrl();

                        mReference = mDatabase.getReference().child("Users").child(uid);
                        mReference.child("username").setValue(username);
                        mReference.child("date").setValue(userDate);
                        mReference.child("gender").setValue(gender);
                        mReference.child("profileImage").setValue(downloadUrl.toString());
                        mReference.child("country").setValue(countryString);
                        mReference.child("flag").setValue(downloadFlagUri.toString());
                        mReference.child("favoriteClub").setValue(favoriteClub.getText().toString().trim());


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

}
