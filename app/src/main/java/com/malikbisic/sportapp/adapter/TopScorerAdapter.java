package com.malikbisic.sportapp.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.TopScorerModel;
import com.malikbisic.sportapp.viewHolder.TopScorerViewHolder;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by korisnik on 02/01/2018.
 */

public class TopScorerAdapter extends RecyclerView.Adapter<TopScorerViewHolder> {

    ArrayList<TopScorerModel> topScorerModelArrayList;
    Activity activity;
    int myPointsVote = 50;

    public TopScorerAdapter(ArrayList<TopScorerModel> topScorerModelArrayList, Activity activity) {
        this.topScorerModelArrayList = topScorerModelArrayList;
        this.activity = activity;
    }

    @Override
    public TopScorerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_scores_item, parent, false);

        return new TopScorerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TopScorerViewHolder holder, int position) {
        final TopScorerModel model = topScorerModelArrayList.get(position);
        holder.updateUI(model);

        holder.vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                votePlayer(model);
            }
        });

    }

    public void votePlayer(TopScorerModel model){

        AlertDialog.Builder playerVoteDialogBuilder = new AlertDialog.Builder(activity);
        View viewDialog = LayoutInflater.from(activity).inflate(R.layout.vote_player_dialog, null);
        playerVoteDialogBuilder.setView(viewDialog);

        TextView playerPoints = (TextView) viewDialog.findViewById(R.id.playerPointsVote);
        CircleImageView playerImage = (CircleImageView) viewDialog.findViewById(R.id.playerImageVote);
        TextView playerName = (TextView) viewDialog.findViewById(R.id.playerNameVote);
        final TextView myPoints = (TextView) viewDialog.findViewById(R.id.myPointsVote);
        final EditText enterPointsVote = (EditText) viewDialog.findViewById(R.id.enterPointsVote);

        playerPoints.setText("Player points: 50");
        Glide.with(playerImage.getContext()).load(model.getImagePlayer()).into(playerImage);
        playerName.setText(model.getName());
        myPoints.setText("My points" + myPointsVote);

        playerVoteDialogBuilder.setPositiveButton("Vote", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enterPoint = enterPointsVote.getText().toString().trim();
                int points = Integer.parseInt(enterPoint);


                myPointsVote = myPointsVote - points;
            }
        })
                .setNegativeButton("Cancel", null);

        playerVoteDialogBuilder.create();
        playerVoteDialogBuilder.show();

    }

    @Override
    public int getItemCount() {
        return topScorerModelArrayList.size();
    }
}
