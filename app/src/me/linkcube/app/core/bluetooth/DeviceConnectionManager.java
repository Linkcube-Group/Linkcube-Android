package me.linkcube.app.core.bluetooth;

import java.util.Timer;
import java.util.TimerTask;

import android.bluetooth.BluetoothDevice;
import android.os.RemoteException;
import me.linkcube.app.LinkcubeApplication;
import me.linkcube.app.core.Timber;

public class DeviceConnectionManager {

	private Timer timer;

	private boolean mIsConnected = false;

	private static DeviceConnectionManager instance;

	public CheckConnectionCallback callback;

	private BluetoothDevice device;

	public boolean isSexPositionMode;

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

	public void startCheckConnetionTask() {
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {

				if (!isSexPositionMode) {

					try {
						if (mIsConnected != LinkcubeApplication.toyServiceCall
								.checkData()) {
							mIsConnected = LinkcubeApplication.toyServiceCall
									.checkData();
							Timber.d("mIsConnected:" + mIsConnected);
							if (mIsConnected) {
								callback.stable();
							} else {
								stopTimerTask();
							}
						}
					} catch (RemoteException e1) {
						callback.interrupted();
						e1.printStackTrace();
					}
				}

			}
		}, 3000, 3000);
	}

	public void cancelCheckConnectionTask() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
	
	public void stopTimerTask(){
		callback.interrupted();
		cancelCheckConnectionTask();
	}

	public void setmIsConnected(boolean mIsConnected, BluetoothDevice device) {
		this.mIsConnected = mIsConnected;
		this.device = device;
	}

	public boolean isConnected() {
		return mIsConnected;
	}

	public boolean isSexPositionMode() {
		return isSexPositionMode;
	}

	public void setSexPositionMode(boolean isSexPositionMode) {
		this.isSexPositionMode = isSexPositionMode;
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
