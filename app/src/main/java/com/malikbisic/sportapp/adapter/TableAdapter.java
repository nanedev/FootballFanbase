package com.malikbisic.sportapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.model.TableModel;
import com.malikbisic.sportapp.viewHolder.TableViewHolder;

import java.util.ArrayList;

/**
 * Created by Nane on 20.10.2017.
 */

public class TableAdapter extends RecyclerView.Adapter<TableViewHolder> {

    private ArrayList<TableModel> tableList;

    public TableAdapter(ArrayList<TableModel> tableList) {
        this.tableList = tableList;
    }


    @Override
    public TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View table_card = LayoutInflater.from(parent.getContext()).inflate(R.layout.league_table_row,parent,false);


        return new TableViewHolder(table_card);
    }

    @Override
    public void onBindViewHolder(TableViewHolder holder, int position) {
TableModel model = tableList.get(position);
holder.updateUI(model);
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }
}
