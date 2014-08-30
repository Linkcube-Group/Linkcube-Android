package me.linkcube.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 
 * @author Ervin
 */
public class PreferenceUtils {

	private final static String PREFS_NAME = "me.linkcube.app";

	private static SharedPreferences sharedPreference;

	public static void initDataShare(Context context) {
		sharedPreference = context.getSharedPreferences(PREFS_NAME, 0);
	}

	public static void clearData() {
		if (null != sharedPreference) {
			SharedPreferences.Editor editor = sharedPreference.edit();
			editor.clear();
			editor.commit();
		}
	}

	public static void removeData(String key) {
		if (null != sharedPreference) {
			SharedPreferences.Editor editor = sharedPreference.edit();
			editor.remove(key);
			editor.commit();
		}
	}

	public static boolean contains(String key) {
		return sharedPreference.contains(key);
	}

	public static int getInt(String key, int def) {
		if (key == null || key.equals("")) {
			return def;
		}
		return sharedPreference.getInt(key, def);
	}

	public static boolean getBoolean(String key, boolean def) {
		if (key == null || key.equals("")) {
			return def;
		}
		return sharedPreference.getBoolean(key, def);
	}

	public static String getString(String key, String def) {
		if (key == null || key.equals("")) {
			return def;
		}
		return sharedPreference.getString(key, def);
	}

	public static void setInt(String key, int value) {
		Editor editor = sharedPreference.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static void setString(String key, String value) {
		Editor editor = sharedPreference.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void setBoolean(String key, boolean value) {
		Editor editor = sharedPreference.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
}