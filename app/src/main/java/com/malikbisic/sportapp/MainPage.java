package com.malikbisic.sportapp;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.data;
import static android.R.attr.mode;
import static android.R.attr.theme;
import static android.R.attr.track;
import static android.R.attr.voiceIcon;


public class MainPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, TextWatcher {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    static String uid;
    private TextView username;
    private TextView email;
    private ImageView profile;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference, postingDatabase;
    private BitmapDrawable obwer;
    private LinearLayout layout;
    private ImageView userProfileImage;
    private TextView usernameuser;
    private ImageView galleryIcon;
    private TextView galleryText;
    private ImageView videoIcon;
    private TextView videoText;
    private ImageView audioIcon;
    private TextView audioText;
    private EditText postText;
    MenuItem myMenu;
    private ProgressDialog postingDialog;
    private RecyclerView wallList;
    static boolean photoSelected;
    static String usernameInfo;
    static String profielImage;
    String neznijavise;

    private boolean pauseAudioPressed = false;
    private boolean audiofinished = false;
    private int pausePosition;

    private static final int PHOTO_OPEN = 1;
    private static final int VIDEO_OPEN = 2;
    boolean pause_state;
    boolean play_state;
    boolean stop_state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_main_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        postingDatabase = FirebaseDatabase.getInstance().getReference().child("Posting");
        userProfileImage = (ImageView) findViewById(R.id.userProfilImage);
        usernameuser = (TextView) findViewById(R.id.user_username);
        galleryIcon = (ImageView) findViewById(R.id.gallery_icon_content_main);
        galleryText = (TextView) findViewById(R.id.galleryText);
        videoIcon = (ImageView) findViewById(R.id.vide_icon_content_main);
        videoText = (TextView) findViewById(R.id.videoText);
        audioIcon = (ImageView) findViewById(R.id.talk_icon_content_main);
        audioText = (TextView) findViewById(R.id.audioText);
        postText = (EditText) findViewById(R.id.postOnlyText);
        postingDialog = new ProgressDialog(this);
        wallList = (RecyclerView) findViewById(R.id.wall_rec_view);
        wallList.setHasFixedSize(false);
        wallList.setLayoutManager(new LinearLayoutManager(this));

        postText.addTextChangedListener(this);
        postText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if (i == KeyEvent.KEYCODE_DEL) {
                }

