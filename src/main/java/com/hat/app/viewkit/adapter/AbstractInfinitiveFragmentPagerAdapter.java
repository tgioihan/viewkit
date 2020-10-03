package com.hat.app.viewkit.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public abstract class AbstractInfinitiveFragmentPagerAdapter extends
		FragmentStatePagerAdapter {
	int mLoopValue;

	
	public static final int count = 2000;
	public static final int HALF_MAX_VALUE = count/ 2;
	public int MIDDLE;

	public AbstractInfinitiveFragmentPagerAdapter(FragmentManager fm,
			int mLoopValue) {
		super(fm);
		this.mLoopValue = mLoopValue;
		MIDDLE = HALF_MAX_VALUE - HALF_MAX_VALUE % mLoopValue;
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getCount() {
		return count;
	}

	public int getDivFromMiddle(int postion) {
		return postion - MIDDLE;
	}

}
