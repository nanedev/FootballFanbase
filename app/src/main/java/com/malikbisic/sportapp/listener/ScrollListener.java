package com.malikbisic.sportapp.listener;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;

/**
 * Created by malikbisic on 10/03/2018.
 */

public abstract class ScrollListener extends RecyclerView.OnScrollListener {

    private int toolbarOffset = 0;
    private int toolbarHeight;

    int scrollDist = 0;
    boolean isVisible = true;
    static final float MINIMUM = 25;

    public ScrollListener(Context context) {
        int[] actionBarAttr = new int[] { android.R.attr.actionBarSize };
        TypedArray a = context.obtainStyledAttributes(actionBarAttr);
        toolbarHeight = (int) a.getDimension(0, 0) + 10;
        a.recycle();
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (isVisible && scrollDist > MINIMUM) {
            hide();
            scrollDist = 0;
            isVisible = false;
        }
        else if (!isVisible && scrollDist < -MINIMUM) {
            show();
            scrollDist = 0;
            isVisible = true;
        }

        if ((isVisible && dy > 0) || (!isVisible && dy < 0)) {
            scrollDist += dy;
        }
    }

    private void clipToolbarOffset() {
        if(toolbarOffset > toolbarHeight) {
            toolbarOffset = toolbarHeight;
        } else if(toolbarOffset < 0) {
            toolbarOffset = 0;
        }
    }

    public abstract void show();
    public abstract void hide();
}
