package com.malikbisic.sportapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.ListAdapter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateActivity extends AppCompatActivity {
    Calendar c;
    SimpleDateFormat df;
    String formattedDate;

    ArrayList<String> dateList;
    ArrayAdapter<String> adapter;
    ListView listView;
    public static boolean isClickedDate = false;
    public static int positionClicked;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        listView = (ListView) findViewById(R.id.dateList);
        dateList = new ArrayList<String>();
        c = Calendar.getInstance();
toolbar = (Toolbar) findViewById(R.id.toolbardatelist);
setSupportActionBar(toolbar);

getSupportActionBar().setTitle("Select date");

        adapter = new ListAdapter(this, dateList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 7) {
                    view.requestFocus();
                    view.setSelected(true);


                }
            }
        });

        DateTime today = new DateTime().withTimeAtStartOfDay();
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy");
        for (int i = 7; i > 0; i--) {
            dateList.add(formatter.print(today.minusDays(i)));
            adapter.notifyDataSetChanged();
        }

        for (int i = 0; i < 8; i++) {
            dateList.add(formatter.print(today.plusDays(i)));
            adapter.notifyDataSetChanged();
        }


        if (!isClickedDate) {
            listView.smoothScrollToPosition(11);
        } else {
            listView.smoothScrollToPosition(positionClicked);
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                df = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                String selected = String.valueOf(adapterView.getItemAtPosition(i));
                isClickedDate = true;
                positionClicked = i;


                String mytime = selected;
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "dd.MM.yyyy", Locale.getDefault());
                Date myDate = null;
                try {
                    myDate = dateFormat.parse(mytime);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String finalDate = timeFormat.format(myDate);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("newDate", finalDate);

                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}
