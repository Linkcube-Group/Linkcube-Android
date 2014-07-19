package me.linkcube.app.ui.main.single;

import java.util.ArrayList;
import java.util.List;

import me.linkcube.app.R;
import me.linkcube.app.util.PreferenceUtils;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 个人设置页面装有三种模式的PagerAdapter
 * 
 * @author orange
 * 
 */
public class ModeViewBgSelectedPagerAdapter extends PagerAdapter {

	private List<View> views = new ArrayList<View>();

	private Context mContext;

	public ModeViewBgSelectedPagerAdapter(Context context) {
		mContext = context;
		initPager();
	}

	private void initPager() {
		views.add(getModeTipBg(R.drawable.shake_mode_bg,
				R.drawable.shake_mode_tip));
		views.add(getModeTipBg(R.drawable.voice_mode_bg,
				R.drawable.voice_mode_tip));
		//TODO 语音模式
		views.add(getModeTipBg(R.drawable.voice_mode_bg,
				R.drawable.voice_mode_tip));
		views.add(getModeTipBg(R.drawable.sex_position_mode_bg,
				R.drawable.sex_position_mode_tip));
	}

	private View getModeTipBg(int resId, int tipId) {
		LayoutInflater mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = mInflater.inflate(R.layout.mode_tip_bg, null);
		ImageView tipView = (ImageView) view.findViewById(R.id.mode_tip_iv);
		tipView.setImageResource(tipId);
		ImageView bg = (ImageView) view.findViewById(R.id.mode_bg_vp);
		bg.setImageResource(resId);
		return view;
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

	private int[] modeTipPic = { R.drawable.shake_mode_tip,
			R.drawable.voice_mode_tip,R.drawable.voice_mode_tip, R.drawable.sex_position_mode_tip };
	private int[] modeTipPicEn = { R.drawable.shake_mode_tip_en,
			R.drawable.voice_mode_tip_en,R.drawable.voice_mode_tip_en, R.drawable.sex_position_mode_tip_en };

	public void setLanguage() {
		System.out.println("setLanguage");
		switch (PreferenceUtils.getInt("app_language", 0)) {
		case 0:
			for (int i = 0; i < views.size(); i++) {
				ImageView tipView = (ImageView) views.get(i).findViewById(
						R.id.mode_tip_iv);
				tipView.setImageResource(modeTipPic[i]);
			}
			break;

		case 1:
			for (int i = 0; i < views.size(); i++) {
				ImageView tipView = (ImageView) views.get(i).findViewById(
						R.id.mode_tip_iv);
				tipView.setImageResource(modeTipPicEn[i]);
			}
			break;

		default:
			break;
		}
	}
}
