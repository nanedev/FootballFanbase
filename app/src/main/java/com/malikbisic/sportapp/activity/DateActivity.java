package com.malikbisic.sportapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.malikbisic.sportapp.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DateActivity extends AppCompatActivity {
    Calendar c;
    SimpleDateFormat df;
    String formattedDate;

    ArrayList dateList;
    ArrayAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        listView = (ListView) findViewById(R.id.dateList);
        dateList = new ArrayList();
        c = Calendar.getInstance();


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .8));
        getSupportActionBar().hide();
            adapter = new ArrayAdapter(DateActivity.this, android.R.layout.simple_list_item_1, dateList);
            listView.setAdapter(adapter);
            DateTime today = new DateTime().withTimeAtStartOfDay();
            DateTimeFormatter formatter = DateTimeFormat.forPattern( "dd-MM-yyyy" );
            for(int i=15; i>0; i--){
                dateList.add( formatter.print(today.minusDays(i)));
                adapter.notifyDataSetChanged();
            }

        for(int i=0; i<16; i++){
            dateList.add( formatter.print(today.plusDays(i)));
            adapter.notifyDataSetChanged();
        }


        listView.smoothScrollToPosition(19);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                df = new SimpleDateFormat("dd-MM-yyyy");
                String selected = String.valueOf(adapterView.getItemAtPosition(i));




                String mytime= selected;
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "dd-MM-yyyy");
                Date myDate = null;
                try {
                    myDate = dateFormat.parse(mytime);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
                String finalDate = timeFormat.format(myDate);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("newDate", finalDate);

                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }
}
