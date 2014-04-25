package me.linkcube.app.ui.main.multi;

import java.lang.reflect.Field;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import me.linkcube.app.R;
import me.linkcube.app.core.user.UserManager;
import me.linkcube.app.ui.BaseFragment;
import me.linkcube.app.ui.bluetooth.BluetoothSettingActivity;
import me.linkcube.app.ui.main.multi.MultiStatusBarView.OnMultiStatusBarClickListener;
import me.linkcube.app.ui.setting.SettingActivity;
import me.linkcube.app.ui.user.UserInfoActivity;
import me.linkcube.app.widget.CirclePageIndicator;

public class MultiPlayerFragment extends BaseFragment implements
		OnMultiStatusBarClickListener {

	private MultiStatusBarView mStatusBarView;

	private ViewPager mViewPager;

	private MultiPlayerPagerAdapter mAdapter;

	private CirclePageIndicator indicator;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.multi_player_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mStatusBarView = (MultiStatusBarView) view
				.findViewById(R.id.status_bar);
		mViewPager = (ViewPager) view.findViewById(R.id.chat_vp);
		indicator = (CirclePageIndicator) view.findViewById(R.id.vp_indicator);
		mAdapter = new MultiPlayerPagerAdapter(getChildFragmentManager());
		mViewPager.setAdapter(mAdapter);
		indicator.setViewPager(mViewPager);
		mStatusBarView.setMultiStatusBarClickListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		initData();
	}

	private void initData() {
		mStatusBarView.setUserInfo(UserManager.getInstance().getUserInfo());
		mStatusBarView.setTargetInfo(UserManager.getInstance()
				.getPlayingTarget());
	}

	@Override
	public void showUserInfoActivity() {
		startActivity(new Intent(mActivity, UserInfoActivity.class));
	}

	@Override
	public void showTargetInfoActivity() {
		// TODO Auto-generated method stub
	}

	@Override
	public void showSettingActivity() {
		startActivityForResult(new Intent(mActivity, SettingActivity.class),0);
	}

	@Override
	public void showBluetoothSettingActivity() {
		startActivity(new Intent(mActivity, BluetoothSettingActivity.class));
	}

	@Override
	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e1) {
			throw new RuntimeException(e1);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
