package com.malikbisic.sportapp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.transition.Visibility;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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
    private EditText player;
    private RelativeLayout layout;
    private Uri resultUri = null;
    private boolean hasSetProfileImage = false;

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
        layout = (RelativeLayout) view.findViewById(R.id.relaiveLayoutBackgroudnProfile);


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

                String flagImageFirebase = String.valueOf(value.get("flag"));

                Picasso.with(getActivity())
                        .load(flagImageFirebase)
                        .into(flag);
               country.setText(String.valueOf(value.get("country")));

                if (country.getText().toString().equals("Bosnia and Herzegovina")){
                    layout.setBackgroundResource(R.drawable.bihflag);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
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

                profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent openGallery = new Intent(Intent.ACTION_GET_CONTENT);
                        openGallery.setType("image/*");
                        startActivityForResult(openGallery, GALLERY_REQUEST);
                    }
                });

                username.setFocusable(true);
                username.setEnabled(true);
                username.setClickable(true);
                username.setCursorVisible(true);
                username.setFocusableInTouchMode(true);;
                username.setInputType(InputType.TYPE_CLASS_TEXT);
                username.requestFocus();

            } else if (item.getTitle().equals("Save")) {
                item.setTitle("Edit Profile");
                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

            }

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            profile.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            profile.setAlpha(254);


            Uri imageUri = data.getData();
            profile.setImageURI(imageUri);


            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(getActivity());

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
            }
        }
    }
}


