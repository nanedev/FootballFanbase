package com.malikbisic.sportapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.FanbaseFanClubTable;
import com.malikbisic.sportapp.activity.MyPostsActivity;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Nane on 26.9.2017.
 */

public class ProfileFragmentAdapter extends RecyclerView.Adapter<ProfileFragmentAdapter.ViewHolder> {


    private String[] titles = {"Posts", "Boost your team", "Fanbase club table", "Premium"};
    private int[] images = {R.drawable.posts, R.drawable.boost, R.drawable.boost, R.drawable.premium};

    int number;
    Activity activity;

    public ProfileFragmentAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_in_fragment_profile, parent, false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemTitle.setText(titles[position]);
        holder.itemImage.setImageResource(images[position]);

        if (position == 0){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, MyPostsActivity.class);
                    activity.startActivity(intent);
                }
            });

        } else if (position == 2){
            final String uid;
            FirebaseAuth mAuth;

            mAuth = FirebaseAuth.getInstance();
            uid = mAuth.getCurrentUser().getUid();

            holder.numberSomething.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, FanbaseFanClubTable.class);
                    intent.putExtra("activityToBack", "myProfile");
                    intent.putExtra("uid", uid);
                    activity.startActivity(intent);
                    activity.finish();
                }
            });

            holder.itemImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, FanbaseFanClubTable.class);
                    intent.putExtra("activityToBack", "myProfile");
                    intent.putExtra("uid", uid);
                    activity.startActivity(intent);
                    activity.finish();
                }
            });
        }

        if (position == 0){
            holder.numberPost();
        } else if (position == 1){
            holder.numberPointsForYourTeam();
        } else if (position == 2){
            holder.positionTeam();
        }else if (position == 3){
            holder.premiumTrialDateText();
        }
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemTitle;
        TextView numberSomething;
        FirebaseFirestore mDatabase;
        DocumentReference mReference;
        String uid;
        FirebaseAuth mAuth;

        DateFormat dateFormat;
        Calendar calendar;
        Date currentDateOfUser;
        String getDateFromDatabase;
        TextView premiumTrialDate;
        Date trialDate;
        String trialDateString;
        Date dateRightNow;



        public ViewHolder(View itemView) {
            super(itemView);



            mDatabase = FirebaseFirestore.getInstance();

            mAuth = FirebaseAuth.getInstance();
            uid = mAuth.getCurrentUser().getUid();
            mReference = mDatabase.collection("Users").document(uid);


            String clubLogo;

            itemImage = (ImageView) itemView.findViewById(R.id.card_image);
            itemTitle = (TextView) itemView.findViewById(R.id.card_text);
            numberSomething = (TextView) itemView.findViewById(R.id.number_of);
            calendar = Calendar.getInstance();

            dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dateRightNow = new Date();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Snackbar.make(v, "Clicked" + pos, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }



    public void numberPost() {
            final CollectionReference numberPostRef = FirebaseFirestore.getInstance().collection("Posting");

        final com.google.firebase.firestore.Query query = numberPostRef.whereEqualTo("uid",uid);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                number = documentSnapshots.size();
                numberSomething.setText(String.valueOf(number));
            }
        });

   /*numberPostRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
       @Override
       public void onComplete(@NonNull Task<QuerySnapshot> task) {
           DocumentReference ref = numberPostRef.getDo
      number = task.getResult().size();
      numberSomething.setText("" + number);
       }
   });*/
   /*     DatabaseReference numberPostRef = FirebaseDatabase.getInstance().getReference().child("Posting");
        Query numberPostQuery = numberPostRef.orderByChild("uid").equalTo(uid);
        numberPostQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                number  = (int) dataSnapshot.getChildrenCount();
                numberSomething.setText(""+number);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

    }
        public void numberPointsForYourTeam() {
            numberSomething.setText("23");
        }

        public void positionTeam() {
            numberSomething.setText("11.");
            mReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
               String logoClub = (String) documentSnapshot.getData().get("favoriteClubLogo");
                    Picasso.with(itemView.getContext()).load(logoClub).into(itemImage);
                }
            });

     /*       mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String logoClub = (String) dataSnapshot.child("favoriteClubLogo").getValue();
                    Picasso.with(itemView.getContext()).load(logoClub).into(itemImage);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/
        }

        public void premiumTrialDateText(){

        /*    mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    getDateFromDatabase = String.valueOf(dataSnapshot.child("premiumDate").getValue());
                    try {
                        currentDateOfUser = dateFormat.parse(getDateFromDatabase);
                        calendar.setTime(currentDateOfUser);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    calendar.add(Calendar.DAY_OF_MONTH, 15);
                    trialDate = calendar.getTime();
                    trialDateString = dateFormat.format(trialDate);
                    mReference.child("trialPremiumDate").setValue(trialDateString);

                    numberSomething.setText("Your premium trial will end on : " + trialDateString);


                    if (dateRightNow.equals(trialDate) || dateRightNow.after(trialDate)) {

                        numberSomething.setText("Your premium trial ended");
                        mReference.child("premium").setValue(false);


                        Log.i("proba", "isti su");
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/

        }
    }


}
