package me.linkcube.app.ui.main.single;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import me.linkcube.app.LinkcubeApplication;
import me.linkcube.app.R;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.toy.ShakeSensor;
import me.linkcube.app.core.toy.VoiceSensor;
import me.linkcube.app.core.user.UserManager;
import me.linkcube.app.ui.BaseFragment;
import me.linkcube.app.ui.bluetooth.BluetoothSettingActivity;
import me.linkcube.app.ui.main.SensorProvider;
import me.linkcube.app.ui.setting.SettingActivity;
import me.linkcube.app.ui.user.LoginActivity;
import me.linkcube.app.ui.user.UserInfoActivity;
import me.linkcube.app.widget.AlertUtils;
import me.linkcube.app.widget.CirclePageIndicator;
import static me.linkcube.app.core.toy.ToyConst.*;

/**
 * 单人模式页面
 * 
 * @author orange
 * 
 */
public class SinglePalyerFragment extends BaseFragment implements
		ModeSelectedListener, OnSingleStatusBarClickListener {

	private ViewPager mViewPager;

	private ViewPager mBgViewPager;

	private SingleStatusBarView statusBarView;

	private ModeSelectedPagerAdapter mAdapter;

	private ModeViewBgSelectedPagerAdapter mBgPagerAdapter;

	private CirclePageIndicator indicator;

	private ShakeSensor mShakeSensor;

	private VoiceSensor mVoiceSensor;

	private SensorProvider sensorProvider;

	private int mStateMode = STATE_NONE;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			sensorProvider = (SensorProvider) activity;
		} catch (Exception e) {
			throw new ClassCastException(activity.toString()
					+ " must implement SensorProvider");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.single_player_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		statusBarView = (SingleStatusBarView) view
				.findViewById(R.id.status_bar);
		statusBarView.setOnSingleStatusBarClickListener(this);
		mViewPager = (ViewPager) view.findViewById(R.id.mode_vp);
		mBgViewPager = (ViewPager) view.findViewById(R.id.mode_bg_vp);
		mAdapter = new ModeSelectedPagerAdapter(mActivity, this);
		indicator = (CirclePageIndicator) view.findViewById(R.id.vp_indicator);
		mViewPager.setAdapter(mAdapter);
		indicator.setViewPager(mViewPager);
		indicator.setOnPageChangeListener(onPageChangeListener);
		mBgPagerAdapter = new ModeViewBgSelectedPagerAdapter(mActivity);
		mBgViewPager.setAdapter(mBgPagerAdapter);
	}
	
	

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Timber.d("SinglePalyerFragment--unRegisterReceiver");
		//TODO 注销广播
		mAdapter.unRegisterReceiver();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (UserManager.getInstance().isAuthenticated()
				&& UserManager.getInstance().getUserInfo() != null) {
			Timber.i("nickname:"
					+ UserManager.getInstance().getUserInfo().getNickName());
			statusBarView.setUserInfo(UserManager.getInstance().getUserInfo());
		} else {
			statusBarView.setUserInfo(null);
		}
		if (LinkcubeApplication.toyServiceCall != null) {
			try {
				statusBarView
						.setBluetoothState(LinkcubeApplication.toyServiceCall
								.isToyConnected());
			} catch (RemoteException e) {
				statusBarView.setBluetoothState(false);
				e.printStackTrace();
			}
		} else {
			statusBarView.setBluetoothState(false);
		}
	}

	private void registerShakeSensor() {
		Timber.d("注册传感器");
		mShakeSensor = sensorProvider.getShakeSensor();
		mShakeSensor.registerListener();
	}

	private void unregisterShakeSensor() {
		Timber.d("注销传感器");
		if (mShakeSensor != null) {
			mShakeSensor.unRegisterListener();
		}
	}

	private void registerVoiceSensor() {
		Timber.d("注册声音传感器");
		mVoiceSensor = sensorProvider.getVoiceSensor();
		mVoiceSensor.registerVoiceListener();
	}

	private void unregisterVoiceSensor() {
		Timber.d("注销声音传感器");
		if (mVoiceSensor != null) {
			mVoiceSensor.unregisterVoiceListener();
		}
	}

	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int position) {
			mBgViewPager.setCurrentItem(position);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};

	@Override
	public void onShakeMode(int level) {
		if (mStateMode != STATE_SHAKE) {
			registerShakeSensor();
			unregisterVoiceSensor();
			mStateMode = STATE_SHAKE;
			mActivity.sendBroadcast(new Intent("com.linkcube.resetvoicemodeview"));
			mActivity.sendBroadcast(new Intent("com.linkcube.resetsexpositionmodeview"));
		}
		try {
			LinkcubeApplication.toyServiceCall.setShakeSensitivity(level);
			Timber.d("摇晃强度=%d", level);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onVoiceMode(int level) {
		if (mStateMode != STATE_VOICE) {
			mStateMode = STATE_VOICE;
			registerVoiceSensor();
			unregisterShakeSensor();
			mActivity.sendBroadcast(new Intent("com.linkcube.resetshakemodeview"));
			mActivity.sendBroadcast(new Intent("com.linkcube.resetsexpositionmodeview"));
		}
		mVoiceSensor.setVoiceLevel(level);
	}

	@Override
	public void onSexPositionMode(int level) {
		if (mStateMode != STATE_POSITION) {
			mStateMode = STATE_POSITION;
			unregisterShakeSensor();
			unregisterVoiceSensor();
			mActivity.sendBroadcast(new Intent("com.linkcube.resetvoicemodeview"));
			mActivity.sendBroadcast(new Intent("com.linkcube.resetshakemodeview"));
		}
		try {
			LinkcubeApplication.toyServiceCall.cacheSexPositionMode(level);
			Timber.d("体位=%d", level);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void showConnectBluetoothTip() {
		AlertUtils.showToast(mActivity, "设备没有连接，请连接后再常尝试");
	}

	@Override
	public void showLoginActivity() {
		startActivity(new Intent(mActivity, LoginActivity.class));

	}

	@Override
	public void showMoreSettingActivity() {
		startActivity(new Intent(mActivity, SettingActivity.class));

	}

	@Override
	public void showBluetoothSettingActivity() {
		startActivity(new Intent(mActivity, BluetoothSettingActivity.class));
	}

	@Override
	public void resetToy() {
		try {
			LinkcubeApplication.toyServiceCall.closeToy();
			mStateMode = STATE_NONE;
			unregisterShakeSensor();
			unregisterVoiceSensor();
			mActivity.sendBroadcast(new Intent("com.linkcube.resetview"));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void showUserInfoActivity() {
		startActivity(new Intent(mActivity, UserInfoActivity.class));
	}

}
