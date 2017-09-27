package com.malikbisic.sportapp.adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.malikbisic.sportapp.R;

import java.util.ArrayList;

/**
 * Created by Nane on 26.9.2017.
 */

public class ProfileFragmentAdapter extends RecyclerView.Adapter<ProfileFragmentAdapter.ViewHolder> {




    private String[] titles = {"Posts","Boost your team","Football Fanbase club table","Premium"};
    private int[] images = {R.drawable.boost,R.drawable.boost,R.drawable.boost,R.drawable.boost};

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_in_fragment_profile,parent,false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
holder.itemTitle.setText(titles[position]);
holder.itemImage.setImageResource(images[position]);




    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView itemImage;
        TextView itemTitle;


        public ViewHolder(View itemView) {
            super(itemView);


            itemImage = (ImageView) itemView.findViewById(R.id.card_image);
            itemTitle = (TextView) itemView.findViewById(R.id.card_text);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Snackbar.make(v,"Clicked" + pos,Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
            });
        }

    }
}
