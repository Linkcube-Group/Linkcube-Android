package me.linkcube.app.util;

import me.linkcube.app.AppConfig;
import static me.linkcube.app.core.Const.Preference.SHOW_GUIDE;

/**
 * 
 * @author Orange
 * 
 */
public class AppUtils {
	/**
	 * 判断用户是否需要展示引导页
	 */
	public static boolean isShowGuide() {
		String guideTime = PreferenceUtils.getString(SHOW_GUIDE, null);
		if (guideTime == null) {
			PreferenceUtils.setString(SHOW_GUIDE, AppConfig.GUIDE_TIME);
			return true;
		} else {
			if (guideTime.equals(AppConfig.GUIDE_TIME)) {
				return false;
			} else {
				return true;
			}
		}
	}

}
