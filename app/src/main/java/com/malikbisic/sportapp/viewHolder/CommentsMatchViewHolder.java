package com.malikbisic.sportapp.viewHolder;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.CommentaryMatchModel;
import com.malikbisic.sportapp.model.CommentsInCommentsModel;

import java.awt.font.TextAttribute;

/**
 * Created by korisnik on 30/12/2017.
 */

public class CommentsMatchViewHolder extends RecyclerView.ViewHolder {

    TextView minuteText;
    TextView commentaryText;
    ImageView iconCommentary;

    public CommentsMatchViewHolder(View itemView) {
        super(itemView);

        minuteText = (TextView) itemView.findViewById(R.id.minuteText);
        commentaryText = (TextView) itemView.findViewById(R.id.commentsText);
        iconCommentary = (ImageView) itemView.findViewById(R.id.imageComment);
    }

    public void updateUI(CommentaryMatchModel model, Activity activity){
        String min = String.valueOf(model.getMinutes());
        String comments = model.getComments();

        String goal = "goal!";
        String substitution = "substitution";
        String yellow = "yellow card";
        String red = "red card";

        minuteText.setText(min);
        if (!comments.matches("[0-9]")) {
            commentaryText.setText(comments);
        }

        if (comments.toLowerCase().contains(goal.toLowerCase())){
            iconCommentary.setVisibility(View.VISIBLE);
            iconCommentary.setImageDrawable(activity.getResources().getDrawable(R.drawable.goalscored));
         } else if (comments.toLowerCase().contains(substitution.toLowerCase())){
            iconCommentary.setVisibility(View.VISIBLE);
            iconCommentary.setImageDrawable(activity.getResources().getDrawable(R.drawable.substitutionpng));

        } else if (comments.toLowerCase().contains(yellow.toLowerCase())){
            iconCommentary.setVisibility(View.VISIBLE);
            iconCommentary.setImageDrawable(activity.getResources().getDrawable(R.drawable.yellowcard));

        } else if (comments.toLowerCase().contains(red.toLowerCase())){
            iconCommentary.setVisibility(View.VISIBLE);
           iconCommentary.setImageDrawable(activity.getResources().getDrawable(R.drawable.redcard));

        } else {
            iconCommentary.setVisibility(View.INVISIBLE);
        }

    }
}
