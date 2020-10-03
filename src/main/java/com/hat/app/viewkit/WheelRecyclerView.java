package com.hat.app.viewkit;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by tuannx on 10/28/2016.
 */
public class WheelRecyclerView extends RecyclerView {
    private final LinearSnapHelper snapHelper;
    private float minAlpha = 0.7f;
    private float range = 1 - minAlpha;
    private float maxScale = 1.5f;
    private float rangScale = maxScale - 1;
    OnSelectionChange onSelectionChange;
    public WheelRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(this);
    }

    public void setOnSelectionChange(OnSelectionChange onSelectionChange) {
        this.onSelectionChange = onSelectionChange;
    }

    @Override
    public void setRecycledViewPool(RecycledViewPool pool) {
        super.setRecycledViewPool(pool);
    }

    public int getSnappedPosition() {
        View view = snapHelper.findSnapView(getLayoutManager());
        if (view != null) {
            return getLayoutManager().getPosition(view);
        }
        return -1;
    }

    public void setSelectedItem(int dayPos) {
        int pos = dayPos - getChildCount() / 2 + 1;
        ((LinearLayoutManager)getLayoutManager()).scrollToPositionWithOffset(pos,0);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        int size = getChildCount();
        for (int i = 0; i < size; i++) {
            View child = getChildAt(i);
            int[] distance = snapHelper.calculateDistanceToFinalSnap(getLayoutManager(), child);
            int distanceY = Math.abs(distance[1]);
            offsetChild(child, distanceY);
        }
        View view = snapHelper.findSnapView(getLayoutManager());
        if (view != null) {
            int selected =  getLayoutManager().getPosition(view);
            if(onSelectionChange!=null){
                onSelectionChange.onSelectionChange(view,selected);
            }
        }
    }

    private void offsetChild(View child, int distanceY) {
        float alpha = 1;
        float scale = 1;
        if (distanceY <= child.getHeight()) {
            float offset = (1 - ((distanceY) / (float) child.getHeight()));
            alpha = (minAlpha + offset * range);
            scale = (1 + offset * rangScale);
        } else {
            alpha = minAlpha;
        }
        child.setAlpha(alpha);
        child.setPivotX(0.5f * child.getWidth());
        child.setPivotY(0.5f * child.getHeight());
        child.setScaleX(scale);
        child.setScaleY(scale);
    }

    @Override
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }

    public interface OnSelectionChange{
        void onSelectionChange(View view, int index);
    }
}
