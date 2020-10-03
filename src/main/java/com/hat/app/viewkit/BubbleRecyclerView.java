package com.hat.app.viewkit;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

public class BubbleRecyclerView extends RecyclerView implements PerformActionListener, View.OnTouchListener {
    private static final float SCALEDOWN = 0.85f;
    private static final long TIME = 300;
    private boolean performClick;
    private View currentSelectedView;
    private OnItemClickListener onItemClickListener;
    private DownCallback downCallback;
    private UpCallback upCallback;

    public BubbleRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        downCallback = new DownCallback();
        upCallback = new UpCallback(this);
    }


    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        child.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (currentSelectedView != null && currentSelectedView != view) {
            return false;
        }
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                currentSelectedView = view;
                performClick = false;
                scaleDown(view);
                break;
            case MotionEvent.ACTION_UP:
                performClick = true;
                scaleUp(view);
                break;
            case MotionEvent.ACTION_CANCEL:
                performClick = false;
                currentSelectedView = null;
                scaleUp(view);
                break;
        }
        return true;
    }

    private void scaleUp(View view) {
        ViewCompat.animate(view).cancel();
        ViewCompat.setScaleX(view, SCALEDOWN);
        ViewCompat.setScaleY(view, SCALEDOWN);
        ViewCompat.animate(view).scaleX(1).scaleY(1).setDuration(TIME).setInterpolator(new DecelerateInterpolator(3)).setListener(upCallback);
    }

    private void scaleDown(View view) {
        ViewCompat.animate(view).cancel();
        ViewCompat.setScaleX(view, 1);
        ViewCompat.setScaleY(view, 1);
        ViewCompat.animate(view).scaleX(SCALEDOWN).scaleY(SCALEDOWN).setDuration(TIME).setInterpolator(new DecelerateInterpolator(3)).setListener(downCallback);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onPerformAction(View view) {
        int index = getChildLayoutPosition(currentSelectedView);
        if(index!=-1){
            onItemClickListener.onItemClick(view, index);
            currentSelectedView = null;
        }

    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private static final class UpCallback extends DownCallback {

        private PerformActionListener performActionListener;

        public UpCallback(PerformActionListener performActionListener) {
            this.performActionListener = performActionListener;
        }

        @Override
        public void onAnimationEnd(View view) {
            super.onAnimationEnd(view);
            performActionListener.onPerformAction(view);
        }
    }

}
