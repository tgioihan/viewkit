package com.hat.app.viewkit.collectionviewadapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by tuannx on 6/17/2016.
 */
public class FixedCollectionAdapterView extends CollectionAdapterView {

    private int fixedSize;

    public void setFixedSize(int fixedSize) {
        this.fixedSize = fixedSize;
    }

    public FixedCollectionAdapterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (adapter == null || adapter.getCount() == 0) {
            setMeasuredDimension(0, 0);
            return;
        }
        int width = getDefaultSize(0, widthMeasureSpec);
        int size = getChildCount();
        int viewHeight = childSpacing;
        int childWidth = (width - 2 * childSpacing - (maxItemInRow - 1) * childSpacing) / maxItemInRow;
        childWidth = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
        int itemInRow = 0;
        int maxItemHeightInRow = 0;
        int i = 0;
        while (i < fixedSize) {
            if (i < size) {
                View child = getChildAt(i);
                child.measure(childWidth, heightMeasureSpec);
                maxItemHeightInRow = Math.max(maxItemHeightInRow, child.getMeasuredHeight());
            }
            itemInRow++;

            if (itemInRow >= maxItemInRow) {
                viewHeight += maxItemHeightInRow + childSpacing;
                itemInRow = 0;
                maxItemHeightInRow = 0;
            }
            i++;
        }
        setMeasuredDimension(width, viewHeight);
    }
}
