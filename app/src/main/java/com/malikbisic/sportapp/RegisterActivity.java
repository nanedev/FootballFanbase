package com.malikbisic.sportapp;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EditText dateText = (EditText) findViewById(R.id.dateRegLabel);
        dateText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                DatePicker datePicker = new DatePicker(view);
                android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                datePicker.show(transaction, "DatePicker");
            }
        });
    }
}
