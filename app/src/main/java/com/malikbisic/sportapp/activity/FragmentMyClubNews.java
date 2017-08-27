package com.malikbisic.sportapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malikbisic.sportapp.R;

/**
 * Created by Nane on 27.8.2017.
 */

public class FragmentMyClubNews extends Fragment {
TextView textView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_club_news,container,false);
textView = (TextView) view.findViewById(R.id.my_club_news);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("my club news kliked");
            }
        });


        return view;
    }
}
