package com.malikbisic.sportapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.DateActivity;

import java.util.ArrayList;

import static com.malikbisic.sportapp.activity.DateActivity.positionClicked;

/**
 * Created by Nane on 24.12.2017.
 */

public class ListAdapter extends ArrayAdapter<String> {




    public ListAdapter(@NonNull Context context, ArrayList<String> dateItems) {
        super(context, R.layout.list_item_row, dateItems);
    }


    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.list_item_row, parent, false);
        String singleItem = getItem(position);
        TextView text = (TextView) view.findViewById(R.id.item_list_text_date);
        text.setText(singleItem);

        if (DateActivity.isClickedDate) {
            if (position == positionClicked) {
                view.setBackgroundColor(R.color.black);
                text.setTextColor(R.color.white);
            }
        } else {
            if (position == 15) {
                view.setBackgroundColor(R.color.black);
                text.setTextColor(R.color.white);
            }
        }
        return view;
    }




}
