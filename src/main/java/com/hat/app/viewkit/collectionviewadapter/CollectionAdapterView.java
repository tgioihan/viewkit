package com.hat.app.viewkit.collectionviewadapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tuannx on 6/2/2016.
 */
public class CollectionAdapterView extends ViewGroup implements View.OnClickListener {

    private static final int MAX_ITEM_ROW = 5;
    private Recycler caches;
    protected Adapter adapter;
    protected AnimatorProvider animatorProvider;
    private boolean registerObserver;
    OnPageSelectionListener onPageSelectionListener;
    protected int maxItemInRow;
    protected int childSpacing;
    private DataSetObserver dataObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            clearAdapter();
            bindData();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            clearAdapter();
            bindData();
        }
    };
    protected boolean needValidateAnimation;
    private View currentSelectedView;
    private int currentItem;

    public void setOnPageSelectionListener(OnPageSelectionListener onPageSelectionListener) {
        this.onPageSelectionListener = onPageSelectionListener;
    }

    public void setChildSpacing(int childSpacing) {
        this.childSpacing = childSpacing;
    }

    public void setMaxItemInRow(int maxItemInRow) {
        this.maxItemInRow = maxItemInRow;
    }

    public CollectionAdapterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    protected void init(Context context) {
        caches = new Recycler();
        maxItemInRow = MAX_ITEM_ROW;
    }

    public void setAdapter(Adapter adapter) {
        if (this.adapter == adapter) {
            return;
        }
        if (this.adapter != null) {
            clearAdapter();
            caches.clear();
            this.adapter.unregisterDataSetObserver(dataObserver);
            registerObserver = false;
        }

        this.adapter = adapter;
        this.adapter.registerDataSetObserver(dataObserver);
        registerObserver = true;
        bindData();
    }

    private void clearAdapter() {
        int size = getChildCount();
        int index = 0;
        for (int i = 0; i < size; i++) {
            View child = getChildAt(i);
            caches.put(child, this.adapter.getItemViewType(index));

            removeView(child);
            index++;
            i--;
            size--;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (adapter != null && registerObserver) {
            adapter.unregisterDataSetObserver(dataObserver);
            registerObserver = false;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (adapter != null && !registerObserver) {
            adapter.registerDataSetObserver(dataObserver);
            registerObserver = true;
        }
    }

    public int getMaxItemInRow() {
        return maxItemInRow;
    }

    public void bindData() {
        int index = 0;
        needValidateAnimation = true;
        boolean viewSelected = false;
        while (index < this.adapter.getCount()) {
            View viewItem;
            viewItem = this.adapter.getView(index, caches.get(adapter.getItemViewType(index)), this);
            viewItem.setOnClickListener(this);
            if (viewItem != null) {
                viewSelected = index == currentItem;
                viewItem.setSelected(viewSelected);
                if(viewSelected){
                    currentSelectedView = viewItem;
                }
                addView(viewItem);
                index++;
            } else {
                break;
            }
        }

        setCurrentItemInternal(currentItem);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (adapter == null || adapter.getCount() == 0) {
            setMeasuredDimension(0, 0);
            return;
        }
        int width = getDefaultSize(0, widthMeasureSpec);
        int size = getChildCount();
        LayoutParams params = getLayoutParams();
        boolean full = false;
        if (params != null) {
            full = params.height == LayoutParams.MATCH_PARENT;
        }
        int viewHeight = childSpacing;
        int childWidth = (width - 2 * childSpacing - (maxItemInRow - 1) * childSpacing) / maxItemInRow;
        childWidth = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
        int itemInRow = 0;
        int maxItemHeightInRow = 0;
        for (int i = 0; i < size; i++) {
            View child = getChildAt(i);
            child.measure(childWidth, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            itemInRow++;
            maxItemHeightInRow = Math.max(maxItemHeightInRow, child.getMeasuredHeight());
            if (itemInRow >= maxItemInRow) {
                viewHeight += maxItemHeightInRow + childSpacing;
                itemInRow = 0;
                maxItemHeightInRow = 0;
            } else {

                if (i == size - 1) {
                    viewHeight += maxItemHeightInRow + childSpacing;
                }
            }

        }
        setMeasuredDimension(width, full ? getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec) : viewHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int size = getChildCount();
        int childTop = childSpacing;
        int childLeft = childSpacing;
        int itemInRow = 1;
        for (int i = 0; i < size; i++) {
            View child = getChildAt(i);
//            if(child.getVisibility() == VISIBLE){
            child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(), childTop + child.getMeasuredHeight());
            itemInRow++;
            if (itemInRow > maxItemInRow) {
                childTop += child.getMeasuredHeight() + childSpacing;
                itemInRow = 1;
                childLeft = childSpacing;
            } else {
                childLeft += child.getMeasuredWidth() + childSpacing;
            }
            if (animatorProvider != null && needValidateAnimation)
                animatorProvider.getAnimator(child, i).start();
//            }
        }
        needValidateAnimation = false;
    }

    public void setAnimatorProvider(AnimatorProvider animatorProvider) {
        this.animatorProvider = animatorProvider;
    }

    public void notifyDataChanged() {
        if (!registerObserver && adapter != null) {
            adapter.registerDataSetObserver(dataObserver);
            registerObserver = true;
        }
        dataObserver.onChanged();
    }

    public int getIndexView(View child) {
        int size = getChildCount();
        for (int i = 0; i < size; i++) {
            if (getChildAt(i) == child) {
                return i;
            }
        }
        return -1;
    }

    public void setCurrentItem(int currentItem) {
        if (this.currentItem != currentItem && currentItem < getChildCount()) {
            setCurrentItemInternal(currentItem);
        }
    }

    private void setCurrentItemInternal(int currentItem) {
        View selectedView = setItemSelected(currentItem);
        if(onPageSelectionListener!=null){
            onPageSelectionListener.onPageSelected(selectedView, currentItem);
        }

    }

    public View setItemSelected(int currentItem) {
        if (currentSelectedView != null) {
            currentSelectedView.setSelected(false);
        }
        this.currentItem = currentItem;
        View selectedView = getChildAt(currentItem);
        if(selectedView==null){
            return null;
        }
        selectedView.setSelected(true);
        currentSelectedView = selectedView;
        return selectedView;
    }

    @Override
    public void onClick(View view) {
        int index = getIndexView(view);
        setCurrentItem(index);
    }

    public class Recycler {

        private SparseArray<List<View>> caches;

        public Recycler() {
            this.caches = new SparseArray<>();
        }

        public void put(View view, int viewType) {
            List<View> views = caches.get(viewType);
            if (views == null) {
                views = new ArrayList<>();
                views.add(view);
                caches.put(viewType, views);
            } else {
                views.add(view);
            }
        }

        public View get(int viewType) {
            List<View> views = caches.get(viewType);
            if (views == null || views.size() == 0) {
                return null;
            }
            return views.remove(0);
        }

        public void clear() {
            caches.clear();
        }
    }

    public interface OnPageSelectionListener {
        void onPageSelected(View view, int index);
    }

}