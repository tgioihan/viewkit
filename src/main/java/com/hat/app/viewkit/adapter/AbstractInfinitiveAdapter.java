package com.hat.app.viewkit.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

public abstract class AbstractInfinitiveAdapter extends BaseAdapter {
	Context mContext;
	int mLoopValue;

	public static final int HALF_MAX_VALUE = Integer.MAX_VALUE / 2;
	public int MIDDLE;

	public AbstractInfinitiveAdapter(Context mContext, int mLoopValue) {
		this.mContext = mContext;
		this.mLoopValue = mLoopValue;
		MIDDLE = HALF_MAX_VALUE - HALF_MAX_VALUE % mLoopValue;
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}
	
	public int  getDivFromMiddle(int postion){
		return postion - MIDDLE;
	}

	@Override
	public Object getItem(int position) {
		return position % mLoopValue;
	}

	@Override
	public long getItemId(int position) {
		return position % mLoopValue;
	}
}
