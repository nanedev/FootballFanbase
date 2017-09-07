package com.malikbisic.sportapp.model;

import android.support.v7.widget.RecyclerView;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by korisnik on 03/09/2017.
 */

public class UserChatGroup extends ExpandableGroup {
String clubLogo;
    String online;
    public UserChatGroup(String title, List items,String clubLogo) {
        super(title, items);

        this.clubLogo = clubLogo;
        this.online = online;
    }

    public String getClubLogo() {
        return clubLogo;
    }

    public void setClubLogo(String clubLogo) {
        this.clubLogo = clubLogo;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }


}
