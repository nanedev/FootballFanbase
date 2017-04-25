package com.malikbisic.sportapp;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
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
import android.widget.ProgressBar;
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
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private ImageView profile;
    private ImageView flag;
    private TextView username;
    private TextView gender;
    private TextView birthday;
    private TextView country;
    private TextView club;
    private TextView name_surname;

    private Calendar minAdultAge;
    private BitmapDrawable obwer;
    private ProgressBar loadProfile_image;
    private String uid;
    private static final int GALLERY_REQUEST = 1;
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
    TextView editProfilePicture;
    String profileImage;
    String flagImageFirebase;
    Uri imageUri;
    ImageView backgroundImage;



    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setHasOptionsMenu(false);

        mDatabase = FirebaseDatabase.getInstance();
        uid = MainPage.uid;
        mReference = mDatabase.getReference().child("Users").child(uid);
        profile = (ImageView) view.findViewById(R.id.get_profile_image_id);
        name_surname = (TextView) view.findViewById(R.id.name_surname);
        flag = (ImageView) view.findViewById(R.id.user_countryFlag);
        username = (TextView) view.findViewById(R.id.user_username);
        gender = (TextView) view.findViewById(R.id.user_gender);
        birthday = (TextView) view.findViewById(R.id.user_date);
        country = (TextView) view.findViewById(R.id.user_country);
        club = (TextView) view.findViewById(R.id.user_club);
        editProfilePicture = (TextView) view.findViewById(R.id.edit_profile_image);
        backgroundImage = (ImageView) view.findViewById(R.id.backgroundProfile);
        usernameList = new ArrayList<>();
        mFilePath = FirebaseStorage.getInstance().getReference();
        dialog = new ProgressDialog(getContext());
        loadProfile_image = (ProgressBar) view.findViewById(R.id.loadingProfileImageProgressBar);;
        minAdultAge = new GregorianCalendar();
        editProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = {"Open gallery"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dark_Dialog);
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (items[i].equals("Open gallery")){
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
                .setColorFilter(ContextCompat.getColor(getContext(), R.color.redError), PorterDuff.Mode.SRC_IN );
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
                name_surname.setText(name);
                username.setText(String.valueOf(value.get("username")));
                gender.setText(String.valueOf(value.get("gender")));
                birthday.setText(String.valueOf(value.get("date")));
                club.setText(String.valueOf(value.get("favoriteClub")));

                flagImageFirebase = String.valueOf(value.get("flag"));
                Log.i("flag uri", flagImageFirebase);

                Picasso.Builder builder = new Picasso.Builder(getContext());
                builder.listener(new Picasso.Listener()
                {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
                    {
                        exception.printStackTrace();
                        Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                builder.build().load(flagImageFirebase).into(flag);

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
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Flags");
        query.whereEqualTo("country", country.getText().toString().trim());
        Log.i("countr", String.valueOf(country.getText()));
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
                                    backgroundImage.setImageDrawable(obwer);


                                } else {
                                    Toast.makeText(ProfileFragment.this.getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                    }
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
        return super.onOptionsItemSelected(item);
    }



}


