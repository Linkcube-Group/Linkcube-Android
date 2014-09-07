package me.linkcube.app.common.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class ActivityUtils {

	public static void goHome(Activity activity, Class<?> homeActivityClass) {
		activity.finish();
		Intent intent = new Intent(activity, homeActivityClass);
		intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
		activity.startActivity(intent);
	}

	/**
	 * 判断activity是否正在前台运行
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isRunningForeground(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		String currentPackageName = cn.getPackageName();
		if (!TextUtils.isEmpty(currentPackageName)
				&& currentPackageName.equals(context.getPackageName())) {
			return true;
		}
		return false;
	}

}
