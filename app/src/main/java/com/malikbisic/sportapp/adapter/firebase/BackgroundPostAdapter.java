package com.malikbisic.sportapp.adapter.firebase;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.firebase.OnlyPostActivity;

/**
 * Created by Nane on 20.3.2018.
 */

public class BackgroundPostAdapter extends RecyclerView.Adapter<BackgroundPostAdapter.BackgroundViewHolder>{
    private int[] images;
    Context context;

    public BackgroundPostAdapter(Context context,int[]images) {
        this.images = images;
        this.context = context;
    }

    @Override
    public BackgroundViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.background_item,parent,false);



        return new BackgroundViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BackgroundViewHolder holder, final int position) {
holder.backgroudImage.setImageResource(images[position]);
final int id = images[position];
holder.backgroudImage.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if (position == 0) {
            Toast.makeText(context,"Prva poz",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, OnlyPostActivity.class);
            intent.putExtra("defaultBackground", "default");
            context.startActivity(intent);

        } else {

            Intent intent = new Intent(context, OnlyPostActivity.class);
            intent.putExtra("imageRes", id);
            context.startActivity(intent);
        }
    }
});
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public class BackgroundViewHolder extends RecyclerView.ViewHolder{
ImageView backgroudImage;
        public BackgroundViewHolder(View itemView) {
            super(itemView);

            backgroudImage = (ImageView) itemView.findViewById(R.id.backgroundimage);
        }
    }
}
