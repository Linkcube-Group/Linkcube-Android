package com.oplibs.services;

/*
 * 
 * XMPP 关于JID封装
 */
public class XMPPUtils {
	private String JID="";
	
	public static String GetDisplayID(String jid)
	{
		String[] result = jid.split("@");
		if(result.length>0)
		{
			String res;
			res = result[0].replace('-', '@');
			return res;
		}
		return "";
	}
	public static String GetJID(String uid)
	{
		if(uid.length()>0)
		{
			String res = uid.replace('@', '-');
			return res;
		}
		return "";
	}
}
