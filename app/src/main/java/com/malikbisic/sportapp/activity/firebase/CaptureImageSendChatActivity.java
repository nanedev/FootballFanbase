package com.malikbisic.sportapp.activity.firebase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.firebase.MultiSelectImageAdapter;
import com.malikbisic.sportapp.utils.ImageAlbumName;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class CaptureImageSendChatActivity extends AppCompatActivity implements TextWatcher,EmojiconsFragment.OnEmojiconBackspaceClickedListener,EmojiconGridFragment.OnEmojiconClickedListener {

    Toolbar mChatToolbar;

    private ImageView displayImage;
    FirebaseAuth mAuth;

    Intent myIntent;
    Uri image;
    String userID;
    String myUID;
    ProgressDialog dialog;
    String userIdFromMainPage;
    boolean mainpage;
    String username;
    String clubLogo;
    String country;
    String clubName;
    String key;
    String profileImage;
    Animation slideUpAnimation;
    EmojiconEditText saySomething;
    ImageView smajlic;
    FrameLayout emoticonsPhoto;
    boolean firstClickSmile = true;
    boolean secondClickSmile = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image_send_chat);

        mChatToolbar = (Toolbar) findViewById(R.id.chatImageCapture_toolbar);
        setSupportActionBar(mChatToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("Send image...");
        myIntent = getIntent();
        mAuth = FirebaseAuth.getInstance();
        image = myIntent.getData();
        userID = myIntent.getStringExtra("userID");
        myUID = mAuth.getCurrentUser().getUid();
        userIdFromMainPage = myIntent.getStringExtra("userIDFromMainPage");
        mainpage = myIntent.getBooleanExtra("fromMainPage", false);
        username = myIntent.getStringExtra("username");
        clubLogo = myIntent.getStringExtra("clubHeader");
        country = myIntent.getStringExtra("country");
        profileImage = myIntent.getStringExtra("profileImage");
        clubName = myIntent.getStringExtra("clubName");
        key = myIntent.getStringExtra("postkey");
        dialog = new ProgressDialog(this);
        saySomething = (EmojiconEditText) findViewById(R.id.tell_something_capture_image);
smajlic = (ImageView) findViewById(R.id.smileInCaptureImage) ;
emoticonsPhoto = (FrameLayout) findViewById(R.id.emojiconsImageCapture);
        displayImage = (ImageView) findViewById(R.id.imageCapture);
        displayImage.setImageURI(image);

        if (mainpage){



            slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.anmation_drom_down_to_top);

//            Picasso
//                    .with(AddPhotoOrVideo.this)
//                    .load(imageUri)
//                    .fit()
//                    .centerCrop()
//                    .
//                    // call .centerInside() or .centerCrop() to avoid a stretched image
//                    .into(photoSelected);



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


                    } else if (secondClickSmile) {
                        firstClickSmile = true;
                        secondClickSmile = false;
                        saySomething.clearFocus();
          /*  InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);*/
                        emoticonsPhoto.setVisibility(View.GONE);


                    }
                }
            });

            setEmojiconFragment(false);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_send, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent backToChat = new Intent(CaptureImageSendChatActivity.this, ChatMessageActivity.class);
                backToChat.putExtra("userId", userID);
                setResult(Activity.RESULT_OK, backToChat);
                finish();
                return true;
            case R.id.send_images:
                if (mainpage) {
                    sendImageFromMainPage();
                } else {
                    sendImage();
                }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void sendImageFromMainPage() {
        try {

            Uri imageUri = image;
            dialog.setMessage("Sending...");
            dialog.show();
            File imagePath = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                imagePath = new File(imageUri.getPath());
            }
            Log.i("imagePath", imagePath.getPath());

            final Bitmap imageCompressBitmap = new Compressor(CaptureImageSendChatActivity.this)
                    .setMaxWidth(640)
                    .setMaxHeight(480)
                    .setQuality(75)
                    .compressToBitmap(imagePath);


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageCompressBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] data = baos.toByteArray();
            StorageReference mFilePath = FirebaseStorage.getInstance().getReference();
            StorageReference photoPost = mFilePath.child("Post_Photo").child(imagePath.getName());
            UploadTask uploadTask = photoPost.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
