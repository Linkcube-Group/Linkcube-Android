package me.linkcube.client;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;

/*
 * 
 * 程序配置 用户密码等
 * 
 */

public class LinkcubeConfiguration implements OnSharedPreferenceChangeListener{
	private static final String TAG = "yaxim.Configuration";

	private static final String GMAIL_SERVER = "talk.google.com";
	
	private static final String id_username = "username";
	private static final String id_password = "password";
	private static final String id_autologin = "autologin";
	//private static final String id_connectcode = "connectcode";

	public String userID;
	public String passWord;
	//public String connectionCode;
	public Boolean isSave;

	private final SharedPreferences prefs;

	public LinkcubeConfiguration(SharedPreferences _prefs) {
		prefs = _prefs;
		prefs.registerOnSharedPreferenceChangeListener(this);
		loadPrefs(prefs);
	}

	@Override
	protected void finalize() {
		prefs.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		Log.i(TAG, "onSharedPreferenceChanged(): " + key);
		loadPrefs(prefs);
	}

	public void updatePrefs()
	{
		Editor edit = prefs.edit();
        edit.putString(id_username,userID);      
        edit.putString(id_password,passWord);
        edit.putBoolean(id_autologin, isSave);
        //edit.putString(id_connectcode, connectionCode);
        edit.commit();
	}
	public boolean CouldAutoLogin()
	{
		if((this.userID!=null)&&(this.passWord!=null))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private void loadPrefs(SharedPreferences prefs) {
		this.userID = prefs.getString(id_username, null);
		this.passWord = prefs.getString(id_password, null);
		this.isSave = prefs.getBoolean(id_autologin, false);
		//this.connectionCode = prefs.getString(id_connectcode, "123456");
		/*
		this.isLEDNotify = prefs.getBoolean(PreferenceConstants.LEDNOTIFY,
				false);
		this.vibraNotify = prefs.getString(
				PreferenceConstants.VIBRATIONNOTIFY, "SYSTEM");
		this.notifySound = Uri.parse(prefs.getString(
				PreferenceConstants.RINGTONENOTIFY, ""));
		this.ticker = prefs.getBoolean(PreferenceConstants.TICKER,
				true);
		this.password = prefs.getString(PreferenceConstants.PASSWORD, "");
		this.ressource = prefs
				.getString(PreferenceConstants.RESSOURCE, "yaxim");
		this.port = XMPPHelper.tryToParseInt(prefs.getString(
				PreferenceConstants.PORT, PreferenceConstants.DEFAULT_PORT),
				PreferenceConstants.DEFAULT_PORT_INT);

		this.priority = validatePriority(XMPPHelper.tryToParseInt(prefs
				.getString(PreferenceConstants.PRIORITY, "0"), 0));

		this.foregroundService = prefs.getBoolean(PreferenceConstants.FOREGROUND, true);

		this.autoConnect = prefs.getBoolean(PreferenceConstants.CONN_STARTUP,
				false);
		this.autoReconnect = prefs.getBoolean(
				PreferenceConstants.AUTO_RECONNECT, false);
		this.messageCarbons = prefs.getBoolean(
				PreferenceConstants.MESSAGE_CARBONS, true);

		this.smackdebug = prefs.getBoolean(PreferenceConstants.SMACKDEBUG,
				false);
		this.reportCrash = prefs.getBoolean(PreferenceConstants.REPORT_CRASH,
				false);
		this.jabberID = prefs.getString(PreferenceConstants.JID, "");
		this.customServer = prefs.getString(PreferenceConstants.CUSTOM_SERVER,
				"");
		this.require_ssl = prefs.getBoolean(PreferenceConstants.REQUIRE_SSL,
				false);
		this.statusMode = prefs.getString(PreferenceConstants.STATUS_MODE, "available");
		this.statusMessage = prefs.getString(PreferenceConstants.STATUS_MESSAGE, "");
        this.theme = prefs.getString(PreferenceConstants.THEME, "dark");
        this.chatFontSize = prefs.getString("setSizeChat", "18");
		*/
	}

}
