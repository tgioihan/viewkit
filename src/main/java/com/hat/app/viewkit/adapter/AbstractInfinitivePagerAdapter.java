package com.hat.app.viewkit.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public abstract class AbstractInfinitivePagerAdapter extends PagerAdapter {

	int mLoopValue;
	public static final int COUNT = 10000;
	public static final int HALF_MAX_VALUE = COUNT / 2;
	public int MIDDLE;

	public AbstractInfinitivePagerAdapter( int mLoopValue) {
		this.mLoopValue = mLoopValue;
		MIDDLE = HALF_MAX_VALUE - HALF_MAX_VALUE % mLoopValue;
	}

	@Override
	public int getCount() {
		return COUNT;
	}

	public int getDivFromMiddle(int position) {
		return position - MIDDLE;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = getItemView(getDivFromMiddle(position),container);
		container.addView(view);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	protected abstract View getItemView(int postion, ViewGroup container);

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

}
