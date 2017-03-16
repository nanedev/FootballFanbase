package com.malikbisic.sportapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
    TextView errorInfo, errorName, errorSurname, errorEmail, errorPassword, errorRePassword, errorNick;
    String value;

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
    int selectYear;
    int currentYear;
    int realYear;
    ProgressDialog progressDialog;


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
        errorInfo = (TextView) findViewById(R.id.input_dateofbirthInfo);
        errorName = (TextView) findViewById(R.id.input_nameInfoError);
        errorSurname = (TextView) findViewById(R.id.input_surnameInfoError);
        errorEmail = (TextView) findViewById(R.id.input_emailInfoError);
        errorNick = (TextView) findViewById(R.id.input_nicknameInfoError);
        errorPassword = (TextView) findViewById(R.id.input_passwordInfoError);
        errorRePassword = (TextView) findViewById(R.id.input_rePasswordInfoError);
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
                    Log.i("tag", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.i("Tag", "onAuthStateChanged:signed_out");
                }

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
        mReference = mDatabase.getReference();

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

            selectYear = year;
            currentYear = minAdultAge.get(Calendar.YEAR);
            Log.i("Year select", String.valueOf(selectYear));
            Log.i("Year current", String.valueOf(currentYear));

            realYear = currentYear - selectYear;
            Log.i("Year check", String.valueOf(realYear));


            updateLabel();
        }

    };

    public boolean validate() {
        valid = true;

        String name = mNameText.getText().toString();
        String surname = mSurnameText.getText().toString();
        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();
        String reEnterPassword = mReEnterPasswordText.getText().toString();
        String date = dateTx.getText().toString().trim();

        mReference = mDatabase.getReference("Users");

        Query query = mReference.orderByChild("nick").equalTo(mNickNameText.getText().toString());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot nickNames : dataSnapshot.getChildren()) {

                    errorNick.setText("Already exists");
                    errorNick.setVisibility(View.VISIBLE);
                    value = (String) nickNames.child("nick").getValue();
                    //nešto


                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (mNickNameText.getText().toString().isEmpty()) {
            errorNick.setText("Field can not be empty");
            errorNick.setVisibility(View.VISIBLE);
            valid = false;

        } else if (mNickNameText.getText().toString().equals(value)) {
            errorNick.setText("Already exists");
            errorNick.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            errorNick.setText("");
            errorNick.setVisibility(View.INVISIBLE);
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
            errorName.setVisibility(View.INVISIBLE);
        }

        if (surname.isEmpty()) {
            errorSurname.setText("Field can not be empty");
            errorSurname.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            errorSurname.setText("");
            errorSurname.setVisibility(View.INVISIBLE);
        }

      /*  if (email.isEmpty()) {
            errorEmail.setText("Field can not be empty");
            errorEmail.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            errorEmail.setText("");
            errorEmail.setVisibility(View.INVISIBLE);
        } */


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
            errorPassword.setVisibility(View.INVISIBLE);
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
            errorRePassword.setVisibility(View.INVISIBLE);
        }

        if (realYear < 18) {

            errorInfo.setText("You must be older than 18");
            errorInfo.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            errorInfo.setText("");
            errorInfo.setVisibility(View.INVISIBLE);
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
        } else if (view.getId() == R.id.btn_signup) {

        } else if (view.getId() == R.id.link_login) {

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
                    if (task.isSuccessful()) {
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
                                Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

                        }

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    errorEmail.setText(e.getMessage());
                    errorEmail.setVisibility(View.VISIBLE);
                    valid = false;
                    mEmailText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mEmailText, InputMethodManager.SHOW_IMPLICIT);
                    progressDialog.dismiss();
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
    public void onTextChanged(final CharSequence s, int start, int before, int count) {

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
