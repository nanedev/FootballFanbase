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
  /*  private EditText nameField;
    private EditText nickField;
    private EditText emailField;
    private EditText passwordField;
    private ConstraintLayout registerLayout;*/
  private static final String TAG = "SignupActivity";
    EditText mNameText;
    EditText mSurnameText;
    EditText mEmailText;
    EditText mNickNameText;
    EditText mPasswordText;
    EditText mReEnterPasswordText;
    Button mSignupButton;
    TextView mLoginLink;

    Button regBtn;

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


    EditText dateTx;
    List<String> spinnerArray;
    ArrayAdapter<String> adapter;
    ProgressDialog mDialog;

    FirebaseDatabase mDatabase;
    DatabaseReference mReference;

    LinearLayout layout;


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
        mLoginLink = (TextView) findViewById(R.id.link_login);
        layout = (LinearLayout) findViewById(R.id.registerLayout);
        layout.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
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
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });









      /*  dateTx = (EditText) findViewById(R.id.dateRegLabel);
        dateTx.setOnClickListener(this);
        spinnerArray = new ArrayList<>();
        spinnerArray.add("Male");
        spinnerArray.add("Female");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderItems = (Spinner) findViewById(R.id.spinnerRegGender);
        genderItems.setAdapter(adapter);



        registerLayout.setOnClickListener(this);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

*/
    }
    public void signup() {
        Log.d(TAG, "Signup");

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

        String name = mNameText.getText().toString();
        String surname = mSurnameText.getText().toString();
        String email = mEmailText.getText().toString();
        String nick = mNickNameText.getText().toString();
        String password = mPasswordText.getText().toString();
        String reEnterPassword = mReEnterPasswordText.getText().toString();

        // TODO: Implement your own signup logic here.
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
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        mSignupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = mNameText.getText().toString();
        String surname = mSurnameText.getText().toString();
        String email = mEmailText.getText().toString();
        String nick = mNickNameText.getText().toString();
        String password = mPasswordText.getText().toString();
        String reEnterPassword = mReEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            mNameText.setError("at least 3 characters");
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


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailText.setError("enter a valid email address");
            valid = false;
        } else {
            mEmailText.setError(null);
        }

        if (nick.isEmpty()) {
            mNickNameText.setError("Nick already exists");
            valid = false;
        } else {
            mNickNameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 10) {
            mPasswordText.setError("between 6 and 10 alphanumeric characters");
            valid = false;
        } else {
            mPasswordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 6|| reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            mReEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            mReEnterPasswordText.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View view) {
       /* if (view.getId() == R.id.dateRegLabel) {
            new DatePickerDialog(RegisterActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        } else if (view.getId() == R.id.registerBtnRegActivity) {
            Log.i("klikno","klikno si");


        } else*/ if (view.getId() == R.id.registerLayout){
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
       /* userDate = dateTx.getText().toString().trim();
        userGender = genderItems.getSelectedItem().toString().trim();*/



        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userSurname) &&!TextUtils.isEmpty(userNick) && !TextUtils.isEmpty(userEmail)
                && !TextUtils.isEmpty(userPassword) )
        {


            mAuth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){
                       user_id = mAuth.getCurrentUser().getUid();
                        mReference= mDatabase.getReference().child("Users").child(user_id);
                        mReference.child("name").setValue(userName);
                        mReference.child("name").setValue(userSurname);
                        mReference.child("nick").setValue(userNick);
                        /*mReference.child("date").setValue(userDate);
                        mReference.child("gender").setValue(userGender);*/

                        mReference = mDatabase.getReference().child("Nickname").push();
                        mReference.setValue(userNick);




                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                   /* if (TextUtils.isEmpty(userPassword) || userPassword.length() < 6){
                        passwordField.setError("Password must be at least 6 characters");
                        mDialog.dismiss();
                    }

                    emailField.setError(e.getMessage());
                    mDialog.dismiss();
*/
                }
            });
        }

        /*if (TextUtils.isEmpty(userName)) {
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
*/

    }

   /* Calendar myCalendar = Calendar.getInstance();

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

    };*/

    private void updateLabel() {

       /* String myFormat = "dd/MMMM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        dateTx.setText(sdf.format(myCalendar.getTime()));*/
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if (i == KeyEvent.KEYCODE_ENTER){
            registerUser();
        }

        return false;
    }
}
