package com.malikbisic.sportapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by korisnik on 07/03/2017.
 */

public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    EditText dateText;

    public DatePicker(View view){
        this.dateText = (EditText) view;
    }

    public Dialog onCreateDialog (Bundle savedinstance){
        final Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        return new DatePickerDialog(getActivity(), this, day, month, year);
    }

    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {

        String date = day +" - "+ (month+1)+ " - " + year;
        dateText.setText(date);
    }
}
