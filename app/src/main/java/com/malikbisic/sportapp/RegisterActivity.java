package com.malikbisic.sportapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText dateTx;
    final int Date_Dialog_ID=0;
    int cDay,cMonth,cYear;
    Calendar cDate;
    int sDay,sMonth,sYear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        dateTx = (EditText) findViewById(R.id.dateRegLabel);
        dateTx.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.dateRegLabel){
            showDialog(Date_Dialog_ID);

        }

    cDate=Calendar.getInstance();

    cDay=cDate.get(Calendar.DAY_OF_MONTH);

    cMonth=cDate.get(Calendar.MONTH);

    cYear=cDate.get(Calendar.YEAR);

    sDay=cDay;

    sMonth=cMonth;

    sYear=cYear;

    updateDateDisplay(sYear,sMonth,sDay);

        }

    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {

            case Date_Dialog_ID:

                return new DatePickerDialog(this, onDateSet, cYear, cMonth, cDay);

        }

        return null;

    }
    private void updateDateDisplay(int year,int month,int date) {

        String date2 = date +" - " +(month+1)+" - "+year;
        dateTx.setText(date2);

    }

    private DatePickerDialog.OnDateSetListener onDateSet=new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(android.widget.DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            System.out.println("2");

            sYear=year;

            sMonth=monthOfYear;

            sDay=dayOfMonth;

            updateDateDisplay(sYear,sMonth,sDay);
        }



    };

}
