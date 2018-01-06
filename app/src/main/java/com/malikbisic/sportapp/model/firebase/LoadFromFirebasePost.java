package com.malikbisic.sportapp.model.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by korisnik on 20/08/2017.
 */

public class LoadFromFirebasePost {


    DatabaseReference postingDatabase = FirebaseDatabase.getInstance().getReference().child("Posting");

}
