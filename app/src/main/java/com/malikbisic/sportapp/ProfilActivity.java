package com.malikbisic.sportapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ProfilActivity extends AppCompatActivity {
    private ImageView addImage;
    private TextView profileUsername;
    private TextView profileDate;
    private TextView profileGender;
    private Button profilBtnContinue;
    private static final int GALLERY_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        addImage = (ImageView) findViewById(R.id.imageIdProfil);
        profileUsername = (TextView) findViewById(R.id.username_profil);
        profileDate = (TextView) findViewById(R.id.date_profil);
        profileGender = (TextView) findViewById(R.id.gender_profil);
        profilBtnContinue = (Button) findViewById(R.id.profil_tbn_continue);


        boolean firsttime = getSharedPreferences("preference", MODE_PRIVATE).getBoolean("isfirsttime", true);

        if (firsttime == true){
            getSharedPreferences("preference", MODE_PRIVATE).edit().putBoolean("isfirsttime", false).commit();
        } else {
            Intent newsFeed = new Intent(ProfilActivity.this, SetUpAccount.class);
            startActivity(newsFeed);
        }

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGallery = new Intent(Intent.ACTION_GET_CONTENT);
                openGallery.setType("image/*");
                startActivityForResult(openGallery, GALLERY_REQUEST);
            }
        });
    }

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
                Uri resultUri = result.getUri();
                Picasso.with(getApplicationContext()).load(resultUri)
                        .placeholder(R.drawable.profilimage).error(R.mipmap.ic_launcher)
                        .into(addImage);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
