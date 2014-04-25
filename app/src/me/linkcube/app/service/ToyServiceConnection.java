package me.linkcube.app.service;

import me.linkcube.app.LinkcubeApplication;
import me.linkcube.app.service.IToyServiceCall;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * 玩具启动服务与activity通信
 * 
 * 
 */
public class ToyServiceConnection implements ServiceConnection {

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		LinkcubeApplication.toyServiceCall = (IToyServiceCall) service;
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		LinkcubeApplication.toyServiceCall = null;
	}
}
