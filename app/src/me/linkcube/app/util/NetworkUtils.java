package me.linkcube.app.util;

import me.linkcube.app.core.Timber;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.ASmackManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

public class NetworkUtils {

	public static boolean isNetworkAvailable(Context context) {

		try {
			ConnectivityManager connManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNet = connManager.getActiveNetworkInfo();
			if (null == activeNet) {
				return false;
			}
			return activeNet.isAvailable();
		} catch (Exception e) {
			Timber.e(e, "NetworkInfo 调用异常");
			return false;
		}
	}

	public static boolean isConnected(final ASmackRequestCallBack iCallBack) {
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (msg.what != 0)
					iCallBack.responseFailure(msg.what);
			}

		};
		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						sleep(5 * 1000);
						Message msg = new Message();
						if (ASmackManager.getInstance().getXMPPConnection()
								.isConnected()) {
							msg.what = 0;// 连接正确
						} else if (!ASmackManager.getInstance()
								.getXMPPConnection().isConnected()) {
							msg.what = -1;// 没有链接上
						} else {
							msg.what = 1;// 其他错误
						}
						handler.sendMessage(msg);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}.start();
		return false;

	}

}
