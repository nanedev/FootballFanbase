package com.malikbisic.sportapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {
    private EditText nameField;
    private EditText nickField;
    private EditText emailField;
    private EditText passwordField;
    private ConstraintLayout registerLayout;

    Button regBtn;

    String userDate;
    String userGender;
    String userName;
    String userNick;
    String userEmail;
    String userPassword;
    String user_id;
    Spinner genderItems;


    FirebaseAuth mAuth;


    EditText dateTx;
    List<String> spinnerArray;
    ArrayAdapter<String> adapter;
    ProgressDialog mDialog;

    FirebaseDatabase mDatabase;
    DatabaseReference mReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_register);
        nameField = (EditText) findViewById(R.id.nameRegLabel);
        nickField = (EditText) findViewById(R.id.nicknameRegLabel);
        emailField = (EditText) findViewById(R.id.emailLabel);
        passwordField = (EditText) findViewById(R.id.passLabel);
        regBtn = (Button) findViewById(R.id.registerBtnRegActivity);
        registerLayout = (ConstraintLayout) findViewById(R.id.registerLayout);

        dateTx = (EditText) findViewById(R.id.dateRegLabel);
        dateTx.setOnClickListener(this);
        spinnerArray = new ArrayList<>();
        spinnerArray.add("Male");
        spinnerArray.add("Female");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderItems = (Spinner) findViewById(R.id.spinnerRegGender);
        genderItems.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);
        mDatabase = FirebaseDatabase.getInstance();

        registerLayout.setOnClickListener(this);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.dateRegLabel) {
            new DatePickerDialog(RegisterActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        } else if (view.getId() == R.id.registerBtnRegActivity) {
            Log.i("klikno","klikno si");


        } else if (view.getId() == R.id.registerLayout){
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void registerUser() {

        userName = nameField.getText().toString().trim();
        userNick = nickField.getText().toString().trim();
        userEmail = emailField.getText().toString().trim();
        userPassword = passwordField.getText().toString().trim();
        userDate = dateTx.getText().toString().trim();
        userGender = genderItems.getSelectedItem().toString().trim();



        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userNick) && !TextUtils.isEmpty(userEmail)
                && !TextUtils.isEmpty(userPassword) && !TextUtils.isEmpty(userDate) && !TextUtils.isEmpty(userGender))
        {
            mDialog.setMessage("Registering");
            mDialog.show();


            mAuth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){
                       user_id = mAuth.getCurrentUser().getUid();
                        mReference= mDatabase.getReference().child("Users").child(user_id);
                        mReference.child("name").setValue(userName);
                        mReference.child("nick").setValue(userNick);
                        mReference.child("date").setValue(userDate);
                        mReference.child("gender").setValue(userGender);

                        mDialog.dismiss();



                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    if (TextUtils.isEmpty(userPassword) || userPassword.length() < 6){
                        passwordField.setError("Password must be at least 6 characters");
                        mDialog.dismiss();
                    }

                    emailField.setError(e.getMessage());
                    mDialog.dismiss();

                }
            });
        }

        if (TextUtils.isEmpty(userName)) {
            nameField.setError("You did not enter a name");
            mDialog.dismiss();
        }

        if (TextUtils.isEmpty(userNick)) {
            nickField.setError("You did not enter a username");
            mDialog.dismiss();
        }
        if (TextUtils.isEmpty(userDate)) {
            dateTx.setError("You did not enter a birthday");
            mDialog.dismiss();
        }


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

            updateLabel();
        }

    };

    private void updateLabel() {

        String myFormat = "dd/MMMM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        dateTx.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if (i == KeyEvent.KEYCODE_ENTER){
            registerUser();
        }

        return false;
    }
}
