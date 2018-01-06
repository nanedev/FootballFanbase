package com.malikbisic.sportapp.activity.firebase;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.malikbisic.sportapp.R;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {
    private static final String TAG = "RegisterActivity";
    private EditText mNameText;
    private EditText mSurnameText;
    private EditText mEmailText;
    private EditText mPasswordText;
    private EditText mReEnterPasswordText;
    private Button mSignupButton;

    TextView mLoginLink;
    private TextView errorName, errorSurname, errorEmail, errorPassword, errorRePassword;
    private String userName;
    String userEmail;
    String userPassword;
    private String userSurname;
    static String user_id;
    static String name;
    static String surname;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
     FirebaseDatabase mDatabase;
  DatabaseReference mReferenceUsers;
    static boolean checkLoginPressed = false;
    FirebaseFirestore db;

    LinearLayout layout;

    private ProgressDialog progressDialog;

    boolean valid;

    private String error;
    public static boolean registerPressed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_register);
        mNameText = (EditText) findViewById(R.id.input_name);
        mSurnameText = (EditText) findViewById(R.id.input_surname);
        mEmailText = (EditText) findViewById(R.id.input_email);
        mPasswordText = (EditText) findViewById(R.id.input_password);
        mReEnterPasswordText = (EditText) findViewById(R.id.input_reEnterPassword);
        mSignupButton = (Button) findViewById(R.id.btn_signup);
        errorName = (TextView) findViewById(R.id.input_nameInfoError);
        errorSurname = (TextView) findViewById(R.id.input_surnameInfoError);
        errorEmail = (TextView) findViewById(R.id.input_emailInfoError);
        errorPassword = (TextView) findViewById(R.id.input_passwordInfoError);
        errorRePassword = (TextView) findViewById(R.id.input_rePasswordInfoError);
        mLoginLink = (TextView) findViewById(R.id.link_login);
        layout = (LinearLayout) findViewById(R.id.registerLayout);
        layout.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.i("tag", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.i("Tag", "onAuthStateChanged:signed_out");
                }

            }
        };

        progressDialog = new ProgressDialog(this);
        mDatabase = FirebaseDatabase.getInstance();
        db = FirebaseFirestore.getInstance();
        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = mNameText.getText().toString().trim();
                surname = mSurnameText.getText().toString().trim();
                LoginActivity.checkGoogleSignIn = false;

                checkLoginPressed = true;
                signup();
            }
        });

        mLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity

                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });


    }

    public void signup() {
        Log.d(TAG, "RegisterActivity");
        if (!validate()) {
            onSignupFailed();
            return;
        }

        mSignupButton.setEnabled(false);

        progressDialog = new ProgressDialog(RegisterActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        registerUser();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        if (error == null) {
                            onSignupSuccess();
                        } else if (error != null) {
                            onSignupFailed();
                            error = null;
                        }
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    public void onSignupSuccess() {
        mSignupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.putExtra("firstName",userName);
        intent.putExtra("secondName", userSurname);


        Map<String, Object> user = new HashMap<>();
        user.put("name",userName);
        user.put("surname",userSurname);
     db.collection("Users").document(user_id)
             .set(user)
             .addOnSuccessListener(new OnSuccessListener<Void>() {
                 @Override
                 public void onSuccess(Void aVoid) {

                 }
             }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {

         }
     });


      /*  DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        Map userMAP = new HashMap();
        userMAP.put("name", userName);
        userMAP.put("surname", userSurname);
        usersRef.updateChildren(userMAP, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){
                    Log.e("registeruser", databaseError.getMessage());
                }
            }
        });*/
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        registerPressed = true;
        LoginActivity.checkGoogleSignIn = false;
        startActivity(intent);
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        mSignupButton.setEnabled(true);
    }

    public boolean validate() {
        valid = true;
        name = mNameText.getText().toString();
        surname = mSurnameText.getText().toString();
        String password = mPasswordText.getText().toString();
        String reEnterPassword = mReEnterPasswordText.getText().toString();


        if (mEmailText.getText().toString().isEmpty()) {
            errorEmail.setText("Field can not be empty");
            errorEmail.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            errorEmail.setText("");
            errorEmail.setVisibility(View.GONE);
        }


        if (name.isEmpty()) {
            errorName.setText("Field can not be empty");
            errorName.setVisibility(View.VISIBLE);
            valid = false;
        } else if (name.length() < 3) {
            errorName.setText("Field should contain at least 3 characters");
            errorName.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            errorName.setText("");
            errorName.setVisibility(View.GONE);
        }

        if (surname.isEmpty()) {
            errorSurname.setText("Field can not be empty");
            errorSurname.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            errorSurname.setText("");
            errorSurname.setVisibility(View.GONE);
        }

        if (password.isEmpty()) {
            errorPassword.setText("Field can not be empty");
            errorPassword.setVisibility(View.VISIBLE);
            valid = false;
        } else if (password.length() < 6) {
            errorPassword.setText("Password must contain at least 6 characters");
            errorPassword.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            errorPassword.setText("");
            errorPassword.setVisibility(View.GONE);
        }

        if (reEnterPassword.isEmpty()) {
            errorRePassword.setText("Field can not be empty");
            errorRePassword.setVisibility(View.VISIBLE);
            valid = false;
        } else if (reEnterPassword.length() < 6) {
            errorRePassword.setText("Password must contain at least 6 characters");
            errorRePassword.setVisibility(View.VISIBLE);
            valid = false;
        } else if (!(reEnterPassword.equals(password))) {
            errorRePassword.setText("Password do not match!");
            errorRePassword.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            errorRePassword.setText("");
            errorRePassword.setVisibility(View.GONE);
        }

        return valid;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.registerLayout) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } else if (view.getId() == R.id.link_login) {
            Intent goToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
            goToLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(goToLogin);
        }
    }


    private void registerUser() {

        //nesto
        userName = mNameText.getText().toString().trim();
        userSurname = mSurnameText.getText().toString().trim();
        userEmail = mEmailText.getText().toString().trim();
        userPassword = mPasswordText.getText().toString().trim();


        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userSurname) && !TextUtils.isEmpty(userEmail)
                && !TextUtils.isEmpty(userPassword)) {


            mAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getInstance().getCurrentUser();
                        if (user != null)
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Please verify your email!", Toast.LENGTH_LONG).show();
                                    }
                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });


                        if (task.isSuccessful()) {

                            user_id = mAuth.getCurrentUser().getUid();
//                            mReferenceUsers = mDatabase.getReference().child("Users").child(user_id);
//                            mReferenceUsers.child("name").setValue(userName);
//                            mReferenceUsers.child("surname").setValue(userSurname);


                        }

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    error = e.getMessage();


                    errorEmail.setText(e.getMessage());
                    errorEmail.setVisibility(View.VISIBLE);
                    mEmailText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mEmailText, InputMethodManager.SHOW_IMPLICIT);

                    progressDialog.dismiss();
                    onSignupFailed();

                    Log.i("email exists", e.getMessage());

                }
            });
        }


    }


    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if (i == KeyEvent.KEYCODE_ENTER) {
            registerUser();
        }

        return false;
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
