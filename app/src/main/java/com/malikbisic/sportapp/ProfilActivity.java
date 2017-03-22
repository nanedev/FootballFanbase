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
            Uri imageUri = data.getData();
            addImage.setImageURI(imageUri);
            Picasso.with(this).load(imageUri).into(addImage);
        }
    }
}
