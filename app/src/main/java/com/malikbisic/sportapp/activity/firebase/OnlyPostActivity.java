package com.malikbisic.sportapp.activity.firebase;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.malikbisic.sportapp.R;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class OnlyPostActivity extends AppCompatActivity implements TextWatcher, EmojiconsFragment.OnEmojiconBackspaceClickedListener, EmojiconGridFragment.OnEmojiconClickedListener, View.OnClickListener {
    Toolbar onlyposttoolbar;
    EmojiconEditText onlyposteditText;
    ImageButton smajliBtn;
    CircleImageView profileImageOnlyPost;
    TextView usernameOnlyPost;
    ImageView send;
    FirebaseAuth mauth;
    FirebaseFirestore postingDatabase;
    Intent getIntent;
    String username;
    String profileImage;
    AlertDialog postingDialog;
    FrameLayout emoticonsOnlyPost;
    boolean firstClickSmile = true;
    boolean secondClickSmile = false;
    Animation slideUpAnimation;
    CircleImageView backgroundsClick;
    int imageBacgkround;
    RelativeLayout layoutForPost;
    String withoutBackground;
    RelativeLayout postWithBackgroundLayout;
    EmojiconEditText editTextWithBackground;

    private static final int PHOTO_OPEN = 1;
    private static final int PHOTO_OPEN_ON_OLDER_PHONES = 3;
    private static final int VIDEO_OPEN = 2;
    public static int CAMERA_REQUEST = 32;

    boolean photoSelected;

    ImageButton galleryIcon;
    ImageButton videoIcon;
    ImageButton audioIcon;
    ImageButton captureImage;
    Uri  imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_only_post);
        mauth = FirebaseAuth.getInstance();
        onlyposttoolbar = (Toolbar) findViewById(R.id.onlyposttoolbar);
        setSupportActionBar(onlyposttoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        onlyposteditText = (EmojiconEditText) findViewById(R.id.onlypostedittext);
        postingDatabase = FirebaseFirestore.getInstance();
        profileImageOnlyPost = (CircleImageView) findViewById(R.id.pofileImageOnlyPost);
        smajliBtn = (ImageButton) findViewById(R.id.smileImage);
        layoutForPost = (RelativeLayout) findViewById(R.id.edittextlayoutonlypost);
        usernameOnlyPost = (TextView) findViewById(R.id.usernameOnlyPost);
        postWithBackgroundLayout = (RelativeLayout) findViewById(R.id.edittextlayoutonlypostWithBackground);
        galleryIcon = (ImageButton) findViewById(R.id.plus_btn);
        videoIcon = (ImageButton) findViewById(R.id.video_btn);
        audioIcon = (ImageButton) findViewById(R.id.audiobtn);
        captureImage = (ImageButton) findViewById(R.id.take_photo_btn);

        postingDialog = new SpotsDialog(OnlyPostActivity.this, "Posting...", R.style.StyleLogin);
        getIntent = getIntent();
        editTextWithBackground = (EmojiconEditText) findViewById(R.id.onlypostedittextWithBackground);

        Picasso.with(this).load(MainPage.profielImage).into(profileImageOnlyPost);
        usernameOnlyPost.setText(MainPage.usernameInfo);
        backgroundsClick = (CircleImageView) findViewById(R.id.choosebackground);
        send = (ImageView) findViewById(R.id.onlypostsend);
        emoticonsOnlyPost = (FrameLayout) findViewById(R.id.emojiconsOnlyPost);
        onlyposteditText.addTextChangedListener(this);
        withoutBackground = getIntent.getStringExtra("defaultBackground");
        send.setRotation(300);
        if (getIntent().getIntExtra("imageRes", 0) != 0) {
            withoutBackground = null;
            layoutForPost.setVisibility(View.GONE);
            postWithBackgroundLayout.setVisibility(View.VISIBLE);
            imageBacgkround = getIntent.getIntExtra("imageRes", 0);
            postWithBackgroundLayout.setBackgroundResource(imageBacgkround);
        } else if (withoutBackground != null) {
            layoutForPost.setVisibility(View.VISIBLE);
            postWithBackgroundLayout.setVisibility(View.GONE);
        }

        if (postWithBackgroundLayout.getVisibility() == View.VISIBLE) {
            editTextWithBackground.addTextChangedListener(this);
            editTextWithBackground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editTextWithBackground.setFocusable(true);
                    emoticonsOnlyPost.setVisibility(View.GONE);

                    firstClickSmile = true;

                }
            });

            setEmojiconFragment(false);


            editTextWithBackground.clearFocus();
            editTextWithBackground.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        emoticonsOnlyPost.setVisibility(View.GONE);
                        editTextWithBackground.setHint("");
                        firstClickSmile = true;


                    }

                }
            });

            smajliBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (firstClickSmile) {
                        firstClickSmile = false;
                        secondClickSmile = true;

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                        emoticonsOnlyPost.startAnimation(slideUpAnimation);
                        emoticonsOnlyPost.setVisibility(View.VISIBLE);


                    } else if (secondClickSmile) {
                        firstClickSmile = true;
                        secondClickSmile = false;

                        editTextWithBackground.clearFocus();
          /*  InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);*/
                        emoticonsOnlyPost.setVisibility(View.GONE);


                    }
                }
            });
        }


        slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.anmation_drom_down_to_top);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(OnlyPostActivity.this, "Can't send empty post", Toast.LENGTH_SHORT).show();
            }
        });

        onlyposteditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onlyposteditText.setFocusable(true);
                emoticonsOnlyPost.setVisibility(View.GONE);

                firstClickSmile = true;

            }
        });

        setEmojiconFragment(false);


        onlyposteditText.clearFocus();
        onlyposteditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    emoticonsOnlyPost.setVisibility(View.GONE);

                    firstClickSmile = true;


                }

            }
        });
        backgroundsClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OnlyPostActivity.this, BackgroundPostActivity.class);
                startActivity(intent);
            }
        });

        smajliBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firstClickSmile) {
                    firstClickSmile = false;
                    secondClickSmile = true;

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    emoticonsOnlyPost.startAnimation(slideUpAnimation);
                    emoticonsOnlyPost.setVisibility(View.VISIBLE);


                } else if (secondClickSmile) {
                    firstClickSmile = true;
                    secondClickSmile = false;

                    onlyposteditText.clearFocus();
          /*  InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);*/
                    emoticonsOnlyPost.setVisibility(View.GONE);


                }
            }
        });

        videoIcon.setOnClickListener(this);
        captureImage.setOnClickListener(this);
        audioIcon.setOnClickListener(this);


        galleryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageIntent = new Intent(OnlyPostActivity.this, PhotoPostSelectActivity.class);
                imageIntent.putExtra("username", MainPage.usernameInfo);
                imageIntent.putExtra("profileImage", MainPage.profielImage);
                imageIntent.putExtra("country", MainPage.country);
                imageIntent.putExtra("clubheader", MainPage.clubHeaderString);
                startActivity(imageIntent);
