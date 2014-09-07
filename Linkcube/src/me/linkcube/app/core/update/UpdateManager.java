package me.linkcube.app.core.update;

import me.linkcube.app.common.util.PreferenceUtils;
import me.linkcube.app.core.Const;
import me.linkcube.app.core.Timber;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class UpdateManager {

	private static UpdateManager updateManager = null;

	private boolean isForcedUpdate;// 是否需要强制更新

	private boolean isUpdate;// 是否需要更新

	public static UpdateManager getInstance() {
		if (updateManager == null) {
			updateManager = new UpdateManager();
		}
		return updateManager;
	}

	private UpdateManager() {

	}

	private String getVersionName(Context context) throws Exception {
		// 获取packagemanager的实例
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(
				context.getPackageName(), 0);
		String version = packInfo.versionName;
		return version;
	}

	public void checkNeedUpdate(Context context, String newVersionName) {

		int isNeedUpdate = 0;
		try {
			Timber.d(newVersionName + "---" + getVersionName(context));
			isNeedUpdate = newVersionName.compareTo(getVersionName(context));
		} catch (Exception e) {
			e.printStackTrace();
		}
		int addUpdateFlag = PreferenceUtils.getInt(
				Const.AppUpdate.APK_UPDATE_FLAG, 0);
		if (addUpdateFlag != 2) {
			if (isNeedUpdate > 0) {
				PreferenceUtils.setInt(Const.AppUpdate.APK_UPDATE_FLAG, 1);
				setUpdate(true);
			} else {
				PreferenceUtils.setInt(Const.AppUpdate.APK_UPDATE_FLAG, 0);
				setUpdate(false);
			}
		}

	}

	public boolean isForcedUpdate() {
		return isForcedUpdate;
	}

	public void setForcedUpdate(boolean isForcedUpdate) {
		this.isForcedUpdate = isForcedUpdate;
	}

	public boolean isUpdate() {
		return isUpdate;
	}

	public void setUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

}
