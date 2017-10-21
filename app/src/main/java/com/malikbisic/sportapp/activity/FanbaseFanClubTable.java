package com.malikbisic.sportapp.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.ClubTable;
import com.squareup.picasso.Picasso;

public class FanbaseFanClubTable extends AppCompatActivity {

    Toolbar likeToolbar;
    Intent myIntent;
    String openActivity = "";
    String myUid;

    RecyclerView recFanClub;
    DatabaseReference clubReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fanbase_fan_club_table);

        likeToolbar = (Toolbar) findViewById(R.id.clubTable_toolbar);
        setSupportActionBar(likeToolbar);
        getSupportActionBar().setTitle("Club Table");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myIntent = getIntent();
        openActivity = myIntent.getStringExtra("activityToBack");
        myUid = myIntent.getStringExtra("uid");
        recFanClub = (RecyclerView) findViewById(R.id.fanBaseTableClub_recView);
        recFanClub.setLayoutManager(new LinearLayoutManager(this));
        recFanClub.setHasFixedSize(false);



        clubReference = FirebaseDatabase.getInstance().getReference().child("ClubTable");



        Log.i("root", String.valueOf(clubReference.getRef()));


    }

    @Override
    protected void onStart() {
        super.onStart();

        final Query queryClub = clubReference.orderByChild("numbersFans");

        FirebaseRecyclerAdapter<ClubTable, ClubTableViewHolder> populateRecView = new FirebaseRecyclerAdapter<ClubTable, ClubTableViewHolder>(
                ClubTable.class,
                R.layout.fanclabfans_row,
                ClubTableViewHolder.class,
                queryClub) {

            @Override
            public ClubTable getItem(int position) {
                return super.getItem(getItemCount() - 1 - position);
            }
            @Override
            protected void populateViewHolder(ClubTableViewHolder viewHolder, ClubTable model, int position) {
                int pos = position + 1;
                viewHolder.positionClub.setText(""+pos);
                viewHolder.clubName.setText(model.getClubName());
                Picasso.with(FanbaseFanClubTable.this).load(model.getClubLogo()).into(viewHolder.clubLogo);
                viewHolder.numberFans.setText("" + model.getNumbersFans());


                Log.i("root", String.valueOf(getRef(position)));
            }
        };
        recFanClub.setAdapter(populateRecView);
        clubReference.onDisconnect();
    }

    public static class ClubTableViewHolder extends RecyclerView.ViewHolder{

        TextView positionClub;
        ImageView clubLogo;
        TextView clubName;
        TextView numberFans;

        public ClubTableViewHolder(View itemView) {
            super(itemView);
            positionClub = (TextView) itemView.findViewById(R.id.positionClub);
            clubLogo = (ImageView) itemView.findViewById(R.id.clubLogo);
            clubName = (TextView) itemView.findViewById(R.id. clubName);
            numberFans = (TextView) itemView.findViewById(R.id.fansNumber);

        }
    }


    @Nullable
    @Override
    public Intent getParentActivityIntent() {


        if (openActivity.equals("myProfile")){

            Bundle bundle = new Bundle();
            bundle.putString("myUid", myUid);
            bundle.putBoolean("openFromFanBaseTable", true);

            ProfileFragment profileFragment = new ProfileFragment();
            profileFragment.setArguments(bundle);

            FragmentTransaction manager = getSupportFragmentManager().beginTransaction();

            manager.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in,
                    R.anim.push_left_out, R.anim.push_left_out).replace(R.id.clubTable_layout, profileFragment, profileFragment.getTag()).addToBackStack(null).commit();
            Log.i("tacno", "true");

        } else if (openActivity.equals("commentsActivity")){
            Intent backComments = new Intent(FanbaseFanClubTable.this, CommentsActivity.class);
            //backComments.putExtra("keyComment", postKey);
            startActivity(backComments);
            finish();
        }

        return super.getParentActivityIntent();

    }
}
