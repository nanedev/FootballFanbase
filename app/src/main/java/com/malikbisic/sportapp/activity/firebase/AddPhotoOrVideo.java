package com.malikbisic.sportapp.activity.firebase;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;

import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.StopAppServices;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import cn.jzvd.JZVideoPlayerStandard;
import id.zelory.compressor.Compressor;

import static com.iceteck.silicompressorr.FileUtils.getDataColumn;
import static com.iceteck.silicompressorr.FileUtils.isExternalStorageDocument;
import static com.iceteck.silicompressorr.FileUtils.isGooglePhotosUri;
import static com.iceteck.silicompressorr.Util.isMediaDocument;
import static com.malikbisic.sportapp.BuildConfig.DEBUG;

public class AddPhotoOrVideo extends AppCompatActivity implements View.OnClickListener,EmojiconsFragment.OnEmojiconBackspaceClickedListener,EmojiconGridFragment.OnEmojiconClickedListener {

    private Intent myIntent;
    private ImageView photoSelected;
    private JZVideoPlayerStandard videoSelected;
    private EmojiconEditText saySomething;

    ProgressDialog pDialog;
    ProgressDialog postingDialog;

    private StorageReference mFilePath;
    private StorageReference photoPost;
    private StorageReference postVideo;
    private FirebaseStorage mStorage;

    FirebaseFirestore postingCollection;
    Animation slideUpAnimation;

    private FirebaseAuth mAuth;
    private static final String TAG = "AddPhotoOrVideo";
    String videoSize;
    RelativeLayout layout;
Toolbar addPhotoVideoToolbar;
FrameLayout emoticonsPhoto;
ImageView smajlic;
RelativeLayout layoutVideoPhot;
    boolean firstClickSmile = true;
    boolean secondClickSmile = false;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo_or_video);
        Intent closeAPP = new Intent(this, StopAppServices.class);
        startService(closeAPP);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        myIntent = getIntent();
        mStorage = FirebaseStorage.getInstance();
        postingCollection = FirebaseFirestore.getInstance();
        photoSelected = (ImageView) findViewById(R.id.post_image);
        emoticonsPhoto = (FrameLayout)findViewById(R.id.emojiconsPhotoVideo);
        videoSelected = (cn.jzvd.JZVideoPlayerStandard) findViewById(R.id.post_video);
        saySomething = (EmojiconEditText) findViewById(R.id.tell_something_about_video_image);
        postingDialog = new ProgressDialog(this,R.style.AppTheme_Dark_Dialog);
        mAuth = FirebaseAuth.getInstance();
        smajlic = (ImageView) findViewById(R.id.smileinphoto);
        layout = (RelativeLayout) findViewById(R.id.container);
        addPhotoVideoToolbar = (Toolbar) findViewById(R.id.addPhotoVideoToolbar);
        layoutVideoPhot = (RelativeLayout) findViewById(R.id.layoutvideophoto);
        setSupportActionBar(addPhotoVideoToolbar);
        getSupportActionBar().setTitle("Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFilePath = FirebaseStorage.getInstance().getReference();

        if (MainPage.photoSelected) {
            videoSelected.setVisibility(View.GONE);
            photoSelected.setVisibility(View.VISIBLE);
            photoSelected.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            Uri imageUri = myIntent.getData();
            slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.anmation_drom_down_to_top);

            Picasso
                    .with(AddPhotoOrVideo.this)
                    .load(imageUri)
                    .fit()
                    .centerCrop()
                    // call .centerInside() or .centerCrop() to avoid a stretched image
                    .into(photoSelected);


            saySomething.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            saySomething.clearFocus();
            saySomething.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        emoticonsPhoto.setVisibility(View.GONE);
                        layoutVideoPhot.setVisibility(View.VISIBLE);
                        firstClickSmile = true;


                    }

                }
            });


            saySomething.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saySomething.setFocusable(true);
                    emoticonsPhoto.setVisibility(View.GONE);
                    layoutVideoPhot.setVisibility(View.VISIBLE);
                    firstClickSmile = true;

                }
            });


