package com.malikbisic.sportapp.adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.MainPage;
import com.malikbisic.sportapp.activity.ProfileFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Nane on 26.9.2017.
 */

public class ProfileFragmentAdapter extends RecyclerView.Adapter<ProfileFragmentAdapter.ViewHolder> {


    private String[] titles = {"Posts", "Boost your team", "Fanbase club table", "Premium"};
    private int[] images = {R.drawable.posts, R.drawable.boost, R.drawable.boost, R.drawable.premium};

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
            holder.numberPost();
        } else if (position == 1){
            holder.numberPointsForYourTeam();
        } else if (position == 2){
            holder.positionTeam();
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
        FirebaseDatabase mDatabase;
        DatabaseReference mReference;
        String uid;
        FirebaseAuth mAuth;


        public ViewHolder(View itemView) {
            super(itemView);
            mDatabase = FirebaseDatabase.getInstance();

            mAuth = FirebaseAuth.getInstance();
            uid = mAuth.getCurrentUser().getUid();

            mReference = mDatabase.getReference().child("Users").child(uid);
            String clubLogo;

            itemImage = (ImageView) itemView.findViewById(R.id.card_image);
            itemTitle = (TextView) itemView.findViewById(R.id.card_text);
            numberSomething = (TextView) itemView.findViewById(R.id.number_of);


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
            numberSomething.setText("3443");


        }

        public void numberPointsForYourTeam() {
            numberSomething.setText("23");

        }

        public void positionTeam() {
            numberSomething.setText("11.");
            mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String logoClub = (String) dataSnapshot.child("favoriteClubLogo").getValue();
                    Picasso.with(itemView.getContext()).load(logoClub).into(itemImage);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


}
