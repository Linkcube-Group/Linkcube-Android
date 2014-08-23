package me.linkcube.app.sync.core;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * 
 * @author Rodriguez-xin
 * 
 */
public class ASmackUtils {

	private final static String SERVERNAME = "@lcserver";// 测试ip：@server1   正式ip：@lcserver

	public static String ROSTER_NAME = null;

	public static String ROSTER_JID = null;

	/**
	 * 获取当前用户JID
	 * 
	 * @return
	 */
	public static String getUserJID() {
		ROSTER_JID = getRosterName() + SERVERNAME;
		return ROSTER_JID;
	}

	/**
	 * 获取当前用户名
	 * 
	 * @return
	 */
	public static String getRosterName() {

		if (ASmackManager.getInstance().getXMPPConnection() != null) {
			if (ROSTER_NAME == null) {
				String rosterName = ASmackManager.getInstance()
						.getXMPPConnection().getUser().toString();
				ROSTER_NAME = rosterName.substring(0, rosterName.indexOf("@"));
			} else {
				return ROSTER_NAME;
			}

		}
		return ROSTER_NAME;
	}

	/**
	 * 返回好友JID
	 * 
	 * @param friendName
	 * @return JID
	 */
	public static String getFriendJid(String friendName) {
		return friendName + ASmackUtils.SERVERNAME;
	}

	/**
	 * 去除jid中的服务器地址
	 * 
	 * @param JID
	 * @return
	 */
	public static String deleteServerAddress(String JID) {
		String username = JID.substring(0, JID.indexOf("@"));
		return username;
	}

	/**
	 * 判断给定字符串中是否包含空格
	 * 
	 * @param input
	 * @return
	 */
	public static boolean containWhiteSpace(String input) {
		Pattern pattern = Pattern.compile("\\s");
		Matcher matcher = pattern.matcher(input);
		boolean found = matcher.find();
		return found;
	}

	public static String getUserAge(String birthday) {
		// 网络问题会影响年龄设置
		if (birthday != null) {
			String[] birthData = birthday.split("-");
			int birthYear = Integer.parseInt(birthData[0]);
			Calendar cal = Calendar.getInstance();
			int curYear = cal.get(Calendar.YEAR);
			int age = curYear - birthYear;
			String ageString = age + "";
			return ageString;
		} else {
			return "23";
		}
	}

	/**
	 * 将用户名中的"@"替换为"-"
	 * 
	 * @param _userName
	 * @return
	 */
	public static String userNameEncode(String _userName) {
		String userName = _userName.replace("@", "-");
		return userName;
	}

	/**
	 * 将用户名中的"-"替换为"@"
	 * 
	 * @param _userName
	 * @return
	 */
	public static String userNameDecode(String _userName) {
		String userName = _userName.replace("-", "@");
		return userName;
	}
}
