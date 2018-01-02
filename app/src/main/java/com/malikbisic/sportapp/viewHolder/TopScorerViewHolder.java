package com.malikbisic.sportapp.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.TopScorerModel;

import javax.microedition.khronos.opengles.GL;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by korisnik on 02/01/2018.
 */

public class TopScorerViewHolder extends RecyclerView.ViewHolder {

    TextView positionText;
    TextView namePlayer;
    TextView goalScoredText;
    CircleImageView imagePlayer;

    public TopScorerViewHolder(View itemView) {
        super(itemView);

        positionText = (TextView) itemView.findViewById(R.id.positinScore);
        namePlayer = (TextView) itemView.findViewById(R.id.name_player_score);
        goalScoredText = (TextView) itemView.findViewById(R.id.scoreNumber);
        imagePlayer = (CircleImageView) itemView.findViewById(R.id.image_player_score);
    }

    public void updateUI(TopScorerModel model){
        String name = model.getName();
        String position = String.valueOf(model.getPosition());
        String goal = String.valueOf(model.getGoal());
        String image = model.getImagePlayer();

        positionText.setText(position);
        namePlayer.setText(name);
        goalScoredText.setText(goal);

        Glide.with(imagePlayer.getContext()).load(image).into(imagePlayer);
    }
}