smajlic.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if (firstClickSmile) {
            firstClickSmile = false;
            secondClickSmile = true;

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            emoticonsPhoto.startAnimation(slideUpAnimation);
            emoticonsPhoto.setVisibility(View.VISIBLE);
            layoutVideoPhot.setVisibility(View.GONE);

        } else if (secondClickSmile) {
            firstClickSmile = true;
            secondClickSmile = false;
layoutVideoPhot.setVisibility(View.VISIBLE);
            saySomething.clearFocus();
          /*  InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);*/
            emoticonsPhoto.setVisibility(View.GONE);


        }
    }
});

            setEmojiconFragment(false);

        } else if (!MainPage.photoSelected) {
            photoSelected.setVisibility(View.GONE);

            slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.anmation_drom_down_to_top);

            pDialog = new ProgressDialog(this,R.style.AppTheme_Dark_Dialog);
            pDialog.setMessage("Buffering");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();


            try {
                pDialog.dismiss();

                saySomething.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                saySomething.clearFocus();
                saySomething.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            emoticonsPhoto.setVisibility(View.GONE);
                            videoSelected.setVisibility(View.VISIBLE);
                            firstClickSmile = true;


                        }

                    }
                });


                saySomething.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saySomething.setFocusable(true);
                        emoticonsPhoto.setVisibility(View.GONE);
                        videoSelected.setVisibility(View.VISIBLE);
                        firstClickSmile = true;

                    }
                });


                smajlic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (firstClickSmile) {
                            firstClickSmile = false;
                            secondClickSmile = true;

                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                            emoticonsPhoto.startAnimation(slideUpAnimation);
                            emoticonsPhoto.setVisibility(View.VISIBLE);
                            videoSelected.setVisibility(View.GONE);

                        } else if (secondClickSmile) {
                            firstClickSmile = true;
                            secondClickSmile = false;
                            videoSelected.setVisibility(View.VISIBLE);
                            saySomething.clearFocus();
          /*  InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);*/
                            emoticonsPhoto.setVisibility(View.GONE);


                        }
                    }
                });

                setEmojiconFragment(false);
                videoSelected.setVisibility(View.VISIBLE);
                Uri video = myIntent.getData(); //Uri.parse(myIntent.getStringExtra("video-uri_selected"));

                videoSelected.setUp(getPath(AddPhotoOrVideo.this, video), JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "");
                videoSelected.requestFocus();

                InputStream fileInputStream = this.getContentResolver().openInputStream(video);
                float size = fileInputStream.available();

                float fileSizeInKB = size / 1024;
                float fileSizeInMB = fileSizeInKB / 1024;

                NumberFormat formatter = NumberFormat.getNumberInstance();
                formatter.setMinimumFractionDigits(2);
                formatter.setMaximumFractionDigits(2);
                videoSize = formatter.format(fileSizeInMB);
                Log.i("videoSize", videoSize);

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            videoSelected.requestFocus();

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_post_text, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.save_edit_text) {
            if (MainPage.photoSelected) {
                try {
                    Uri imageUri = myIntent.getData();
                    File imagePath = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        imagePath = new File(getRealPathFromURI(imageUri));
                    }
                    Log.i("imagePath", imagePath.getPath());

                    final Bitmap imageCompressBitmap = new Compressor(this)
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(75)
                            .compressToBitmap(imagePath);

                    final String aboutPhotoText = saySomething.getText().toString().trim();
                    final String username = myIntent.getStringExtra("username");
                    final String profileImage = myIntent.getStringExtra("profileImage");
                    final String country = myIntent.getStringExtra("country");
                    final String clubLogo = myIntent.getStringExtra("clubheader");

                    Log.i("name", username);

                    photoPost = mFilePath.child("Post_Photo").child(imageUri.getLastPathSegment());
                    postingDialog.setMessage("Posting");
                    postingDialog.show();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imageCompressBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] data = baos.toByteArray();

                    UploadTask uploadTask = photoPost.putBytes(data);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUri = taskSnapshot.getDownloadUrl();

                            Map<String,Object> imagePost = new HashMap<>();

                            imagePost.put("username", username);
                            imagePost.put("profileImage", profileImage);
                            imagePost.put("photoPost", downloadUri.toString());
                            imagePost.put("uid", mAuth.getCurrentUser().getUid());
                            imagePost.put("country", country);
                            imagePost.put("clubLogo", clubLogo);
                            imagePost.put("time", FieldValue.serverTimestamp());
                            imagePost.put("favoritePostClub", MainPage.myClubName);
                            imagePost.put("descForPhoto", aboutPhotoText);
                            postingCollection.collection("Posting").add(imagePost).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    String key = documentReference.getId();
                                    Map<String,Object> keyUpdate = new HashMap<>();
                                    keyUpdate.put("key", key);
                                    postingCollection.collection("Posting").document(key).update(keyUpdate);

                                    Toast.makeText(AddPhotoOrVideo.this,
                                            "Event document has been added",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                            postingDialog.dismiss();
                            Intent goToMain = new Intent(AddPhotoOrVideo.this, MainPage.class);
                            startActivity(goToMain);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("errorPosting", e.getMessage());
                            postingDialog.dismiss();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (!MainPage.photoSelected) {
                final Uri videoUri = Uri.parse(myIntent.getStringExtra("video-uri_selected"));
                final String aboutVideoText = saySomething.getText().toString().trim();
                final String username = myIntent.getStringExtra("username");
                final String profileImage = myIntent.getStringExtra("profileImage");
                final String country = myIntent.getStringExtra("country");
                final String clubLogo = myIntent.getStringExtra("clubheader");
                postVideo = mFilePath.child("Post_Video").child(videoUri.getLastPathSegment());


                final float videoLenght = Float.parseFloat(videoSize);
                if (videoLenght > 10) {

                    Snackbar.make(layout, "Your video is bigger than 10 mb", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                            .show();
                } else {

                    postingDialog.setMessage("Posting");
                    postingDialog.show();




                    postVideo.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUri = taskSnapshot.getDownloadUrl();

                            Map<String,Object> videoPost = new HashMap<>();

                            videoPost.put("descVideo", aboutVideoText);
                            videoPost.put("username", username);
                            videoPost.put("profileImage", profileImage);
                            videoPost.put("videoPost", downloadUri.toString());
                            videoPost.put("uid", mAuth.getCurrentUser().getUid());
                            videoPost.put("country", country);
                            videoPost.put("time", FieldValue.serverTimestamp());
                            videoPost.put("clubLogo", clubLogo);
                            videoPost.put("favoritePostClub", MainPage.myClubName);

                            postingCollection.collection("Posting").add(videoPost).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    String key = documentReference.getId();
                                    Map<String,Object> keyUpdate = new HashMap<>();
                                    keyUpdate.put("key", key);
                                    postingCollection.collection("Posting").document(key).update(keyUpdate);

                                    Toast.makeText(AddPhotoOrVideo.this,
                                            "Event document has been added",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                            postingDialog.dismiss();
                            Intent goToMain = new Intent(AddPhotoOrVideo.this, MainPage.class);
                            startActivity(goToMain);
                            finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("errorPosting", e.getLocalizedMessage());
                            postingDialog.dismiss();
                        }
                    });
                }
            }


        }



        return super.onOptionsItemSelected(item);


    }



    @Override
    public void onBackPressed() {
        if (cn.jzvd.JZVideoPlayerStandard.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cn.jzvd.JZVideoPlayerStandard.releaseAllVideos();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String getRealPathFromURI(Uri contentURI) {
        String wholeID = DocumentsContract.getDocumentId(contentURI);

// Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

// where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

        String filePath = "";

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        cursor.close();
        return filePath;
    }


    private void setEmojiconFragment(boolean useSystemDefault){

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojiconsPhotoVideo, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        if (DEBUG)
            Log.d(TAG + " File -",
                    "Authority: " + uri.getAuthority() +
                            ", Fragment: " + uri.getFragment() +
                            ", Port: " + uri.getPort() +
                            ", Query: " + uri.getQuery() +
                            ", Scheme: " + uri.getScheme() +
                            ", Host: " + uri.getHost() +
                            ", Segments: " + uri.getPathSegments().toString()
            );

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
         if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }

            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(saySomething,emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
EmojiconsFragment.backspace(saySomething);
    }
}