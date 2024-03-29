package me.linkcube.app.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正在表达式工具类
 * 
 * @author Ervin
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
	/**
	 * 判断是不是包含中文
	 * @param str
	 * @return
	 */
	public static boolean isContainChinese(String str) {
		 
		Pattern p=Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m=p.matcher(str);
		if(m.find())
		{
			return true;
		}
		return false;
	}
	/**
	 * 当用户名超过指定长度时进行截取
	 * @param userName
	 * @return
	 */
	public static String cutUserName(String userName){
		String cutUserName;
		if(isContainChinese(userName)){
			if(userName.length()>6){
				cutUserName=userName.substring(0, 6);
				return cutUserName+"...";
			}else{
				return userName;
			}
		}else{
			if(userName.length()>12){
				cutUserName=userName.substring(0, 12);
				return cutUserName+"...";
			}else{
				return userName;
			}
		}
	}
	
}
