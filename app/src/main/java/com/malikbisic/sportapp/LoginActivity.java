package com.malikbisic.sportapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.ui.auth.ui.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.util.Arrays;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "LoginActivity";
    private GoogleApiClient mGoogleApiClient;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceUsers;
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
    private String emailAddress;
    private ProgressDialog mDialog;
    static String gFirstName;
    static String gLastName;
    static String gUserId;

    static String fbFirstName;

    static String fbLastName;

    static String userIdLogin;

    static boolean checkgoogleSignIn = false;
    static boolean checkFacebookSignIn = false;
    static boolean checkLoginPressed = false;


    CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_login);
        AppEventsLogger.activateApp(getApplication());
        mEmailText = (EditText) findViewById(R.id.input_email_login);
        mPasswordText = (EditText) findViewById(R.id.input_password_login);
        mLoginButton = (Button) findViewById(R.id.btn_login);
        mSignUpLink = (TextView) findViewById(R.id.link_signup);
        googleSignIn = (ImageButton) findViewById(R.id.google_login);
        facebookLogin = (ImageButton) findViewById(R.id.fb_login);
        emailError = (TextView) findViewById(R.id.emailInfoErrorLogin);
        passwordError = (TextView) findViewById(R.id.passwordInfoErrorLogin);
        mForgotPassword = (TextView) findViewById(R.id.forgot_password);
        mDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mLoginButton.setOnClickListener(this);
        mSignUpLink.setOnClickListener(this);
        mForgotPassword.setOnClickListener(this);
        googleSignIn.setOnClickListener(this);
        mDialog = new ProgressDialog(this);
        mReferenceUsers = mDatabase.getReference("Users");
        mReferenceUsers.keepSynced(true);


        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                handleFacebookAccessToken(loginResult.getAccessToken());
                checkUserExists();


            Profile fb = Profile.getCurrentProfile();

                fbFirstName = fb.getFirstName();
                fbLastName = fb.getLastName();

                Log.i("fbname", fbFirstName);
                Log.i("fnsurname", fbLastName);


            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

                Log.e("Facebook error", error.getMessage());

            }
        });

        mAuth = FirebaseAuth.getInstance();




        //GOOGLE SIGN IN

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


        facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkgoogleSignIn = false;
                checkFacebookSignIn = true;
                checkLoginPressed = false;
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));
            }
        });


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
            mDialog.setMessage("Starting sign in...");
            mDialog.show();
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount acct = result.getSignInAccount();


            Uri personPhoto = acct.getPhotoUrl();


            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                mDialog.dismiss();
            }
        }


        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

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


                        gFirstName = acct.getGivenName();
                        gLastName = acct.getFamilyName();
                        gUserId = user.getUid();


                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {

                           checkUserExists();

                            mDialog.dismiss();

                        }

                    }
                });
    }

    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());


                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());


                            String error = String.valueOf(task.getException().getMessage());

                            if (error.equals(getString(R.string.error_facebook_account_already_exists))) {
                                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                                mDialog.dismiss();
                            }
                        }

                        // ...
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
                        userIdLogin = user.getUid();
                        checkUserExists();

                    } else {
                        mDialog.dismiss();
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
        mDialog.setMessage("Please wait...");
        mDialog.show();
        if (mAuth.getCurrentUser() != null) {

            user_id = mAuth.getCurrentUser().getUid();
            mReferenceUsers = mDatabase.getReference("Users");


            mReferenceUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(user_id)) {

                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user.isEmailVerified() && dataSnapshot.child(user_id).hasChild("username")) {
                            Intent setupIntent = new Intent(LoginActivity.this, MainPage.class);
                            startActivity(setupIntent);
                            mDialog.dismiss();
                        } else if (user.isEmailVerified() && !dataSnapshot.child(user_id).hasChild("username")) {
                            Intent intent = new Intent(LoginActivity.this, EnterUsernameForApp.class);
                            startActivity(intent);
                            mDialog.dismiss();
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_LONG).show();
                        }
                    }  else {
                        Intent goToSetUp = new Intent(LoginActivity.this, EnterUsernameForApp.class);
                        goToSetUp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(goToSetUp);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login) {
            checkgoogleSignIn = false;
            checkFacebookSignIn = false;
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
            checkgoogleSignIn = true;
            checkLoginPressed = false;
            checkFacebookSignIn = false;
            signIn();

        }

    }
}
