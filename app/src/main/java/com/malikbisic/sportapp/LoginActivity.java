package com.malikbisic.sportapp;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
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
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private ProgressDialog mDialog;
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
    boolean valid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(getApplicationContext());
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


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                }
            }
        };


    }

    private void checkLogin() {
        email = mEmailText.getText().toString().trim();
        password = mPasswordText.getText().toString().trim();


        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

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

        user_id = mAuth.getCurrentUser().getUid();
        mReference = mDatabase.getReference("Users");

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id)) {

                    FirebaseUser user = mAuth.getCurrentUser();

                    if (user.isEmailVerified()) {
                        Intent setupIntent = new Intent(LoginActivity.this, SetUpAccount.class);
                        startActivity(setupIntent);
                    } else {
                        Toast.makeText(LoginActivity.this, "Email has not verifacte", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "You need to setup your account", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onStart() {

        mAuth.addAuthStateListener(mAuthListener);
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login) {
            checkLogin();
        } else if (v.getId() == R.id.link_signup) {
            Intent goToReg = new Intent(LoginActivity.this, RegisterActivity.class);
            goToReg.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(goToReg);
        } else if (v.getId() == R.id.forgot_password) {
            mAuth = FirebaseAuth.getInstance();

            emailAddress = mEmailText.getText().toString();
            if (TextUtils.isEmpty(emailAddress)) {
                emailError.setError("Field can not be blank!");
            } else {

                mAuth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Check your email", Toast.LENGTH_LONG).show();

                                    Log.i("proba", "email sent");
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if (e.getMessage().equals(getString(R.string.error_reset_pass_blank_email_field))) {
                            emailError.setText("Bad formatted email");
                            emailError.setVisibility(View.VISIBLE);
                        } else if (e.getMessage().equals(getString(R.string.error_reset_pass_email_doesnt_exists))) {
                            emailError.setText("This email does not exists");
                            emailError.setVisibility(View.VISIBLE);
                        } else {
                            emailError.setText("");
                            emailError.setVisibility(View.INVISIBLE);
                        }
                        Log.i("errori", e.getMessage());
                    }
                });
            }

        }

    }
}
