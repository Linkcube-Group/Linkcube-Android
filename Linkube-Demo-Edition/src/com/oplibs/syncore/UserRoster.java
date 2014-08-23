package com.oplibs.syncore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*
 * 
 * 封装用户Roster
 * 
 */
public class UserRoster {
	public String JID;
	public String NickName;
	public String PS;
	public String userAge;
	public String birthDate;
	public String astrology;
	public String connectCode;
	public String userGendre;
	public boolean isWoman;
	
	public UserRoster(UserRoster roster)
	{
		JID = roster.JID;
		NickName = roster.NickName;
		PS = roster.PS;
		userAge = roster.userAge;
		isWoman = roster.isWoman;
		birthDate = roster.birthDate;
		astrology = roster.astrology;
		connectCode = roster.connectCode;
		userGendre = roster.userGendre;
	}
	
	public UserRoster()
	{
		JID = "";
		NickName = "维纳斯";
		isWoman = false;
		PS = "连我，开始游戏哦~";	
		birthDate = "1990-01-01";
		userAge = UserAge()+"";
		astrology = "摩羯座";
		connectCode = "123456";
		userGendre = "女";
	}
	
	public int UserAge()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");	
		Date birthdate=null;
		if(birthDate!=null)
		{
			try {
				birthdate = sdf.parse(birthDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				try {
					birthdate = sdf.parse("1990-01-01");
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
		else
		{	
			try {
				birthdate = sdf.parse("1990-01-01");
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	    Calendar cal = Calendar.getInstance();
	    int curyear = cal.get(Calendar.YEAR);
		int age = curyear - birthdate.getYear()-1900;
		return age;
	}

	public String UserAstrology()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");	
		Date birthdate=null;
		if(birthDate!=null)
		{
			try {
				birthdate = sdf.parse(birthDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				try {
					birthdate = sdf.parse("1990-01-01");
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
		else
		{	
			try {
				birthdate = sdf.parse("1990-01-01");
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		int day = birthdate.getDay();
		switch(birthdate.getMonth())
		{
		case 0:
			if(day<=20)
			{
				return "摩羯座";
			}
			else
			{
				return "水瓶座";
			}
		case 1:
			if(day<=20)
			{
				return "水瓶座";
			}
			else
			{
				return "双鱼座";
			}
		case 2:
			if(day<=20)
			{
				return "双鱼座";
			}
			else
			{
				return "白羊座";
			}
		case 3:
			if(day<=20)
			{
				return "白羊座";
			}
			else
			{
				return "金牛座";
			}
		case 4:
			if(day<=20)
			{
				return "金牛座";
			}
			else
			{
				return "双子座";
			}
		case 5:
			if(day<=20)
			{
				return "双子座";
			}
			else
			{
				return "巨蟹座";
			}
		case 6:
			if(day<=20)
			{
				return "巨蟹座";
			}
			else
			{
				return "狮子座";
			}
		case 7:
			if(day<=20)
			{
				return "狮子座";
			}
			else
			{
				return "处女座";
			}
		case 8:
			if(day<=20)
			{
				return "处女座";
			}
			else
			{
				return "天秤座";
			}
		case 9:
			if(day<=20)
			{
				return "天秤座";
			}
			else
			{
				return "天蝎座";
			}
		case 10:
			if(day<=20)
			{
				return "天蝎座";
			}
			else
			{
				return "射手座";
			}
		case 11:
			if(day<=20)
			{
				return "射手座";
			}
			else
			{
				return "魔羯座";
			}
		}
		return null;
	}
	
	public static String GetDomain(String jid)
	{
		if(jid!=null)
		{
			int apos = jid.indexOf("@");
			int rpos = jid.indexOf("/");
			if(apos>=0&&apos<jid.length())
			{
				String domain;
				int len = 0;
				if(rpos>=apos&&rpos<jid.length())
				{
					domain = jid.substring(apos,rpos);
				}
				else
				{
					domain = jid.substring(apos);
				}
				
				return domain;
			}
			else
			{
				return null;
			}
		}
		return null;
	}
}
