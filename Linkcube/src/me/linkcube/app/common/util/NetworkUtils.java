package me.linkcube.app.common.util;

import me.linkcube.app.core.Timber;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
/**
 * 判断网络状况类
 * @author Rodriguez-xin
 *
 */
public class NetworkUtils {
	/**
	 * 当前网络是否可用
	 * @param context
	 * @return
	 */
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

}
