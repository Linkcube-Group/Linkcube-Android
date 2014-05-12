package me.linkcube.app.core.bluetooth;

import java.util.Timer;
import java.util.TimerTask;

import android.bluetooth.BluetoothDevice;
import android.os.RemoteException;
import me.linkcube.app.LinkcubeApplication;
import me.linkcube.app.core.Timber;

public class DeviceConnectionManager {

	private Timer timer;

	private int secondCount = 0;

	private boolean mIsConnected = false;

	private static DeviceConnectionManager instance;

	public CheckConnectionCallback callback;

	private BluetoothDevice device;

	public static DeviceConnectionManager getInstance() {
		if (instance == null) {
			synchronized (DeviceConnectionManager.class) {
				if (instance == null) {
					instance = new DeviceConnectionManager();
				}
			}
		}

		return instance;
	}

	private DeviceConnectionManager() {

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
								callback.stable();
							} else {
								callback.interrupted();
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

	public void setmIsConnected(boolean mIsConnected, BluetoothDevice device) {
		this.mIsConnected = mIsConnected;
		this.device = device;
	}

	public boolean isConnected() {
		return mIsConnected;
	}

	public BluetoothDevice getDeviceConnected() {
		return device;
	}

	public void setCheckConnectionCallBack(CheckConnectionCallback callback) {
		this.callback = callback;
	}

	public interface CheckConnectionCallback {

		/**
		 * 连接状态稳定
		 */
		void stable();

		/**
		 * 主动断开连接
		 */
		void disconnect();

		/**
		 * 被动断开连接
		 */
		void interrupted();
	}

}
