package me.linkcube.app.core.bluetooth;

import static me.linkcube.app.core.Const.Device.DEVICE_ADDRESS;
import static me.linkcube.app.core.Const.Device.DEVICE_NAME;

import java.util.Timer;
import java.util.TimerTask;

import android.os.RemoteException;
import me.linkcube.app.LinkcubeApplication;
import me.linkcube.app.core.Timber;
import me.linkcube.app.util.PreferenceUtils;



public class ReconnectBluetoothDevice {

	private Timer timer;
	
	private int secondCount=0;
	
	private static ReconnectBluetoothDevice instance;
	
	public static ReconnectBluetoothDevice getInstance() {
		if (instance == null) {
			synchronized (ReconnectBluetoothDevice.class) {
				if (instance == null) {
					instance = new ReconnectBluetoothDevice();
				}
			}
		}

		return instance;
	}

	private ReconnectBluetoothDevice() {

	}
	
	public void onReconnectDeviceListener(){
		if(timer==null){
			timer=new Timer();
			timer.schedule(new TimerTask() {
				
				@Override
				public void run() {
						try {
							if(!LinkcubeApplication.toyServiceCall.isToyConnected()){
								secondCount++;
								Timber.d("secondCount1:"+secondCount);
								try {
									LinkcubeApplication.toyServiceCall.connectToy(
											PreferenceUtils.getString(DEVICE_NAME,""), PreferenceUtils.getString(DEVICE_ADDRESS,""));
								} catch (RemoteException e) {
									e.printStackTrace();
								}
								if(LinkcubeApplication.toyServiceCall.isToyConnected()){
									timer.cancel();
									timer=null;
								}
							}
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					
				}
			}, 3000, 3000);
		}
	}
}
