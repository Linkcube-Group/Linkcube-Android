package me.linkcube.client;

/*
 * 定义某些常量
 * 
 * 
 */
public class LinkDefines {
	
	final static String Client_Name="LinkCube";
	final static int REQ_LoginOrCreate = 0;
	final static int REQ_Login = 1;
	final static int REQ_CreateAccount = 2;
	final static int REQ_ShowInfo = 3;
	final static int REQ_Connectcode = 4;
	final static int REQ_SelectAvatar = 5;
	final static int REQ_LocalConfirmation = 6;
	
	final static int REQ_NickName=6;
	final static int REQ_JID=7;
	final static int REQ_Gendre=8;
	final static int REQ_BirthDate=9;
	final static int REQ_PS=10;
	final static int REQ_ConnectCode=11;
	
	final static int RES_Close=-1;
	final static int RES_Yes=0;
	final static int RES_No=1;
	
	final static int RES_Login=2;
	final static int RES_CreateAccount=3;
	
	final static String Data_Username="Username";
	final static String Data_Nickname="Nickname";
	final static String Data_JID="jid";
	final static String Data_Password="Password";
	final static String Data_Connectcode="connectcode";
	public final static String Data_From="from";
	public final static String Data_CMD="cmd";
	public final static String Data_PS="PS";
	public final static String Data_AvatarBuf="Avatar";
	
	public final static String Data_Title="Title";
	public final static String Data_CurValue="CurValue";
	public final static String Data_Result="Result";
	public final static String Data_ProgressMsg="ProgressMsg";
}
