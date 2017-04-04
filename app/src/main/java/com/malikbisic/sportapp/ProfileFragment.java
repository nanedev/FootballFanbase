package com.malikbisic.sportapp;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mukesh.countrypicker.fragments.CountryPicker;
import com.mukesh.countrypicker.interfaces.CountryPickerListener;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener{

    private ImageView profile;
    private ImageView flag;
    private TextView username;
    private TextView gender;
    private TextView birthday;
    private TextView country;
    private TextView club;
    private TextView usernameError;
    private TextView countryError;
    private TextView birthdayError;
    private TextView clubError;
    private EditText player;
    private ArrayList<String> usernameList;
    private RelativeLayout layout;
    private Uri resultUri;
    private boolean hasSetProfileImage = false;
    private int selectYear;
    private int currentYear;
    private int realYear;
    private Calendar minAdultAge;
    private boolean valid;
    private Bitmap bitmap;
    CountryPicker picker;
    private StorageReference mFilePath;
    private ProgressDialog dialog;


    private String uid;
    private static final int GALLERY_REQUEST = 1;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setHasOptionsMenu(true);

        mDatabase = FirebaseDatabase.getInstance();
        uid = MainPage.uid;
        mReference = mDatabase.getReference().child("Users").child(uid);

        profile = (ImageView) view.findViewById(R.id.get_profile_image_id);
        flag = (ImageView) view.findViewById(R.id.flagImageView);
        username = (TextView) view.findViewById(R.id.username_id);
        gender = (TextView) view.findViewById(R.id.gender_id);
        birthday = (TextView) view.findViewById(R.id.birthday_id);
        country = (TextView) view.findViewById(R.id.countryId);
        club = (TextView) view.findViewById(R.id.footballClubId);
        player = (EditText) view.findViewById(R.id.footballPlayerId);
        usernameError = (TextView) view.findViewById(R.id.usernameError_Profile);
        countryError = (TextView) view.findViewById(R.id.coutryError_Profile);
        birthdayError = (TextView) view.findViewById(R.id.birthdayError_Profile);
        clubError = (TextView) view.findViewById(R.id.clubError_Profile);
        layout = (RelativeLayout) view.findViewById(R.id.relaiveLayoutBackgroudnProfile);
        usernameList = new ArrayList<>();

        mFilePath = FirebaseStorage.getInstance().getReference();

        dialog = new ProgressDialog(getContext());

        minAdultAge = new GregorianCalendar();


        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();


                String profielImage = String.valueOf(value.get("profileImage"));
                Picasso.with(getActivity())
                        .load(profielImage)
                        .into(profile);
                username.setText(String.valueOf(value.get("username")));
                gender.setText(String.valueOf(value.get("gender")));
                birthday.setText(String.valueOf(value.get("date")));
                club.setText(String.valueOf(value.get("favoriteClub")));

                if (value.get("favoritePlayer") != null){
                    player.setText(String.valueOf(value.get("favoritePlayer")));
                }

                String flagImageFirebase = String.valueOf(value.get("flag"));

                Picasso.with(getActivity())
                        .load(flagImageFirebase)
                        .into(flag);
               country.setText(String.valueOf(value.get("country")));

                if (country.getText().toString().equals("Bosnia and Herzegovina")){
                    layout.setBackgroundResource(R.drawable.bihflag);
                } else if (country.getText().toString().equals("Belgium")) {
                    layout.setBackgroundResource(R.drawable.belgium);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;

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

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            profile.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            profile.setAlpha(254);


            Uri imageUri = data.getData();
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
                        .placeholder(R.drawable.profilimage).error(R.mipmap.ic_launcher)
                        .into(profile);
                hasSetProfileImage = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.i("errorCrop", String.valueOf(error));
            }
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.profile_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.editProfileId) {

            if (item.getTitle().equals("Edit Profile")){
                item.setTitle("Save");
                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

                profile.setOnClickListener(this);

                username.setFocusable(true);
                username.setEnabled(true);
                username.setClickable(true);
                username.setCursorVisible(true);
                username.setFocusableInTouchMode(true);;
                username.setInputType(InputType.TYPE_CLASS_TEXT);
                username.requestFocus();

                birthday.setOnClickListener(this);

                country.setOnClickListener(this);

                club.setFocusable(true);
                club.setEnabled(true);
                club.setClickable(true);
                club.setCursorVisible(true);
                club.setFocusableInTouchMode(true);;
                club.setInputType(InputType.TYPE_CLASS_TEXT);
                club.requestFocus();

                player.setFocusable(true);


            } else if (item.getTitle().equals("Save")) {




                if (valid()){
                    item.setTitle("Edit Profile");
                    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                    username.setFocusable(false);
                    username.setEnabled(false);
                    username.setClickable(false);
                    username.setCursorVisible(false);
                    username.setFocusableInTouchMode(false);;

                    birthday.setClickable(false);

                    country.setClickable(false);

                    club.setFocusable(false);
                    club.setEnabled(false);
                    club.setClickable(false);
                    club.setCursorVisible(false);
                    club.setFocusableInTouchMode(false);
                    player.setFocusable(false);

                    final String usernameString = username.getText().toString().trim();
                    final String userDate = birthday.getText().toString().trim();
                    final String favoriteClubString = club.getText().toString().trim();
                    final String countryString = country.getText().toString().trim();

                    StorageReference imageRef = mFilePath.child("Profile_Image").child(resultUri.getLastPathSegment());
                    dialog.setMessage("Registering...");
                    dialog.show();

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
                                    mReference.child("username").setValue(usernameString);
                                    mReference.child("date").setValue(userDate);
                                    mReference.child("gender").setValue(gender);
                                    mReference.child("profileImage").setValue(downloadUrl.toString());
                                    mReference.child("country").setValue(countryString);
                                    mReference.child("flag").setValue(downloadFlagUri.toString());
                                    mReference.child("favoriteClub").setValue(favoriteClubString);
                                    mReference.child("favoritePlayer").setValue(player.getText().toString().trim());


                                    ParseObject object = new ParseObject("Usernames");
                                    object.put("username", username);
                                    object.saveInBackground();
                                    dialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();

                                }
                            });
                        }
                    });

                }

            }

        }

        return super.onOptionsItemSelected(item);
    }

    public boolean valid() {
        valid = true;

        if (!hasSetProfileImage ) {
            Toast.makeText(getActivity(), "You need to set profile image", Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            valid = true;
        }

        ;
        if (TextUtils.isEmpty(username.getText().toString())) {
            usernameError.setText("Field can not be blank");
            usernameError.setVisibility(View.VISIBLE);
            valid = false;
        } else if (usernameList.contains(username)) {
            usernameError.setText("Username already exists,can not continue!");
            usernameError.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            usernameError.setText("");
            usernameError.setVisibility(View.GONE);

        }

        if (realYear < 13) {

            birthdayError.setText("You must be older than 13!");
            birthdayError.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            birthdayError.setText("");
            birthdayError.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(country.getText().toString())){
            countryError.setText("Field can to be blank");
            countryError.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            countryError.setText("");
            countryError.setVisibility(View.GONE);
        }



        return valid;
    }



    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.birthday_id){
            new DatePickerDialog(getActivity(), date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();

        } else if (view.getId() == R.id.countryId){
            picker  = CountryPicker.newInstance("Select Country");
            picker.show(getActivity().getSupportFragmentManager(), "COUNTRY_PICKER");
            picker.setListener(new CountryPickerListener() {
                @Override
                public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                    picker.dismiss();
                    int imageCountry = flagDrawableResID;
                    Log.i("image", String.valueOf(imageCountry));
                    flag.setImageResource(imageCountry);
                    country.setText(name);
                    // Implement your code here

                    bitmap = BitmapFactory.decodeResource(getResources(), imageCountry);


                }
            });
        } else if (view.getId() == R.id.get_profile_image_id){

            profile.setImageResource(0);
            Intent openGallery = new Intent(Intent.ACTION_GET_CONTENT);
            openGallery.setType("image/*");
            startActivityForResult(openGallery, GALLERY_REQUEST);
        }
    }
}


