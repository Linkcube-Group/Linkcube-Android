package me.linkcube.app.core.bluetooth;

import static me.linkcube.app.core.Const.Device.DEVICE_ADDRESS;
import static me.linkcube.app.core.Const.Device.DEVICE_NAME;

import java.util.Timer;
import java.util.TimerTask;

import android.os.RemoteException;
import me.linkcube.app.LinkcubeApplication;
import me.linkcube.app.core.Timber;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.util.PreferenceUtils;

public class CheckDeviceConnect {

	private Timer timer;

	private int secondCount = 0;

	private boolean mIsConnected = false;

	private static CheckDeviceConnect instance;

	public ASmackRequestCallBack checkDeviceConnectCallBack;

	public static CheckDeviceConnect getInstance() {
		if (instance == null) {
			synchronized (CheckDeviceConnect.class) {
				if (instance == null) {
					instance = new CheckDeviceConnect();
				}
			}
		}

		return instance;
	}

	private CheckDeviceConnect() {

	}

	public void onReconnectDeviceListener() {
		if (timer == null) {
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {

					try {
						if (mIsConnected != LinkcubeApplication.toyServiceCall
								.checkData()) {
							mIsConnected = LinkcubeApplication.toyServiceCall
									.checkData();
							Timber.d("mIsConnected:" + mIsConnected);
							if (mIsConnected) {
								checkDeviceConnectCallBack.responseSuccess(0);
							} else {
								checkDeviceConnectCallBack.responseFailure(-1);
							}
						}
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}

					/*
					 * try { if (!LinkcubeApplication.toyServiceCall
					 * .isToyConnected()) { secondCount++;
					 * Timber.d("secondCount1:" + secondCount); try {
					 * LinkcubeApplication.toyServiceCall.connectToy(
					 * PreferenceUtils.getString(DEVICE_NAME, ""),
					 * PreferenceUtils.getString( DEVICE_ADDRESS, "")); } catch
					 * (RemoteException e) { e.printStackTrace(); } if
					 * (LinkcubeApplication.toyServiceCall .isToyConnected()) {
					 * timer.cancel(); timer = null; } } } catch
					 * (RemoteException e) { e.printStackTrace(); }
					 */

				}
			}, 3000, 3000);
		}
	}
	
	public void setmIsConnected(boolean mIsConnected) {
		this.mIsConnected = mIsConnected;
	}

	public boolean isConnected() {
		return mIsConnected;
	}

	public void setCheckDeviceConnectCallBack(
			ASmackRequestCallBack checkDeviceConnectCallBack) {
		this.checkDeviceConnectCallBack = checkDeviceConnectCallBack;
	}

}
