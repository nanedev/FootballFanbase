package com.malikbisic.sportapp.model;

import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.application.Application;
import com.malikbisic.sportapp.model.api.PlayerModel;


import java.util.Arrays;
import java.util.List;

/**
 * Created by korisnik on 06/01/2018.
 */

public class FootballPlayer {

    private static final String STORAGE = "player";

    public static FootballPlayer get() {
        return new FootballPlayer();
    }

    private SharedPreferences storage;

    private FootballPlayer() {
        storage = Application.getInstance().getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
    }

    public List<PlayerModel> getData() {
        return Arrays.asList(
                new PlayerModel(1, "BiH", R.drawable.bihflag),
                new PlayerModel(2, "CostaRica", R.drawable.flag_cr),
                new PlayerModel(3, "Sweden", R.drawable.flag_se),
                new PlayerModel(4, "Armenia", R.drawable.flag_am),
                new PlayerModel(5, "Hrvatska", R.drawable.flag_hr),
                new PlayerModel(6, "Njemačka", R.drawable.flag_de));
    }
}