//        if (Build.VERSION.SDK_INT < 19) {
//
//
////            Intent openGallery = new Intent(Intent.ACTION_GET_CONTENT);
////            photoSelected = true;
////            openGallery.setType("image/*");
////            startActivityForResult(openGallery, PHOTO_OPEN_ON_OLDER_PHONES);
//        } else {
//            photoSelected = true;
//            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.setType("image/*");
//            startActivityForResult(intent, PHOTO_OPEN);
//
//        }
            }
        });

    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!onlyposteditText.getText().toString().trim().isEmpty() && onlyposteditText.getText().toString().trim().length() >= 1) {
            send.setRotation(0);
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String textPost = onlyposteditText.getText().toString().trim();
                    final String country = MainPage.country;
                    final String clubLogo = MainPage.clubHeaderString;
                    postingDialog.show();
                    Map<String, Object> textMap = new HashMap<>();
                    textMap.put("desc", textPost);
                    textMap.put("username", MainPage.usernameInfo);
                    textMap.put("profileImage", MainPage.profielImage);
                    textMap.put("uid", mauth.getCurrentUser().getUid());
                    textMap.put("country", country);
                    textMap.put("time", FieldValue.serverTimestamp());
                    textMap.put("clubLogo", clubLogo);
                    textMap.put("favoritePostClub", MainPage.myClubName);
                    postingDatabase.collection("Posting").add(textMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                String key = task.getResult().getId();
                                Map<String, Object> keyUpdate = new HashMap<>();
                                keyUpdate.put("key", key);
                                postingDatabase.collection("Posting").document(key).update(keyUpdate);
                                postingDialog.dismiss();
                                onlyposteditText.setText("");
                            }
                        }
                    });
                    Intent intent = new Intent(OnlyPostActivity.this, MainPage.class);
                    startActivity(intent);
                }


            });


        } else if (onlyposteditText.getText().toString().trim().length() < 1) {
            send.setRotation(300);
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(OnlyPostActivity.this, "Can't send empty post", Toast.LENGTH_SHORT).show();
                }
            });

        }


        if (postWithBackgroundLayout.getVisibility() == View.VISIBLE) {

            if (!editTextWithBackground.getText().toString().trim().isEmpty() && editTextWithBackground.getText().toString().trim().length() >= 1) {
                send.setRotation(0);
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String textPost = editTextWithBackground.getText().toString().trim();
                        final String country = MainPage.country;
                        final String clubLogo = MainPage.clubHeaderString;
                        postingDialog.show();
                        Map<String, Object> textMap = new HashMap<>();
                        textMap.put("descWithBackground", textPost);
                        textMap.put("username", MainPage.usernameInfo);
                        textMap.put("profileImage", MainPage.profielImage);
                        textMap.put("uid", mauth.getCurrentUser().getUid());
                        textMap.put("country", country);
                        textMap.put("time", FieldValue.serverTimestamp());
                        textMap.put("clubLogo", clubLogo);
                        textMap.put("favoritePostClub", MainPage.myClubName);
                        textMap.put("idResource", imageBacgkround);
                        postingDatabase.collection("Posting").add(textMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    String key = task.getResult().getId();
                                    Map<String, Object> keyUpdate = new HashMap<>();
                                    keyUpdate.put("key", key);
                                    postingDatabase.collection("Posting").document(key).update(keyUpdate);
                                    postingDialog.dismiss();
                                    editTextWithBackground.setText("");
                                }
                            }
                        });
                        Intent intent = new Intent(OnlyPostActivity.this, MainPage.class);
                        startActivity(intent);
                    }


                });


            } else if (editTextWithBackground.getText().toString().trim().length() < 1) {
                send.setRotation(300);
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(OnlyPostActivity.this, "Can't send empty post", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(onlyposteditText, emojicon);

        if (postWithBackgroundLayout.getVisibility() == View.VISIBLE) {
            EmojiconsFragment.input(editTextWithBackground, emojicon);
        }
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(onlyposteditText);
        if (postWithBackgroundLayout.getVisibility() == View.VISIBLE) {
            EmojiconsFragment.backspace(editTextWithBackground);
        }
    }

    private void setEmojiconFragment(boolean useSystemDefault) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojiconsOnlyPost, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == PHOTO_OPEN || requestCode == PHOTO_OPEN_ON_OLDER_PHONES && resultCode == RESULT_OK) {

                Uri imageUri = data.getData();
                Intent goToAddPhotoOrVideo = new Intent(OnlyPostActivity.this, AddPhotoOrVideo.class);
                goToAddPhotoOrVideo.setData(imageUri);
                goToAddPhotoOrVideo.putExtra("username", MainPage.usernameInfo);
                goToAddPhotoOrVideo.putExtra("profileImage", MainPage.profielImage);
                goToAddPhotoOrVideo.putExtra("country", MainPage.country);
                goToAddPhotoOrVideo.putExtra("clubheader", MainPage.clubHeaderString);
                startActivity(goToAddPhotoOrVideo);
                Log.i("uri photo", String.valueOf(imageUri));

            } else if (requestCode == VIDEO_OPEN && resultCode == RESULT_OK) {
                Uri videoUri = data.getData();
                Log.i("vidoeURL", videoUri.toString());
                if (videoUri.toString().contains("com.google.android.apps.docs.storage")) {

                    Toast.makeText(OnlyPostActivity.this, "Can't open video from google drive", Toast.LENGTH_LONG).show();
                } else {
                    Intent goToAddPhotoOrVideo = new Intent(OnlyPostActivity.this, AddPhotoOrVideo.class);
                    goToAddPhotoOrVideo.setData(videoUri);
                    goToAddPhotoOrVideo.putExtra("video-uri_selected", videoUri.toString());
                    goToAddPhotoOrVideo.putExtra("username", MainPage.usernameInfo);
                    goToAddPhotoOrVideo.putExtra("profileImage", MainPage.profielImage);
                    goToAddPhotoOrVideo.putExtra("country", MainPage.country);
                    goToAddPhotoOrVideo.putExtra("clubheader", MainPage.clubHeaderString);
                    Log.i("uri video", String.valueOf(videoUri));
                    startActivity(goToAddPhotoOrVideo);
                }


            } else if (requestCode == CAMERA_REQUEST) {
                Bitmap image = null;
                String imageData = null;
                if (resultCode == RESULT_OK) {
                    try {
                        image = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), imageUri);

                        imageData = getRealPathFromURI(imageUri);


                        Intent openCameraSend = new Intent(OnlyPostActivity.this, CaptureImageSendChatActivity.class);
                        openCameraSend.setData(imageUri);
                        openCameraSend.putExtra("imagedata", image);
                        openCameraSend.putExtra("userIDFromMainPage", mauth.getCurrentUser().getUid());
                        openCameraSend.putExtra("fromMainPage", MainPage.fromMainPage);
                        openCameraSend.putExtra("username", MainPage.usernameInfo);
                        openCameraSend.putExtra("profileImage", MainPage.profielImage);
                        openCameraSend.putExtra("country", MainPage.country);
                        openCameraSend.putExtra("clubHeader", MainPage.clubHeaderString);
                        openCameraSend.putExtra("clubName", MainPage.myClubName);
                        openCameraSend.putExtra("postkey", MainPage.postKey);
                        startActivity(openCameraSend);
                    } catch (Exception e) {
                        Log.i("imageExcp", e.getLocalizedMessage());
                    }

                } else{
                    Toast.makeText(OnlyPostActivity.this, "Error", Toast.LENGTH_LONG).show();
                }
            } else {

                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {


        if (view.getId() == R.id.take_photo_btn) {

         ContentValues   values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, CAMERA_REQUEST);

        } else if (view.getId() == R.id.video_btn) {
            Intent intent = new Intent();
            photoSelected = false;
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), VIDEO_OPEN);

        } else if (view.getId() == R.id.audiobtn) {

            Intent intent = new Intent(OnlyPostActivity.this, RecordAudio.class);
            startActivity(intent);
        }

    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
