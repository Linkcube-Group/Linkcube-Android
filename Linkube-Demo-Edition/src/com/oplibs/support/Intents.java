package com.oplibs.support;

/*
 * 
 * Intents调用类的设定
 * 
 */
public class Intents
{
	public static String ConnectFailed = "com.expolder.oncall.ConnectFailed";
	public static String ConnectSucceed = "com.expolder.oncall.ConnectSucceed";
	public static String LoggedIn = "com.expolder.oncall.LoggedIn";
	public static String LoggedInFailed = "com.expolder.oncall.LoginFailed";
	
	public static String RegisterSucceed = "com.expolder.oncall.RegisterSucceed";
	public static String RegisterFailed = "com.expolder.oncall.RegisterFailed";
	
	public static String PresenceReceived = "com.expolder.oncall.PresenceReceived";
	
	public static String MessageReceived = "com.expolder.oncall.MessageReceived.";
	
	public static String AvatarUpdated = "com.expolder.oncall.AvatarUpdated";
	
	public static String VoiceMessageStartReceive = "com.expolder.oncall.VoiceMessageStartReceive.";
	public static String VoiceMessageReceived = "com.expolder.oncall.VoiceMessageReceived.";
	public static String VoiceMessageSent = "com.expolder.oncall.VoiceMessageSent.";
	
	public final static int KActionNull=0;
	public final static int KActionLogin=1;
	public final static int KActionRegister=2;
	
	public static String RegisterReason = "Reason";
}
