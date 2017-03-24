package com.malikbisic.sportapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class EnterUsernameForApp extends AppCompatActivity implements View.OnClickListener {
    private EditText enterUsername;
    private Button contunue;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private String value;
    private boolean valid = true;
    private String username;
    Spinner genderItems;
    List<String> spinnerArray;
    ArrayAdapter<String> adapter;
    int selectYear;
    int currentYear;
    int realYear;
    Calendar minAdultAge;

    String googleUser_id;
    String googleFirstName;
    String googleLastName;
    String gender;
    ProgressDialog mDialog;

    EditText birthday;
    private ArrayList<String> nickList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_username);
        enterUsername = (EditText) findViewById(R.id.input_username);
        birthday = (EditText) findViewById(R.id.input_dateofbirth_usernameAkt);
        genderItems = (Spinner) findViewById(R.id.input_gender_usernmaeAkt);
        nickList = new ArrayList<>();
        contunue = (Button) findViewById(R.id.continue_to_profil);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDialog = new ProgressDialog(this);

        spinnerArray = new ArrayList<>();
        spinnerArray.add("Male");
        spinnerArray.add("Female");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        genderItems.setAdapter(adapter);

        contunue.setOnClickListener(this);
        birthday.setOnClickListener(this);

        minAdultAge = new GregorianCalendar();

        googleUser_id = LoginActivity.gUserId;
        googleFirstName = LoginActivity.gFirstName;
        googleLastName = LoginActivity.gLastName;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Usernames");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject object : objects) {
                        nickList.add(String.valueOf(object.get("username")));
                    }
                }
            }
        });

        genderItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gender = String.valueOf(adapterView.getItemAtPosition(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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


    private void updateLabel() {

        String myFormat = "dd/MMMM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        birthday.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.continue_to_profil) {

            if (valid()) {
                if (LoginActivity.checkgoogleSignIn == true){
                    googleEnterDatabase();
                }
                Intent intent = new Intent(EnterUsernameForApp.this, ProfilActivity.class);
                startActivity(intent);
            } else {
                mDialog.dismiss();
            }

        } else if (v.getId() == R.id.input_dateofbirth_usernameAkt) {
            new DatePickerDialog(EnterUsernameForApp.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();

        }

    }


    public boolean valid() {
        valid = true;

        String nick = enterUsername.getText().toString().trim();
        if (TextUtils.isEmpty(enterUsername.getText().toString())) {
            enterUsername.setError("field can not be blank");
            valid = false;
        } else if (nickList.contains(nick)) {
            Toast.makeText(EnterUsernameForApp.this, "Username already exists,can not continue!", Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            enterUsername.setError(null);

        }

        if (realYear < 18) {

            Toast.makeText(EnterUsernameForApp.this, "You must be older than 18!", Toast.LENGTH_LONG).show();


            valid = false;
        } else {

        }

        return valid;
    }

    public void googleEnterDatabase(){

        String username = enterUsername.getText().toString().trim();
        String userDate = birthday.getText().toString().trim();

        mDialog.setMessage("Registering...");
        mDialog.show();
        mReference = mDatabase.getReference().child("Users").child(googleUser_id);
        mReference.child("name").setValue(googleFirstName);
        mReference.child("surname").setValue(googleLastName);
        mReference.child("nick").setValue(username);
        mReference.child("date").setValue(userDate);
        mReference.child("gender").setValue(gender);

        ParseObject object = new ParseObject("Usernames");
        object.put("username", username);
        object.saveInBackground();
        mDialog.dismiss();
    }

}
