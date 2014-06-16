package me.linkcube.app.core.persistable;

/**
 * 
 * @author Orange
 */
public class DBConst {

	/*public static class USER {
		public static final String TABLE_NAME = "user";

		public static final String ID = "id";

		public static final String USER_NAME = "userName";

		public static String createSql() {
			StringBuffer buf = new StringBuffer("CREATE TABLE ");
			buf.append(TABLE_NAME).append(" (");
			buf.append(ID).append(" TEXT PRIMARY KEY, ");
			buf.append(USER_NAME).append(" TEXT);");

			return buf.toString();
		}
	}*/
	
	/**
	 * 用户个人信息表
	 * @author Rodriguez-xin
	 *
	 */
	public static class TABLE_USER {
		public static final String TABLE_NAME = "tbl_user";

		public static final String ID = "id";
		public static final String JID = "jid";
		public static final String USER_NAME = "userName";
		public static final String NICK_NAME = "nickName";
		public static final String USER_AVATAR = "userAvatar";
		public static final String LINKCUBE_ID = "linkcubeId";
		public static final String PERSON_STATE = "personState";
		public static final String USER_AGE = "userAge";
		public static final String BIRTHDAY = "birthday";
		public static final String ASTROLOGY = "astrology";
		public static final String USER_GENDER = "userGender";

		public static String createSql() {
			StringBuffer buf = new StringBuffer("CREATE TABLE ");
			buf.append(TABLE_NAME).append(" (");
			buf.append(ID).append(" TEXT PRIMARY KEY, ");
			buf.append(JID).append(" TEXT,");
			buf.append(USER_NAME).append(" TEXT,");
			buf.append(NICK_NAME).append(" TEXT,");
			buf.append(USER_AVATAR).append(" BLOB,");
			buf.append(LINKCUBE_ID).append(" TEXT,");
			buf.append(PERSON_STATE).append(" TEXT,");
			buf.append(USER_AGE).append(" TEXT,");
			buf.append(BIRTHDAY).append(" TEXT,");
			buf.append(ASTROLOGY).append(" TEXT,");
			buf.append(USER_GENDER).append(" TEXT);");

			return buf.toString();
		}
	}
	/**
	 * 聊天记录表
	 * @author Rodriguez-xin
	 *
	 */
	public static class TABLE_CHAT {
		public static final String TABLE_NAME = "tbl_chat";

		public static final String ID = "id";
		public static final String USER_NAME = "userName";
		public static final String FRIEND_NAME= "friendName";
		public static final String FRIEND_NICKNAME= "friendNickname";
		public static final String IS_AFTER_READ = "isAfterRead";
		public static final String MSG_FLAG = "msgFlag";
		public static final String MESSAGE = "message";
		public static final String MSG_TIME = "msgTime";
		

		public static String createSql() {
			StringBuffer buf = new StringBuffer("CREATE TABLE ");
			buf.append(TABLE_NAME).append(" (");
			buf.append(ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
			buf.append(USER_NAME).append(" TEXT,");
			buf.append(FRIEND_NAME).append(" TEXT,");
			buf.append(FRIEND_NICKNAME).append(" TEXT,");
			buf.append(IS_AFTER_READ).append(" INTEGER DEFAULT '0',");
			buf.append(MSG_FLAG).append(" TEXT,");
			buf.append(MESSAGE).append(" TEXT,");
			buf.append(MSG_TIME).append(" TEXT);");

			return buf.toString();
		}
	}

	/**
	 * 好友信息列表
	 * @author Rodriguez-xin
	 *
	 */
	public static class TABLE_FRIEND {
		public static final String TABLE_NAME = "tbl_friend";

		public static final String ID = "id";
		public static final String USER_JID = "userJid";
		public static final String FRIEND_JID = "friendJid";
		public static final String NICK_NAME = "nickName";
		public static final String USER_AVATAR = "userAvatar";
		public static final String PERSON_STATE = "personState";
		public static final String USER_AGE = "userAge";
		public static final String BIRTHDAY = "birthday";
		public static final String ASTROLOGY = "astrology";
		public static final String USER_GENDER = "userGender";
		public static final String IS_FRIEND = "isFriend";

		public static String createSql() {
			StringBuffer buf = new StringBuffer("CREATE TABLE ");
			buf.append(TABLE_NAME).append(" (");
			buf.append(ID).append(" INTEGER[4] PRIMARY KEY, ");
			buf.append(USER_JID).append(" TEXT,");
			buf.append(FRIEND_JID).append(" TEXT,");
			buf.append(NICK_NAME).append(" TEXT,");
			buf.append(USER_AVATAR).append(" BLOB,");
			buf.append(PERSON_STATE).append(" TEXT,");
			buf.append(USER_AGE).append(" TEXT,");
			buf.append(BIRTHDAY).append(" TEXT,");
			buf.append(ASTROLOGY).append(" TEXT,");
			buf.append(USER_GENDER).append(" TEXT,");
			buf.append(IS_FRIEND).append(" TEXT);");

			return buf.toString();
		}
	}
	/**
	 * 好友申请表
	 * @author Rodriguez-xin
	 *
	 */
	public static class TABLE_FRIEND_REQUEST {
		public static final String TABLE_NAME = "tbl_friend_request";

		public static final String ID = "id";
		public static final String USER_NAME = "userName";
		public static final String FRIEND_NAME = "friendName";
		public static final String SUBSCRIPTION = "subscription";

		public static String createSql() {
			StringBuffer buf = new StringBuffer("CREATE TABLE ");
			buf.append(TABLE_NAME).append(" (");
			buf.append(ID).append(" INTEGER[4] PRIMARY KEY, ");
			buf.append(USER_NAME).append(" TEXT,");
			buf.append(FRIEND_NAME).append(" TEXT,");
			buf.append(SUBSCRIPTION).append(" TEXT);");

			return buf.toString();
		}
	}
}
