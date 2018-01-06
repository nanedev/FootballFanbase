package com.malikbisic.sportapp.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Copy from https://gist.github.com/ssinss/e06f12ef66c51252563e
 */

public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = EndlessRecyclerViewScrollListener.class.getSimpleName();

    public static int previousTotal; // The total number of items in the dataset after the last load
    public static boolean loading; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
    int lastVisibleItem, visibleItemCount, totalItemCount;

    private int current_page = 1;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerViewScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();



        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
            // End has been reached

            // Do something
            current_page++;

            onLoadMore(current_page);

            loading = true;
        }

        Log.i("recPrevTotal", String.valueOf(previousTotal));
        Log.i("recTotal", String.valueOf(totalItemCount));
        Log.i("recLastVisble", String.valueOf(lastVisibleItem));
        Log.i("recisLoading", String.valueOf(loading));

    }

    public abstract void onLoadMore(int current_page);
}