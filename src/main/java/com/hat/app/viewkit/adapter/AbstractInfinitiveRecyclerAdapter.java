package com.hat.app.viewkit.adapter;

import android.support.v7.widget.RecyclerView;

/**
 * Created by tuannx on 10/28/2016.
 */
public abstract class AbstractInfinitiveRecyclerAdapter<T> extends RecyclerView.Adapter {


    public static final int HALF_MAX_VALUE = Integer.MAX_VALUE / 2;
    public int MIDDLE;

    public AbstractInfinitiveRecyclerAdapter() {
        MIDDLE = HALF_MAX_VALUE ;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int relativePosition = position - MIDDLE;
        onBindView(holder,relativePosition);
    }

    public T getValue(int position){
        int relativePosition = position - MIDDLE;
        return getValueFor(relativePosition);
    }

    protected abstract T getValueFor(int relativePosition);

    protected abstract void onBindView(RecyclerView.ViewHolder holder, int relativePosition);


    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }
}
