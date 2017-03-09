package com.malikbisic.sportapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.DatePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener, TextWatcher {
    private static final String TAG = "RegisterActivity";
    EditText mNameText;
    EditText mSurnameText;
    EditText mEmailText;
    EditText mNickNameText;
    EditText mPasswordText;
    EditText mReEnterPasswordText;
    EditText dateTx;
    Button mSignupButton;
    TextView mLoginLink;

    String userDate;
    String userGender;
    String userName;
    String userNick;
    String userEmail;
    String userPassword;
    String userSurname;
    String user_id;
    Spinner genderItems;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;


    List<String> spinnerArray;
    ArrayAdapter<String> adapter;
    ProgressDialog mDialog;

    FirebaseDatabase mDatabase;
    DatabaseReference mReference;

    LinearLayout layout;

    boolean valid;


    Calendar minAdultAge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_register);
        mNameText = (EditText) findViewById(R.id.input_name);
        mSurnameText = (EditText) findViewById(R.id.input_surname);
        mEmailText = (EditText) findViewById(R.id.input_email);
        mNickNameText = (EditText) findViewById(R.id.input_nickname);
        mPasswordText = (EditText) findViewById(R.id.input_password);
        mReEnterPasswordText = (EditText) findViewById(R.id.input_reEnterPassword);
        mSignupButton = (Button) findViewById(R.id.btn_signup);
        dateTx = (EditText) findViewById(R.id.input_dateofbirth);

        dateTx.setOnClickListener(this);
        mLoginLink = (TextView) findViewById(R.id.link_login);
        layout = (LinearLayout) findViewById(R.id.registerLayout);
        layout.setOnClickListener(this);
        mNameText.addTextChangedListener(this);
        mSurnameText.addTextChangedListener(this);
        mEmailText.addTextChangedListener(this);
        mNickNameText.addTextChangedListener(this);
        mPasswordText.addTextChangedListener(this);
        mReEnterPasswordText.addTextChangedListener(this);
        minAdultAge = new GregorianCalendar();

        dateTx.addTextChangedListener(this);


        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("tag", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("Tag", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        mDialog = new ProgressDialog(this);
        mDatabase = FirebaseDatabase.getInstance();
        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        mLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        spinnerArray = new ArrayList<>();
        spinnerArray.add("Male");
        spinnerArray.add("Female");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderItems = (Spinner) findViewById(R.id.input_gender);
        genderItems.setAdapter(adapter);

    }

    public void signup() {
        Log.d(TAG, "RegisterActivity");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        mSignupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        registerUser();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    public void onSignupSuccess() {
        mSignupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        // finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        mSignupButton.setEnabled(true);
    }

    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            minAdultAge.add(Calendar.YEAR, -18);

            updateLabel();
        }

    };

    public boolean validate() {
        valid = true;

        String name = mNameText.getText().toString();
        String surname = mSurnameText.getText().toString();
        String email = mEmailText.getText().toString();
        String nick = mNickNameText.getText().toString();
        String password = mPasswordText.getText().toString();
        String reEnterPassword = mReEnterPasswordText.getText().toString();
        String date = dateTx.getText().toString().trim();

        if (name.isEmpty()) {
            mNameText.setError("field can not be empty");
            valid = false;
        } else if (name.length() < 3) {
            mNameText.setError("field should contain at least 3 characters");
            valid = false;
        } else {
            mNameText.setError(null);
        }

        if (surname.isEmpty()) {
            mSurnameText.setError("Enter Surname");
            valid = false;
        } else {
            mSurnameText.setError(null);
        }


        if (email.isEmpty()) {
            mEmailText.setError("field can not be empty");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailText.setError("bad format of email");
            valid = false;
        } else {
            mEmailText.setError(null);
        }

        if (nick.isEmpty()) {
            mNickNameText.setError("field can not be empty");
            valid = false;
        } else {
            mNickNameText.setError(null);
        }

        if (password.isEmpty()) {
            mPasswordText.setError("field can not be empty");
            valid = false;
        } else if (password.length() < 6) {
            mPasswordText.setError("password must contain at least 6 characters");
            valid = false;
        } else {
            mPasswordText.setError(null);
        }

        if (reEnterPassword.isEmpty()) {
            mReEnterPasswordText.setError("field can not be empty");
            valid = false;
        } else if (reEnterPassword.length() < 6) {
            mReEnterPasswordText.setError("password must contain at least 6 characters");
            valid = false;
        } else if (!(reEnterPassword.equals(password))) {
            mReEnterPasswordText.setError("password do not match!");
            valid = false;
        } else {
            mReEnterPasswordText.setError(null);
        }

       if (minAdultAge.before(myCalendar)) {
            Toast.makeText(RegisterActivity.this, "You must be older 18 ", Toast.LENGTH_LONG).show();
           minAdultAge.add(Calendar.YEAR, -18);
            valid = false;
        } else {

        }
        return valid;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.input_dateofbirth) {
            new DatePickerDialog(RegisterActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();

        } else if (view.getId() == R.id.registerLayout) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void registerUser() {
        userName = mNameText.getText().toString().trim();
        userSurname = mSurnameText.getText().toString().trim();
        userNick = mNickNameText.getText().toString().trim();
        userEmail = mEmailText.getText().toString().trim();
        userPassword = mPasswordText.getText().toString().trim();
        userDate = dateTx.getText().toString().trim();
        userGender = genderItems.getSelectedItem().toString().trim();


        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userSurname) && !TextUtils.isEmpty(userNick) && !TextUtils.isEmpty(userEmail)
                && !TextUtils.isEmpty(userPassword)) {


            mAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    FirebaseUser user = mAuth.getInstance().getCurrentUser();
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Check your email!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });


                    if (task.isSuccessful()) {

                        user_id = mAuth.getCurrentUser().getUid();
                        mReference = mDatabase.getReference().child("Users").child(user_id);
                        mReference.child("name").setValue(userName);
                        mReference.child("surname").setValue(userSurname);
                        mReference.child("nick").setValue(userNick);
                        mReference.child("date").setValue(userDate);
                        mReference.child("gender").setValue(userGender);
                        mReference = mDatabase.getReference().child("Nickname").push();
                        mReference.setValue(userNick);


                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {


                }
            });
        }


    }


    private void updateLabel() {

        String myFormat = "dd/MMMM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        dateTx.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if (i == KeyEvent.KEYCODE_ENTER) {
            registerUser();
        }

        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        validate();
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
