package com.oplibs.services;

interface IOnChatServiceCall
{
	void ConnectToServer(int nextcmd);
	void DisconnectToServer();
	
	void LoginServer();
	void SetUserInfo(String jid, String pwd, String nickname);
	
	boolean CreateNewUser(String userid, String password, String nickname);
	boolean UpdateAvatar(String imgpath);
	
	void SendMessageToUser(String user, String message);
	void SendMessageDirectToUser(String user, String message);
	boolean IsSending();
	void BroadcaseIntent(String action);
	boolean IsConnectted();
	boolean IsAuthenticated();

	String[] GetRosterInfo(String jid);
	boolean SerializeAccountInfo(boolean upload);
	String GetAccountInfoItem(int key);
	boolean SetAccountInfoItem(int key, String value);
	
	boolean SyncRosterInfo(String jid);
	String GetRosterInfoItem(int key);
	
	String GetMsg();
	
	String SearchByID(String jid);
	
	boolean AddFriend(String jid);
	boolean RemoveFriend(String jid);
	boolean ProcessSubscribe(int state, String from, String to);
}