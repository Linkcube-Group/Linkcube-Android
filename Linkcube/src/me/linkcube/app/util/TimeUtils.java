package me.linkcube.app.util;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

	/**
	 * 获取当前日期和时间
	 * 
	 * @return
	 */
	public static String getNowDateAndTime() {
		Calendar calendar = Calendar.getInstance();
		String year = addZero(String.valueOf(calendar.get(Calendar.YEAR)));
		String month = addZero(String.valueOf(calendar.get(Calendar.MONTH) + 1));// 月份比实际的少一
		String day = addZero(String
				.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
		String hour = addZero(String
				.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
		String mins = addZero(String.valueOf(calendar.get(Calendar.MINUTE)));
		String seconds = addZero(String.valueOf(calendar.get(Calendar.SECOND)));
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append(year + "-" + month + "-" + day + " " + hour + ":" + mins
				+ ":" + seconds);
		return sBuffer.toString();
	}

	public static String addZero(String str) {
		if (Integer.parseInt(str) >= 0 && Integer.parseInt(str) <= 9) {
			str = "0" + str;
		}
		return str;
	}

	/**
	 * 得到当前时间(小时和分钟),换算出之前则显示日期
	 * 
	 * @return
	 */
	public static String toNowTime(String dateAndTime) {
		Calendar calendar = Calendar.getInstance();
		Date mDate = new Date();
		SimpleDateFormat simFormat = (SimpleDateFormat) DateFormat
				.getInstance();
		try {
			simFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
			mDate = simFormat.parse(dateAndTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (mDate.getMonth() < Integer.parseInt(addZero(String.valueOf(calendar
				.get(Calendar.MONTH))))
				|| mDate.getDate() < Integer.parseInt(addZero(String
						.valueOf(calendar.get(Calendar.DAY_OF_MONTH))))) {
			if (PreferenceUtils.getInt("app_language", 0) == 1) {
				return mDate.getDate() + "/" + mDate.getMonth() + 1;
			} else {
				return mDate.getMonth() + 1 + "月" + mDate.getDate() + "日";
			}

		} else {
			String hour = addZero(mDate.getHours() + "");
			String minus = addZero(mDate.getMinutes() + "");
			String nowTime = hour + ":" + minus;
			return nowTime;
		}
	}
	/**
	 * 获取当前小时和分钟
	 * @return
	 */
	public static String getNowTime() {
		Calendar calendar = Calendar.getInstance();
		String hour = addZero(String
				.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
		String mins = addZero(String.valueOf(calendar.get(Calendar.MINUTE)));
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append(hour + ":" + mins);
		return sBuffer.toString();

	}

	/**
	 * 根据日期格式判断是周几
	 * 
	 * @param pTime
	 * @return
	 * @throws Exception
	 */
	@SuppressLint("SimpleDateFormat")
	public static int dayForWeek(String pTime) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(format.parse(pTime));
		int dayForWeek = 0;
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			dayForWeek = 7;
		} else {
			dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		}
		return dayForWeek;
	}

	private TimeUtils() {

	}

}
