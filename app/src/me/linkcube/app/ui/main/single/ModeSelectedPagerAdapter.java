package me.linkcube.app.ui.main.single;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * 个人设置页面装有三种模式的PagerAdapter
 * 
 * @author orange
 * 
 */
public class ModeSelectedPagerAdapter extends PagerAdapter {

	private List<View> views = new ArrayList<View>();

	private Context mContext;

	private ModeSelectedListener mListener;
	
	private ShakeModeView shakeView;
	
	private VoiceModeView voiceView;
	
	private SexPositionModeView sexPositionView;

	public ModeSelectedPagerAdapter(Context context,
			ModeSelectedListener listener) {
		mContext = context;
		mListener = listener;
		initPager();
	}

	private void initPager() {
		shakeView = new ShakeModeView(mContext);
		views.add(shakeView);
		shakeView.setOnModeSelectedListener(mListener);
		voiceView = new VoiceModeView(mContext);
		views.add(voiceView);
		voiceView.setOnModeSelectedListener(mListener);
		sexPositionView = new SexPositionModeView(mContext);
		views.add(sexPositionView);
		sexPositionView.setOnModeSelectedListener(mListener);
	}

	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(views.get(position));
		return views.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(views.get(position));
	}

	public void unRegisterReceiver(){
		shakeView.unRegisterReceiver(mContext);
		voiceView.unRegisterReceiver(mContext);
		sexPositionView.unRegisterReceiver(mContext);
	}
}
