package com.hat.app.viewkit;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by tuannx on 10/22/2016.
 */
public class SelectedRecyclerView extends RecyclerView
        implements View.OnClickListener
{

    private OnItemClickListener onItemClickListener;
    private OnSelectionChange onSelectionChange;
    private View selectedView;
    private int selectedPosition = -1;

    public SelectedRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        int indexPos = getLayoutManager().getPosition(child);
        if(indexPos == selectedPosition){
            onSelectionChange(child,indexPos);
        }
        child.setOnClickListener(this);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnSelectionChange(OnSelectionChange onSelectionChange) {
        this.onSelectionChange = onSelectionChange;
    }

    public void setSelectedPosition(int selectedPosition) {
        if (getChildCount() == 0) {
            this.selectedPosition = selectedPosition;
        } else {
            View view = getLayoutManager().getChildAt(selectedPosition);
            if (view != null) {
                onSelectionChange(view, selectedPosition);
            }

        }

    }
//
    @Override
    public void onClick(View view) {
        int index = getLayoutManager().getPosition(view);
        onSelectionChange(view, index);
        if(onItemClickListener!=null)
        onItemClickListener.onItemClick(view, index);
    }

    private void onSelectionChange(View view, int index) {
        if(selectedView == null  || selectedPosition != index){
            if (selectedView != null) {
                selectedView.setSelected(false);
            }
            this.selectedView = view;
            selectedView.setSelected(true);
            selectedPosition = index;
            if(onSelectionChange!=null){
                onSelectionChange.onSelectionChange(view, index);
            }
        }
    }

    public void clearSelection() {
        if (selectedView != null) {
            selectedView.setSelected(false);
            selectedView = null;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnSelectionChange {
        void onSelectionChange(View view, int index);
    }
}
