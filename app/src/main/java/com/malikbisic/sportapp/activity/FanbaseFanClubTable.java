package com.malikbisic.sportapp.activity;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryListenOptions;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.adapter.FanbaseFanClubAdapter;
import com.malikbisic.sportapp.model.ClubTable;
import com.malikbisic.sportapp.model.LikesUsernamePhoto;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FanbaseFanClubTable extends AppCompatActivity {

    Toolbar likeToolbar;
    Intent myIntent;
    String openActivity = "";
    String myUid;
    Handler handler;

    int pos = 0;
    RecyclerView recFanClub;
    FirebaseFirestore db;
    com.google.firebase.firestore.Query clubReference;
    FanbaseFanClubAdapter adapter;
    List<ClubTable> listClub;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fanbase_fan_club_table);

        likeToolbar = (Toolbar) findViewById(R.id.clubTable_toolbar);
        setSupportActionBar(likeToolbar);
        getSupportActionBar().setTitle("Club Table");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        handler = new Handler();

        myIntent = getIntent();
        openActivity = myIntent.getStringExtra("activityToBack");
        myUid = myIntent.getStringExtra("uid");
        recFanClub = (RecyclerView) findViewById(R.id.fanBaseTableClub_recView);
        recFanClub.setLayoutManager(new LinearLayoutManager(this));
        recFanClub.setHasFixedSize(false);
        db = FirebaseFirestore.getInstance();
        listClub = new ArrayList<>();
        adapter = new FanbaseFanClubAdapter(listClub, this, this);

        recFanClub.setLayoutManager(new LinearLayoutManager(this));
        recFanClub.setAdapter(adapter);
        clubReference = db.collection("ClubTable");


        final com.google.firebase.firestore.Query queryClub = clubReference.orderBy("numberClubFan", com.google.firebase.firestore.Query.Direction.DESCENDING);
        queryClub.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot snapshot : task.getResult().getDocuments()){
                    if (snapshot.exists()){
                        ClubTable model = snapshot.toObject(ClubTable.class);
                        listClub.add(model);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();


        /*FirestoreRecyclerOptions<ClubTable> response = new FirestoreRecyclerOptions.Builder<ClubTable>()
                .setQuery(queryClub,ClubTable.class)
                .build();

        final FirestoreRecyclerAdapter<ClubTable, ClubTableViewHolder> populateRecView = new FirestoreRecyclerAdapter<ClubTable, ClubTableViewHolder>(response) {
            @Override
            protected void onBindViewHolder(ClubTableViewHolder viewHolder, int position, ClubTable model) {

                pos++;
                viewHolder.positionClub.setText("" +pos);
                viewHolder.clubName.setText(model.getClubName());
                Picasso.with(FanbaseFanClubTable.this).load(model.getClubLogo()).into(viewHolder.clubLogo);
                viewHolder.numberFans.setText("" + model.getNumberClubFan());

            }

            @Override
            public ClubTableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fanclabfans_row, parent, false);
                return new ClubTableViewHolder(view);
            }
        }
        populateRecView.notifyDataSetChanged();
        recFanClub.setAdapter(populateRecView);
        populateRecView.startListening();

*/



    }

    public static class ClubTableViewHolder extends RecyclerView.ViewHolder{

        public TextView positionClub;
        public ImageView clubLogo;
        public TextView clubName;
        public TextView numberFans;

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
