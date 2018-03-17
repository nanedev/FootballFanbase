package com.malikbisic.sportapp.activity.firebase;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.StopAppServices;

import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "LoginActivity";
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseFirestore mReferenceUsers;
    private EditText mEmailText;
    private EditText mPasswordText;
    private Button mLoginButton;
    private TextView mSignUpLink;
    private TextView emailError;
    private TextView passwordError;
    private TextView mForgotPassword;
    private ImageButton googleSignIn;
    private ImageButton facebookLogin;
    private String user_id;
    private String email;
    private String password;

    public static String gFirstName;
    public static String gLastName;
    public static String gUserId;
    public static String fbFirstName;
    public static String fbLastName;
    public static String fbUserId;
    public static String userIdLogin;
    public static boolean checkGoogleSignIn;
    public static boolean checkFacebookLogin;
    public static boolean checkLoginPressed;
    String getUserEmail;


    CallbackManager mCallbackManager;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent closeAPP = new Intent(this, StopAppServices.class);
        startService(closeAPP);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_login);
        AppEventsLogger.activateApp(getApplication());
        mEmailText = (EditText) findViewById(R.id.input_email_login);
        mPasswordText = (EditText) findViewById(R.id.input_password_login);
        mLoginButton = (Button) findViewById(R.id.btn_login);
        mSignUpLink = (TextView) findViewById(R.id.link_signup);
        googleSignIn = (ImageButton) findViewById(R.id.google_login);
        facebookLogin = (ImageButton) findViewById(R.id.facebbok_login);
        emailError = (TextView) findViewById(R.id.emailInfoErrorLogin);
        passwordError = (TextView) findViewById(R.id.passwordInfoErrorLogin);
        mForgotPassword = (TextView) findViewById(R.id.forgot_password);


        Intent intent = getIntent();
        getUserEmail = intent.getStringExtra("email");
        mEmailText.setText(getUserEmail, TextView.BufferType.EDITABLE);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mLoginButton.setOnClickListener(this);
        mSignUpLink.setOnClickListener(this);
        mForgotPassword.setOnClickListener(this);
        googleSignIn.setOnClickListener(this);

        mReferenceUsers = FirebaseFirestore.getInstance();
        mReferenceUsers.collection("Users");
        //mReferenceUsers.keepSynced(true);

        dialog = new SpotsDialog(LoginActivity.this, "Login...", R.style.StyleLogin);

        mEmailText.setFocusableInTouchMode(true);
        mPasswordText.setFocusableInTouchMode(true);
        mEmailText.clearFocus();
        mPasswordText.clearFocus();
        mAuth = FirebaseAuth.getInstance();

        //FACEBOOK SIGN IN

        facebookLogin.setOnClickListener(this);


        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.malikbisic.sportapp",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("patchE", e.getLocalizedMessage());

        } catch (NoSuchAlgorithmException e) {
            Log.e("algE", e.getLocalizedMessage());
        }


        //GOOGLE SIGN IN

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    autoLogin();
                    Log.i("user", user.getEmail());

                }
            }
        };
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this, "You got an error", Toast.LENGTH_LONG).show();
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            showProgressBar();
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount acct = result.getSignInAccount();

            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                ApiException e = new ApiException(result.getStatus());
                Log.e("googleLogin", e.getMessage());
                hideProgressBar();
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            fbFirstName = String.valueOf(task.getResult().getAdditionalUserInfo().getProfile().get("first_name"));
                            fbLastName = String.valueOf(task.getResult().getAdditionalUserInfo().getProfile().get("last_name"));
                            fbUserId = user.getUid();
                            checkUserExistsFacebook();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {

        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        if (user != null) {
                            gFirstName = acct.getGivenName();
                            gLastName = acct.getFamilyName();
                            gUserId = user.getUid();

                        }

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            checkGoogleSignIn = true;
                            checkFacebookLogin = false;
                            checkLoginPressed = false;
                            checkUserExists();



                        }

                    }
                });
    }


    private void checkLogin() {
        email = mEmailText.getText().toString().trim();
        password = mPasswordText.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null)
                            userIdLogin = user.getUid();


                        checkUserExists();

                    } else {

                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e.getMessage().equals(getString(R.string.error_email))) {
                        emailError.setText("Bad formatted email");
                        emailError.setVisibility(View.VISIBLE);
                    } else {
                        emailError.setText("");
                        emailError.setVisibility(View.INVISIBLE);
                    }

                    if (e.getMessage().equals(getString(R.string.error_password))) {
                        passwordError.setText("The password is invalid");
                        passwordError.setVisibility(View.VISIBLE);
                    } else {
                        passwordError.setText("");
                        passwordError.setVisibility(View.INVISIBLE);
                    }

                    if (e.getMessage().equals(getString(R.string.error_emailandpass))) {
                        Toast.makeText(LoginActivity.this, "User doesn't exists", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void checkUserExists() {
        showProgressBar();

        if (mAuth.getCurrentUser() != null) {

            user_id = mAuth.getCurrentUser().getUid();
            final DocumentReference usersRef = mReferenceUsers.collection("Users").document(user_id);
            usersRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {


                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user.isEmailVerified() && document.contains("username")) {
                                String current_userID = mAuth.getCurrentUser().getUid();
                                String device_id = FirebaseInstanceId.getInstance().getToken();

                                Map<String, Object> user2 = new HashMap<>();
                                user2.put("device_id", device_id);

                                mReferenceUsers.collection("Users").document(current_userID)
                                        .update(user2)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("error update", e.getLocalizedMessage());
                                    }
                                });
                                Intent setupIntent = new Intent(LoginActivity.this, MainPage.class);
                                startActivity(setupIntent);
                                hideProgressBar();
                                finish();
                            } else if (user.isEmailVerified() && !document.contains("username")) {
                                Intent intent = new Intent(LoginActivity.this, EnterUsernameForApp.class);
                                intent.putExtra("loginBtnPress", checkLoginPressed);
                                startActivity(intent);
                                hideProgressBar();


                            } else if (!user.isEmailVerified() || !document.contains("username")) {
                                hideProgressBar();
                                Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Intent goToSetUp = new Intent(LoginActivity.this, EnterUsernameForApp.class);
                            goToSetUp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(goToSetUp);


                        }
                    }
                }
            });

            //nesto


          /*  mReferenceUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(user_id)) {

                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user.isEmailVerified() && dataSnapshot.child(user_id).hasChild("username")) {
                            String current_userID = mAuth.getCurrentUser().getUid();
                            String device_id = FirebaseInstanceId.getInstance().getToken();

                            mReferenceUsers.child(current_userID).child("device_id").setValue(device_id);
                            Intent setupIntent = new Intent(LoginActivity.this, MainPage.class);
                            startActivity(setupIntent);
                            mDialog.dismiss();
                            finish();
                        } else if (user.isEmailVerified() && !dataSnapshot.child(user_id).hasChild("username")) {
                            Intent intent = new Intent(LoginActivity.this, EnterUsernameForApp.class);
                            startActivity(intent);
                            mDialog.dismiss();
                            finish();

                        } else if (!user.isEmailVerified()){
                            mDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Intent goToSetUp = new Intent(LoginActivity.this, EnterUsernameForApp.class);
                        goToSetUp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(goToSetUp);
                        finish();


                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            }); */
        }
    }

    public void checkUserExistsFacebook() {
        showProgressBar();
        if (mAuth.getCurrentUser() != null) {

            user_id = mAuth.getCurrentUser().getUid();
            final DocumentReference usersRef = mReferenceUsers.collection("Users").document(user_id);
            usersRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {


                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user.getProviders().get(0).equals("facebook.com")) {
                                if (document.contains("username")) {
                                    String current_userID = mAuth.getCurrentUser().getUid();
                                    String device_id = FirebaseInstanceId.getInstance().getToken();

                                    Map<String, Object> user2 = new HashMap<>();
                                    user2.put("device_id", device_id);

                                    mReferenceUsers.collection("Users").document(current_userID)
                                            .update(user2)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("error update", e.getLocalizedMessage());
                                        }
                                    });
                                    Intent setupIntent = new Intent(LoginActivity.this, MainPage.class);
                                    startActivity(setupIntent);
                                    hideProgressBar();
                                    finish();
                                } else if (!document.contains("username")) {
                                    Intent intent = new Intent(LoginActivity.this, EnterUsernameForApp.class);
                                    startActivity(intent);
                                    intent.putExtra("loginBtnPress", checkLoginPressed);
                                    intent.putExtra("googleLoginPress", checkGoogleSignIn);
                                    intent.putExtra("facebookLoginPress", checkFacebookLogin);
                                    hideProgressBar();
                                    finish();

                                }
                            }
                        } else {
                            Intent goToSetUp = new Intent(LoginActivity.this, EnterUsernameForApp.class);
                            goToSetUp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(goToSetUp);
                            finish();


                        }
                    }
                }
            });
        }
    }

    private void autoLogin() {

        if (mAuth.getCurrentUser() != null) {
            user_id = mAuth.getCurrentUser().getUid();
            DocumentReference usersRef = mReferenceUsers.collection("Users").document(user_id);
            usersRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();
                        if (snapshot.exists()) {
                            if (task.getResult().getString("username") != null) {
                                FirebaseUser user = mAuth.getCurrentUser();

                                if (user.isEmailVerified() && snapshot.contains("username")) {
                                    final String current_userID = mAuth.getCurrentUser().getUid();
                                    final String device_id = FirebaseInstanceId.getInstance().getToken();


                                    CountDownTimer timer = new CountDownTimer(3000, 1000) {
                                        @Override
                                        public void onTick(long l) {
                                            showProgressBar();
                                        }

                                        @Override
                                        public void onFinish() {
                                            Intent setupIntent = new Intent(LoginActivity.this, MainPage.class);
                                            startActivity(setupIntent);
                                           hideProgressBar();

                                            Map<String, Object> updateDevideId = new HashMap<>();
                                            updateDevideId.put("device_id", device_id);
                                            finish();
                                            mReferenceUsers.collection("Users").document(current_userID)
                                                    .update(updateDevideId)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("error update auto login", e.getLocalizedMessage());
                                                }
                                            });
                                        }
                                    }.start();

                                } else if (user.getProviders().get(0).equals("facebook.com") && snapshot.contains("username")) {
                                    final String current_userID = mAuth.getCurrentUser().getUid();
                                    final String device_id = FirebaseInstanceId.getInstance().getToken();


                                    CountDownTimer timer = new CountDownTimer(3000, 1000) {
                                        @Override
                                        public void onTick(long l) {
                                            showProgressBar();
                                        }

                                        @Override
                                        public void onFinish() {
                                            Intent setupIntent = new Intent(LoginActivity.this, MainPage.class);
                                            startActivity(setupIntent);
                                            hideProgressBar();

                                            Map<String, Object> updateDevideId = new HashMap<>();
                                            updateDevideId.put("device_id", device_id);
                                            finish();
                                            mReferenceUsers.collection("Users").document(current_userID)
                                                    .update(updateDevideId)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("error update auto login", e.getLocalizedMessage());
                                                }
                                            });
                                        }
                                    }.start();
                                } else {
                                    hideProgressBar();

                                }
                            }
                        } else {
                            Log.e("error auto login", String.valueOf(task.getException()));
                        }
                    }
                }
            });
        }

       /* if (mAuth.getCurrentUser() != null) {
            user_id = mAuth.getCurrentUser().getUid();
            mReferenceUsers = mDatabase.getReference("Users");


            mReferenceUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(user_id)) {

                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user.isEmailVerified() && dataSnapshot.child(user_id).hasChild("username")) {
                            final String current_userID = mAuth.getCurrentUser().getUid();
                            final String device_id = FirebaseInstanceId.getInstance().getToken();


                                    CountDownTimer timer = new CountDownTimer(3000, 1000) {
                                        @Override
                                        public void onTick(long l) {

                                        }

                                        @Override
                                        public void onFinish() {
                                            Intent setupIntent = new Intent(LoginActivity.this, MainPage.class);
                                            startActivity(setupIntent);
                                            mDialog.dismiss();

                                            finish();
                                            mReferenceUsers.child(current_userID).child("device_id").setValue(device_id);
                                        }
                                    }.start();

                        } else {

                            mDialog.dismiss();

                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            }); */

    }

    public void showProgressBar(){
//        loginProgressBar.setVisibility(View.VISIBLE);
//        LinearLayout parentsLayout = (LinearLayout) findViewById(R.id.parentLayoutLogin);
//        parentsLayout.setVisibility(View.INVISIBLE);

        if (dialog != null && !dialog.isShowing() && !this.isFinishing())
            dialog.show();
    }
    public void hideProgressBar(){
        dialog.dismiss();
//        loginProgressBar.setVisibility(View.INVISIBLE);
//        LinearLayout parentsLayout = (LinearLayout) findViewById(R.id.parentLayoutLogin);
//        parentsLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login) {
            checkGoogleSignIn = false;
            checkFacebookLogin = false;
            RegisterActivity.registerPressed = true;
            checkLoginPressed = true;
            checkLogin();
        } else if (v.getId() == R.id.link_signup) {
            Intent goToReg = new Intent(LoginActivity.this, RegisterActivity.class);
            goToReg.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(goToReg);
        } else if (v.getId() == R.id.forgot_password) {
            Intent openForgotPassActivity = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(openForgotPassActivity);
        } else if (v.getId() == R.id.google_login) {

            LoginManager.getInstance().logOut();
            checkGoogleSignIn = true;
            checkFacebookLogin = false;
            RegisterActivity.registerPressed = false;
            checkLoginPressed = false;

            Log.i("loginGoogle", String.valueOf(checkGoogleSignIn));
            signIn();

        } else if (v.getId() == R.id.facebbok_login) {

            checkFacebookLogin = true;
            checkGoogleSignIn = false;
            RegisterActivity.registerPressed = false;
            checkLoginPressed = false;
            mCallbackManager = CallbackManager.Factory.create();
            Log.i("loginFB", String.valueOf(checkFacebookLogin));

            LoginManager loginButton = LoginManager.getInstance();
            loginButton.logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
            loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d(TAG, "facebook:onSuccess:" + loginResult);
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "facebook:onCancel");
                    // ...
                }

                @Override
                public void onError(FacebookException error) {
                    Log.d(TAG, "facebook:onError", error);
                    // ...
                }
            });
        }

    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
        dialog.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
    }

    @Override
    public void onBackPressed() {

        Intent closeApp = new Intent(Intent.ACTION_MAIN);
        closeApp.addCategory(Intent.CATEGORY_HOME);
        closeApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(closeApp);
        finish();
        System.exit(0);
    }
}
