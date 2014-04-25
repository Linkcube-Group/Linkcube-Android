package me.linkcube.app.util;

/**
 * 正在表达式工具类
 * 
 * @author orange
 * 
 */
public class RegexUtils {

	/**
	 * 判断是不是邮箱格式
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmailAddress(String str) {
		if (str.matches("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$")) {
			return true;
		} else {
			return false;
		}
	}

}
