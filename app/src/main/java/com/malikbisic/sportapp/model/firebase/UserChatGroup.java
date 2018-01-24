package com.malikbisic.sportapp.model.firebase;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.Comparator;
import java.util.List;

/**
 * Created by korisnik on 03/09/2017.
 */

public class UserChatGroup extends ExpandableGroup {
    String clubLogo;
    String online;
    int numberOnline;

    public UserChatGroup(String title, List<UserChat> items, String clubLogo, int numberOnline) {
        super(title, items);

        this.clubLogo = clubLogo;
        this.online = online;
        this.numberOnline = numberOnline;
    }


    public static final Comparator<UserChatGroup> DESCENDING_COMPARATOR = new Comparator<UserChatGroup>() {
        // Overriding the compare method to sort the age
        public int compare(UserChatGroup d, UserChatGroup d1) {
            return d.numberOnline - d1.numberOnline;
        }
    };
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

    public int getNumberOnline() {
        return numberOnline;
    }

    public void setNumberOnline(int numberOnline) {
        this.numberOnline = numberOnline;
    }
}