                return false;
            }
        });


        galleryIcon.setOnClickListener(this);
        galleryText.setOnClickListener(this);
        videoIcon.setOnClickListener(this);
        videoText.setOnClickListener(this);
        audioIcon.setOnClickListener(this);
        audioText.setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    uid = user.getUid();

                    Log.i("proba", uid);

                    mReference = mDatabase.getReference().child("Users").child(uid);
                    mReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            if (dataSnapshot.exists()) {
                                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();


                                profielImage = String.valueOf(value.get("profileImage"));
                                Picasso.with(getApplicationContext())
                                        .load(profielImage)
                                        .into(profile);
                                username.setText(String.valueOf(value.get("username")));
                                usernameuser.setText(String.valueOf(value.get("username")));
                                usernameInfo = usernameuser.getText().toString().trim();

                                Picasso.with(getApplicationContext())
                                        .load(profielImage)
                                        .into(userProfileImage);

                                String country = String.valueOf(value.get("country"));
                                Log.i("country", country);

                                FirebaseUser user = mAuth.getCurrentUser();

                                email.setText(user.getEmail());

                                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Flags");
                                query.whereEqualTo("country", country);
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
                                                            layout.setBackground(obwer);
                                                        } else {
                                                            Log.d("test", "There was a problem downloading the data.");
                                                        }
                                                    }
                                                });
                                            }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }

            }
        };


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        profile = (ImageView) header.findViewById(R.id.nav_header_profil_image);
        username = (TextView) header.findViewById(R.id.header_username);
        email = (TextView) header.findViewById(R.id.header_email);
        layout = (LinearLayout) header.findViewById(R.id.nav_header_main_background);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == PHOTO_OPEN && resultCode == RESULT_OK) {

                Uri imageUri = data.getData();
                Intent goToAddPhotoOrVideo = new Intent(MainPage.this, AddPhotoOrVideo.class);
                goToAddPhotoOrVideo.putExtra("image-uri_selected", imageUri.toString());
                startActivity(goToAddPhotoOrVideo);
                Log.i("uri photo", String.valueOf(imageUri));

            } else if (requestCode == VIDEO_OPEN && resultCode == RESULT_OK) {
                Uri videoUri = data.getData();
                Intent goToAddPhotoOrVideo = new Intent(MainPage.this, AddPhotoOrVideo.class);
                goToAddPhotoOrVideo.putExtra("video-uri_selected", videoUri.toString());
                Log.i("uri video", String.valueOf(videoUri));
                startActivity(goToAddPhotoOrVideo);


            } else {

                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        myMenu = menu.findItem(R.id.postText);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.postText) {
            String textPost = postText.getText().toString().trim();

            postingDialog.setMessage("Posting...");
            postingDialog.show();
            DatabaseReference newPost = postingDatabase.push();
            newPost.child("desc").setValue(textPost);
            newPost.child("username").setValue(MainPage.usernameInfo);
            newPost.child("profileImage").setValue(MainPage.profielImage);
            postingDialog.dismiss();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
            ProfileFragment profileFragment = new ProfileFragment();

            FragmentTransaction manager = getSupportFragmentManager().beginTransaction();

            manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                    R.anim.push_left_out, R.anim.push_left_out).replace(R.id.mainpage_fragment, profileFragment, profileFragment.getTag()).addToBackStack(null).commit();
        } else if (id == R.id.nav_message) {

        } else if (id == R.id.nav_notifications) {

        } else if (id == R.id.premium_account) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent goToLogin = new Intent(MainPage.this, LoginActivity.class);
            goToLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(goToLogin);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);


        FirebaseRecyclerAdapter<Post, PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                Post.class,
                R.layout.wall_row,
                PostViewHolder.class,
                postingDatabase
        ) {
            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, final Post model, int position) {
                viewHolder.setDesc(model.getDesc());
                viewHolder.setProfileImage(getApplicationContext(), model.getProfileImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setPhotoPost(getApplicationContext(), model.getPhotoPost());
                viewHolder.setVideoPost(getApplicationContext(), model.getVideoPost());
                viewHolder.setAudioFile(getApplicationContext(), model.getAudioFile());

                viewHolder.seekBar.setEnabled(true);
                viewHolder.play_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        viewHolder.mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                        try {
                            viewHolder.mPlayer.prepareAsync();
                            viewHolder.mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    viewHolder.mPlayer.start();

                                    viewHolder.seekBar.setMax(viewHolder.mPlayer.getDuration());

                                    new Timer().scheduleAtFixedRate(new TimerTask() {
                                        @Override
                                        public void run() {
                                            viewHolder.seekBar.setProgress(viewHolder.mPlayer.getCurrentPosition());
                                            viewHolder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                @Override
                                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                    if (fromUser) {
                                                        viewHolder.mPlayer.seekTo(progress);
                                                    }
                                                }

                                                @Override
                                                public void onStartTrackingTouch(SeekBar seekBar) {

                                                }

                                                @Override
                                                public void onStopTrackingTouch(SeekBar seekBar) {

                                                }
                                            });
                                        }
                                    }, 0, 100);

                                }
                            });


                            viewHolder.mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    Log.i("finished", "yes");

                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        play_state = true;
                        if (!pause_state) {
                            viewHolder.mPlayer.start();
                            pause_state = false;
                        }

                    }


                });


                viewHolder.pause_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pause_state = true;
                        play_state = false;
                        if (viewHolder.mPlayer.isPlaying() && pause_state)
                            viewHolder.mPlayer.pause();
                        pause_state = false;

                    }
                });

                viewHolder.stop_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pause_state = false;
                        play_state = false;
                        stop_state = true;
                        if (viewHolder.mPlayer != null && stop_state) {
                            viewHolder.mPlayer.stop();
                            viewHolder.seekBar.setMax(0);


                        }
                    }
                });





            }
        };
        wallList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class PostViewHolder extends RecyclerView.ViewHolder {
        View mView;
        Button play_button;
        MediaPlayer mPlayer;
        VideoView videoView;
        ImageView post_photo;
        MediaController mediaController;
        RelativeLayout audioLayout;
        ProgressDialog progressDialog;
        Button pause_button;
        SeekBar seekBar;
        Button stop_button;


        public PostViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            play_button = (Button) mView.findViewById(R.id.play_button);
            pause_button = (Button) mView.findViewById(R.id.pause_button);
            stop_button = (Button) mView.findViewById(R.id.stop_button);

            mediaController = new MediaController(mView.getContext());

            videoView = (VideoView) mView.findViewById(R.id.posted_video);
            post_photo = (ImageView) mView.findViewById(R.id.posted_image);
            audioLayout = (RelativeLayout) mView.findViewById(R.id.layout_for_audio_player);
            mPlayer = new MediaPlayer();
            progressDialog = new ProgressDialog(mView.getContext());
            seekBar = (SeekBar) mView.findViewById(R.id.audio_seek_bar);


        }

        public void setDesc(String desc) {

            TextView post_desc = (TextView) mView.findViewById(R.id.user_text_wall);
            post_desc.setText(desc);
        }

        public void setUsername(String username) {
            TextView post_username = (TextView) mView.findViewById(R.id.username_wall);
            post_username.setText(username);


        }

        public void setProfileImage(Context ctx, String profileImage) {
            ImageView post_profile_image = (ImageView) mView.findViewById(R.id.profile_image_wall);
            Picasso.with(ctx).load(profileImage).into(post_profile_image);

        }


        public void setPhotoPost(Context ctx, String photoPost) {

            if (photoPost != null) {
                post_photo.setVisibility(View.VISIBLE);
                Picasso.with(ctx).load(photoPost).into(post_photo);
            } else {

                post_photo.setVisibility(View.GONE);
            }

        }

        public void setVideoPost(Context ctx, String videoPost) {


            if (videoPost != null) {

                try {
                    videoView.setMediaController(mediaController);
                    videoView.setVisibility(View.VISIBLE);
                    videoView.setVideoURI(Uri.parse(videoPost));
                    videoView.requestFocus();
                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            videoView.start();
                        }
                    });
                } catch (Exception e) {
                    e.getMessage();
                }

            } else {
                videoView.setVisibility(View.GONE);
            }
        }


        public void setAudioFile(Context context, String audioFile) {

            if (audioFile != null) {
                // mPlayer.reset();
                audioLayout.setVisibility(View.VISIBLE);
                try {

                    mPlayer.setDataSource(context, Uri.parse(audioFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                audioLayout.setVisibility(View.GONE);
            }

        }


    }


    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.gallery_icon_content_main || view.getId() == R.id.galleryText) {
            Intent openGallery = new Intent(Intent.ACTION_GET_CONTENT);
            photoSelected = true;
            openGallery.setType("image/*");
            startActivityForResult(openGallery, PHOTO_OPEN);
        } else if (view.getId() == R.id.vide_icon_content_main || view.getId() == R.id.videoText) {
            Intent intent = new Intent();
            photoSelected = false;
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), VIDEO_OPEN);

        } else if (view.getId() == R.id.talk_icon_content_main || view.getId() == R.id.audioText) {

            Intent intent = new Intent(MainPage.this, RecordAudio.class);
            startActivity(intent);
        }

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


        if (!postText.getText().toString().trim().isEmpty() && postText.getText().toString().trim().length() >= 3) {
            myMenu.setEnabled(true);
        } else if (postText.getText().toString().trim().length() < 3) {
            myMenu.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


}
