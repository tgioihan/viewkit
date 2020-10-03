package com.hat.app.viewkit;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpaceItemDecorator  extends RecyclerView.ItemDecoration {

    private final int spacing;

    public SpaceItemDecorator(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.bottom = spacing;
        outRect.left = spacing;
        outRect.right = spacing;
        outRect.top = spacing;
    }
}