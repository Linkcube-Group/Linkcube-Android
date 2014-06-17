package me.linkcube.app.core;

public class Const {

	public static class Preference {

		/** 引导页展示标志位 */
		public static final String SHOW_GUIDE = "ShowGuide";

		/** 是否自动登录 */
		public static final String AUTO_LOGIN = "AutoLogin";

		/** 登录用户名 */
		public static final String USER_NAME = "UserName";

		/** 登录密码 */
		public static final String USER_PWD = "UserPwd";

		/** 原始密码 */
		public static final String OLD_USER_PWD = "OldUserPwd";
	}

	public static class VCard {

		public static final String GENDER = "GENDER";

		public static final String BIRTHDAY = "BIRTHDAY";

		public static final String PERSONSTATE = "PERSONSTATE";

	}

	public static class Game {

		public static String REQUESTCMD = "c:";

		public static String REQUESTCONNECTCMD = "connectrequest";

		public static String ACCEPTCONNECTCMD = "acceptconnect";

		public static String REFUSECONNECTCMD = "refuseconnect";

		public static String DISCONNECTCMD = "disconnect";

		public static String POSITIONMODECMD = "m:";

		public static String SHAKESPEEDCMD = "s:";

		public static String SHAKESPEEDLEVELCMD = "sl:";

		public static int ACCEPTTIMECMD = 30;
	}

	public static class AppUpdate {

		public static String CHECK_VERSION_URL = "http://115.29.175.17/version";
		
		public static String APK_UPDATE_FLAG = "apk_update_flag";//"0"表示不需要更新也不需要展示，“1”表示需要提示更新，“2”表示已经提示过，需要在setting页面展示

		public static String APK_NAME = "linkcube.apk";
		
		public static String APK_VERSION = "apk_version";
		
		public static String APK_SIZE = "apk_size";
		
		public static String APK_DESCRIPTION = "apk_description";
		
		public static String APK_DOWNLOAD_URL = "apk_download_url";
		
	}

	public static class GameInviteMsg {

		public static String GAME_INVITE_TO = "game_invite_to";

		public static String GAME_INVITE_FROM = "game_invite_from";

		public static String GAME_INVITE_BOTH = "game_invite_both";
	}

	public static class Device {

		public static String DEVICE_NAME = "device_name";

		public static String DEVICE_ADDRESS = "device_address";
	}
	
	public static class DeleteAfterRead {

		public static int COUNT_DOWN = 60;

	}

}
