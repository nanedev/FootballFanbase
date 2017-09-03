package com.malikbisic.sportapp.model;

import android.support.v7.widget.RecyclerView;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by korisnik on 03/09/2017.
 */

public class UserChatGroup extends ExpandableGroup {

    public UserChatGroup(String title, List items) {
        super(title, items);
    }
}
