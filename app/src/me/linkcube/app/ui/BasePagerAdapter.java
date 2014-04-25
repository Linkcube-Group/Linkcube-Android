package me.linkcube.app.ui;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class BasePagerAdapter extends PagerAdapter {

	protected List<View> mViewList;

	protected Context mContext;

	public BasePagerAdapter(Context context, List<View> viewList) {
		mViewList = viewList;
		mContext = context;
	}

	public BasePagerAdapter(Context context) {
		mViewList = Collections.emptyList();
		mContext = context;
	}

	@Override
	public int getCount() {
		return mViewList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mViewList.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(mViewList.get(position), 0);
		return mViewList.get(position);
	}
}
