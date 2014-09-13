package com.oplibs.services;

import java.util.Hashtable;

import com.oplibs.support.LogHelper;

import android.content.SharedPreferences;

/*
 * 
 * XMPP设置
 * 
 */
public class XMPPSettings 
{
	private final String KKeySrvAddr="ServerAddress";
	
	private final String KDefSrvAddr="server1.linkcube.me";
	//private final String KDefSrvAddr="sertest.linkcube.me";
	//private final String KDefSrvAddr="192.168.1.145";
	//private final String KDefSrvAddr="10.88.54.218";
	
	public static String SearchAddr = "search.lcserver1";
	public static String HostName = "@lcserver1";
	//public static String SearchAddr = "search.itrek-pc";
	//public static String HostName = "@itrek-pc";
	//public static String SearchAddr = "search.itrek-nb";
	//public static String HostName = "@itrek-nb";
	
	public LogHelper.LogLevel LogLevel;
	public Boolean KeepAlive;
	

	
	private static XMPPSettings instance;
	
	private SharedPreferences sharedPreferences;

	public static XMPPSettings CreateInstance(SharedPreferences preferences)
	{
		if (instance == null)
		{
			instance = new XMPPSettings(); 
		}
		
		instance.sharedPreferences = preferences;
		
		return instance;
	}
	
	public static XMPPSettings GetInstance()
	{
		return instance;
	}

	
	private XMPPSettings()
	{
		LogLevel = LogHelper.LogLevel.Message;
		KeepAlive = false;
	}
	
	public SharedPreferences GetPreferences()
	{
		return sharedPreferences;
	}
	
	public String GetStringValue(String key)
	{
		return sharedPreferences.getString(key, "");
	}
	
	public void SetStringValue(String key, String value)
	{
		SharedPreferences.Editor editor = sharedPreferences.edit();
		
		editor.putString(key, value);
		editor.commit();
	}
	
	public void SetStringValues(Hashtable<String, String> table)
	{
		if (table == null)
		{
			return;
		}
		
		SharedPreferences.Editor editor = sharedPreferences.edit();
		
		for (String key : table.keySet())
		{
			String value = table.get(key);
			editor.putString(key, value);
		}
		
		editor.commit();
	}
	
	public String GetServerAddress()
	{
		return sharedPreferences.getString(KKeySrvAddr, KDefSrvAddr);
	}
	
	public int GetServerPort()
	{
		return 5222;
	}
}
