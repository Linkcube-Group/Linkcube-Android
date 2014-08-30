package me.linkcube.app.ui.main.single;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import me.linkcube.app.LinkcubeApplication;
import me.linkcube.app.R;
import me.linkcube.app.core.Const;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.bluetooth.DeviceConnectionManager;
import me.linkcube.app.core.toy.AudioRecorder;
import me.linkcube.app.core.toy.ShakeSensor;
import me.linkcube.app.core.toy.VoiceSensor;
import me.linkcube.app.core.user.UserManager;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.ui.BaseFragment;
import me.linkcube.app.ui.bluetooth.BluetoothSettingActivity;
import me.linkcube.app.ui.main.SensorProvider;
import me.linkcube.app.ui.setting.SettingActivity;
import me.linkcube.app.ui.user.LoginActivity;
import me.linkcube.app.ui.user.UserInfoActivity;
import me.linkcube.app.util.NotificationUtils;
import me.linkcube.app.util.PreferenceUtils;
import me.linkcube.app.widget.AlertUtils;
import me.linkcube.app.widget.CirclePageIndicator;
import static me.linkcube.app.core.Const.ToyConst.*;

/**
 * 单人模式页面
 * 
 * @author Ervin
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
		// TODO 注销广播
		mAdapter.unRegisterReceiver();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		mAdapter.changeLanguageForTv();
		mBgPagerAdapter.setLanguage();
		mBgPagerAdapter.notifyDataSetChanged();
		mBgViewPager.invalidate();
		if (UserManager.getInstance().isAuthenticated()
				&& UserManager.getInstance().getUserInfo() != null) {
			Timber.i("nickname:"
					+ UserManager.getInstance().getUserInfo().getNickName());
			if (UserManager.getInstance().getUserInfo().getNickName() == null) {
				PreferenceUtils.removeData(Const.Preference.USER_NAME);
				PreferenceUtils.removeData(Const.Preference.USER_PWD);
				PreferenceUtils.setBoolean(Const.Preference.AUTO_LOGIN, false);
			}
			statusBarView.setUserInfo(UserManager.getInstance().getUserInfo());
		} else {
			statusBarView.setUserInfo(null);
		}
		if (LinkcubeApplication.toyServiceCall != null) {
			try {
				statusBarView.setBluetoothState(DeviceConnectionManager
						.getInstance().isConnected());
			} catch (Exception e) {
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

	private void unregisterAudioRecorder() {
		Timber.d("注销AudioRecorder");
		if (AudioRecorder.getInstance() != null) {
			AudioRecorder.getInstance().stopAudioRecorder();
			mAdapter.changeMicSoundIv(1);
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
		// if (mStateMode != STATE_SHAKE) {
		registerShakeSensor();
		unregisterVoiceSensor();
		unregisterAudioRecorder();
		mStateMode = STATE_SHAKE;
		mActivity.sendBroadcast(new Intent("com.linkcube.resetvoicemodeview"));
		mActivity.sendBroadcast(new Intent("com.linkcube.resetmicmodeview"));
		mActivity.sendBroadcast(new Intent(
				"com.linkcube.resetsexpositionmodeview"));
		// }
		try {
			LinkcubeApplication.toyServiceCall.setShakeSensitivity(level);
			Timber.d("摇晃强度=%d", level);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void offShakeMode(int level) {
		Timber.d("关闭摇一摇模式--注销传感器");
		if (mShakeSensor != null) {
			mShakeSensor.unRegisterListener();
		}
	}

	@Override
	public void onVoiceMode(int level) {
		// if (mStateMode != STATE_VOICE) {
		mStateMode = STATE_VOICE;
		registerVoiceSensor();
		unregisterShakeSensor();
		unregisterAudioRecorder();
		mActivity.sendBroadcast(new Intent("com.linkcube.resetshakemodeview"));
		mActivity.sendBroadcast(new Intent("com.linkcube.resetmicmodeview"));
		mActivity.sendBroadcast(new Intent(
				"com.linkcube.resetsexpositionmodeview"));
		// }
		mVoiceSensor.setVoiceLevel(level);
	}

	@Override
	public void offVoiceMode(int level) {
		Timber.d("关闭音浪模式--注销声音传感器");
		if (mVoiceSensor != null) {
			mVoiceSensor.unregisterVoiceListener();
		}
	}

	@Override
	public void onSexPositionMode(int level) {
		if (mStateMode != STATE_POSITION) {
			mStateMode = STATE_POSITION;
			unregisterShakeSensor();
			unregisterVoiceSensor();
			unregisterAudioRecorder();
			mActivity.sendBroadcast(new Intent(
					"com.linkcube.resetvoicemodeview"));
			mActivity
					.sendBroadcast(new Intent("com.linkcube.resetmicmodeview"));
			mActivity.sendBroadcast(new Intent(
					"com.linkcube.resetshakemodeview"));
		}
		try {
			LinkcubeApplication.toyServiceCall.cacheSexPositionMode(level);
			Timber.d("体位=%d", level);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void offMicMode(int level) {
		Timber.d("关闭语音模式--");
		mAdapter.changeMicSoundIv(1);
		try {
			LinkcubeApplication.toyServiceCall.setMicWave(0);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		AudioRecorder.getInstance().stopAudioRecorder();
	}

	@Override
	public void onMicMode(int level) {
		Timber.d("开启语音模式--");
		mStateMode = STATE_MIC;
		unregisterVoiceSensor();
		unregisterShakeSensor();
		mActivity.sendBroadcast(new Intent("com.linkcube.resetshakemodeview"));
		mActivity.sendBroadcast(new Intent("com.linkcube.resetvoicemodeview"));
		mActivity.sendBroadcast(new Intent(
				"com.linkcube.resetsexpositionmodeview"));
		AudioRecorder.getInstance().startAudioRecorder(new ASmackRequestCallBack() {
			
			@Override
			public void responseSuccess(Object object) {
				Message msg = new Message();
				msg.what = (Integer) object;
				micHandler.sendMessage(msg);
			}
			
			@Override
			public void responseFailure(int reflag) {
				
			}
		});
	}

	private Handler micHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int micSound = msg.what;
			mAdapter.changeMicSoundIv(micSound);
			try {
				LinkcubeApplication.toyServiceCall.setMicWave(micSound);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void showConnectBluetoothTip() {
		AlertUtils
				.showToast(
						mActivity,
						getResources().getString(
								R.string.toast_toy_disconnect_pls_set));
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
			unregisterAudioRecorder();
			mActivity.sendBroadcast(new Intent("com.linkcube.resetview"));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void showUserInfoActivity() {
		startActivity(new Intent(mActivity, UserInfoActivity.class));
	}

	@Override
	public void showOpenMusicPlayerDialog() {
		new AlertDialog.Builder(mActivity)
				.setMessage("最小化连酷APP，打开音乐或视频播放器，玩具将随着音浪High起来！")
				.setTitle("提示")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Intent.ACTION_MAIN);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
						intent.addCategory(Intent.CATEGORY_HOME);
						mActivity.startActivity(intent);
						NotificationUtils.appPauseNotification(mActivity, 1100,
								"连酷 Linkcube");
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();
	}

}