String aboutPhotoText = saySomething.getText().toString();

                    Map<String, Object> postMap = new HashMap<>();
                    postMap.put("clubLogo", clubLogo);
                    postMap.put("country", country);
                    postMap.put("descForPhoto", "");
                    postMap.put("favoritePostClub", clubName);
                    postMap.put("photoPost", downloadUri.toString());
                    postMap.put("profileImage", profileImage);
                    postMap.put("time", FieldValue.serverTimestamp());
                    postMap.put("uid", myUID);
                    postMap.put("descForPhoto", aboutPhotoText);
                    postMap.put("username", username);
                    final FirebaseFirestore mRootRef = FirebaseFirestore.getInstance();
                    mRootRef.collection("Posting").add(postMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            String key = documentReference.getId();
                            Map<String, Object> keyUpdate = new HashMap<>();
                            keyUpdate.put("key", key);
                            mRootRef.collection("Posting").document(key).update(keyUpdate);

                            Toast.makeText(CaptureImageSendChatActivity.this,
                                    "Event document has been added",
                                    Toast.LENGTH_SHORT).show();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                    Intent backToMainPage = new Intent(CaptureImageSendChatActivity.this, MainPage.class);

                    startActivity(backToMainPage);
                    dialog.dismiss();
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("errorPosting", e.getMessage());

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendImage() {
        try {

            Uri imageUri = image;
            dialog.setMessage("Sending...");
            dialog.show();
            File imagePath = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                imagePath = new File(imageUri.getPath());
            }
            Log.i("imagePath", imagePath.getPath());

            final Bitmap imageCompressBitmap = new Compressor(CaptureImageSendChatActivity.this)
                    .setMaxWidth(640)
                    .setMaxHeight(480)
                    .setQuality(75)
                    .compressToBitmap(imagePath);


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageCompressBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] data = baos.toByteArray();
            StorageReference mFilePath = FirebaseStorage.getInstance().getReference();
            StorageReference photoPost = mFilePath.child("Chat_Image").child(imagePath.getName());
            UploadTask uploadTask = photoPost.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();


                    Map messageMap = new HashMap();
                    messageMap.put("message", downloadUri.toString());
                    messageMap.put("seen", false);
                    messageMap.put("type", "image");
                    messageMap.put("time", FieldValue.serverTimestamp());
                    messageMap.put("from", myUID);
                    final FirebaseFirestore mRootRef = FirebaseFirestore.getInstance();

                    mRootRef.collection("Messages").document(myUID).collection("chat-user").document(userID).collection("message").add(messageMap);
                    mRootRef.collection("Messages").document(userID).collection("chat-user").document(myUID).collection("message").add(messageMap);

                    final Map chatUser = new HashMap();
                    chatUser.put("to", userID);
                    final Map mychatUser = new HashMap();
                    chatUser.put("to", myUID);
                    mRootRef.collection("Messages").document(myUID).collection("chat-user").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists()){
                                mRootRef.collection("Messages").document(myUID).collection("chat-user").document(userID).update(chatUser);
                            }else {
                                mRootRef.collection("Messages").document(myUID).collection("chat-user").document(userID).set(chatUser);
                            }
                        }
                    });
                    mRootRef.collection("Messages").document(userID).collection("chat-user").document(myUID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists()){
                                mRootRef.collection("Messages").document(userID).collection("chat-user").document(myUID).update(mychatUser);
                            } else {
                                mRootRef.collection("Messages").document(userID).collection("chat-user").document(myUID).set(mychatUser);
                            }
                        }
                    });

                    mRootRef.collection("Messages").document(userID).collection("chat-user").document(myUID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.getResult().exists()){
                                boolean isInChat = task.getResult().getBoolean("isInChat");
                                if (!isInChat){
                                    Map<String, Object> notifMap = new HashMap<>();
                                    notifMap.put("action", "chat");
                                    notifMap.put("uid", myUID);
                                    notifMap.put("seen", false);
                                    notifMap.put("whatIS", "image");
                                    notifMap.put("timestamp", FieldValue.serverTimestamp());

                                    if (!userID.equals(mAuth.getCurrentUser().getUid())) {
                                        CollectionReference notifSet = FirebaseFirestore.getInstance().collection("NotificationChat").document(userID).collection("notif-id");
                                        notifSet.add(notifMap);
                                    }
                                }
                            }
                        }
                    });


                    Intent backToChat = new Intent(CaptureImageSendChatActivity.this, SendImageChatActivity.class);
                    backToChat.putExtra("userId", userID);
                    backToChat.putExtra("username", username);
                    setResult(Activity.RESULT_OK, backToChat);
                    dialog.dismiss();
                    finish();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("errorPosting", e.getMessage());

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setEmojiconFragment(boolean useSystemDefault) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojiconsImageCapture, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(saySomething, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(saySomething);
    }
}
