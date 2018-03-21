package com.malikbisic.sportapp.activity.firebase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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

public class OnlyPostActivity extends AppCompatActivity implements TextWatcher, EmojiconsFragment.OnEmojiconBackspaceClickedListener, EmojiconGridFragment.OnEmojiconClickedListener {
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
        postWithBackgroundLayout = (RelativeLayout)findViewById(R.id.edittextlayoutonlypostWithBackground);
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
        }else if (withoutBackground != null){
            layoutForPost.setVisibility(View.VISIBLE);
            postWithBackgroundLayout.setVisibility(View.GONE);
        }

        if (postWithBackgroundLayout.getVisibility() == View.VISIBLE){
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


        if (postWithBackgroundLayout.getVisibility() == View.VISIBLE){

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
                        textMap.put("idResource",imageBacgkround);
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

        if (postWithBackgroundLayout.getVisibility() == View.VISIBLE){
            EmojiconsFragment.input(editTextWithBackground, emojicon);
        }
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(onlyposteditText);
        if (postWithBackgroundLayout.getVisibility() == View.VISIBLE){
            EmojiconsFragment.backspace(editTextWithBackground);
        }
    }

    private void setEmojiconFragment(boolean useSystemDefault) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojiconsOnlyPost, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }
}
