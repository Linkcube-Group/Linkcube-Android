package me.linkcube.client;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.linkcube.toy.IToyServiceCall;
import me.linkcube.toy.ParserUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import com.oplibs.services.IOnChatServiceCall;
import com.oplibs.syncore.SyncMgr;

/*
 * 
 * 
 * 游戏消息类
 * 
 */


public class GameMgr {
	private static GameMgr gamemgr;
	private IOnChatServiceCall chatServiceCall;
	private IToyServiceCall toyServiceCall;
	private Context execContext; 

	//Definition for controlling local states.
	enum UserState
	{
		us_idle,
		us_waiting_remote,
		us_waiting_local,
		us_broadcasting,
		us_listening
	};
	
	class StateTimerTask extends TimerTask{  
	    @Override
		public void run()
	    {
	    	synchronized (userState)
	    	{
		    	switch(userState)
		    	{
		    	case us_waiting_remote:
		    		setUserState(UserState.us_idle,null);
		    		Log.e("GameMgr:", "Connect to remote timeout!");
		    		BroadcastIntent(LinkCubeIntents.LinkTimeout,null);
		    		//Toast.makeText(execContext, "用户无应答！", Toast.LENGTH_SHORT).show();
		    		break;
		    	case us_waiting_local:
		    		break;
	    		default:
	    			break;
		    	}
	    	}
	    }  
	};
	
	public boolean IsRemoted()
	{
		return (userState==UserState.us_broadcasting||userState==UserState.us_listening);
	}
	
	public String GetRemoteJID()
	{
		return connectedJID;
	}

	public boolean IsConnectedByID(String jid)
	{
		if(userState==UserState.us_broadcasting||userState==UserState.us_listening)
		{
			String curid=connectedJID.split("@")[0];
			jid = jid.split("@")[0];
			if(curid.equals(jid))
			{
				return true;
			}
		}
		return false;
	}
	
	public void BroadcastIntent(String action,String usernick)
	{
		Intent intent = new Intent(action);
		if(usernick!=null)
		{
			intent.putExtra(LinkDefines.Data_Nickname, usernick);
		}
		this.execContext.sendBroadcast(intent);
	}
	
	private Timer stateTimer = null;
	private boolean startStateTimer()
	{
		if(stateTimer==null)
		{
			stateTimer = new Timer();
		}
		stateTimer.schedule(new StateTimerTask(), 30000);
		return true;
	}
	private boolean stopStateTimer()
	{
		if(stateTimer!=null)
		{
			stateTimer.cancel();
			stateTimer=null;
		}
		return true;
	}
	
	private static UserState userState = UserState.us_idle;
	public static String connectedJID = "";
	public static String reqJID = "";
	ArrayList<String> broadCastList=new ArrayList<String>();
	
	private IntentFilter cmdFilter = new IntentFilter();
	
	public UserState GetUserState()
	{
		return userState;
	}
	
	public static GameMgr GetInstance(Context ctx)
	{
		if (gamemgr == null)
		{
			gamemgr = new GameMgr();
		}
		
		gamemgr.execContext = ctx;
		return gamemgr;
	}
	
	//The following three functions will change the user state;
	public boolean askConnection(String jid,String mynick,String connectcode)
	{
		if(userState==UserState.us_idle)
		{
			chatServiceCall = LinkcubeApplication.getApp(execContext).chatServiceCall;
			try {
				Log.i("GameMgr:","Ask the remote connection");
				Log.e("AskConnection to:",jid);
				chatServiceCall.SendMessageToUser(jid, "c:"+mynick+":"+connectcode);
				stopStateTimer();
				setUserState(UserState.us_waiting_remote,jid);
				startStateTimer();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean disConnection(String jid,String mynick)
	{
		if(userState!=UserState.us_idle)
		{
			SyncMgr syncmgr = SyncMgr.GetInstance();
			chatServiceCall = LinkcubeApplication.getApp(execContext).chatServiceCall;
			toyServiceCall = LinkcubeApplication.getApp(execContext).toyServiceCall;
			try {
				Log.i("GameMgr:","Disconnect from the remote connection");
				chatServiceCall.SendMessageToUser(jid, "d:"+mynick);
				toyServiceCall.EnableToyRemoteMode(false, "");
				setUserState(UserState.us_idle,null);
				syncmgr.EnableRemoted(false, "");
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean ProcessUserConfirmation(int resultCode)
	{
		synchronized (userState)
		{
			switch(resultCode)
			{
			case LinkDefines.RES_No:
				{
					SyncMgr syncmgr = SyncMgr.GetInstance();
					toyServiceCall = LinkcubeApplication.getApp(execContext).toyServiceCall;
					
					try {
						syncmgr.SendMessageToUser(reqJID, "n:"+syncmgr.signedUser.NickName);
						setUserState(UserState.us_idle,null);
						syncmgr.EnableRemoted(false, null);
						toyServiceCall.EnableToyRemoteMode(false, null);
						//Toast.makeText(execContext, "与对方建立连接！", Toast.LENGTH_SHORT).show();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return false;
					}
				//Toast.makeText(execContext, "对方拒绝了您的请求！", Toast.LENGTH_SHORT).show();
				//BroadcastIntent(LinkCubeIntents.LinkRejected,null);
				}
				break;
			case LinkDefines.RES_Yes:
			{
				SyncMgr syncmgr = SyncMgr.GetInstance();
				toyServiceCall = LinkcubeApplication.getApp(execContext).toyServiceCall;
				try {
					setUserState(UserState.us_broadcasting,null);
					syncmgr.EnableRemoted(true, connectedJID);
					toyServiceCall.EnableToyRemoteMode(true, connectedJID);
					
					syncmgr.SendMessageToUser(connectedJID, "y:"+syncmgr.signedUser.JID);
					BroadcastIntent(LinkCubeIntents.LinkAccept,null);
					//Toast.makeText(execContext, "与对方建立连接！", Toast.LENGTH_SHORT).show();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
				break;
			}
		}
		return true;
	}
	
	private boolean setUserState(UserState state, String jid)
	{
		
		{switch(state)
		{
		case us_idle:
			userState = UserState.us_idle;
			reqJID = "";
			connectedJID="";
			Log.i("GameState:Idle-",reqJID+" "+connectedJID);
			break;
		case us_waiting_remote:
			reqJID = jid;
			connectedJID = "";
			userState=UserState.us_waiting_remote;
			Log.i("GameState:wait-remote-",reqJID+" "+connectedJID);
			break;
		case us_waiting_local:
			reqJID=jid;
			connectedJID = "";
			userState = UserState.us_waiting_local;
			Log.i("GameState:wait-local-",reqJID+" "+connectedJID);
			break;
		case us_broadcasting:
			connectedJID = reqJID;
			reqJID="";
			userState=UserState.us_broadcasting;
			Log.i("GameState:broadcasting-",reqJID+" "+connectedJID);
			break;
		case us_listening:
			connectedJID = reqJID;
			reqJID="";
			userState=UserState.us_listening;
			Log.i("GameState:listening-",reqJID+" "+connectedJID);
			break;
		}
		}
		return true;
	}
	private boolean processCmd(String jid,String cmdstring)
	{
		String[] cmddata = cmdstring.split(":");
		Log.i("Gamemgr ProcessCMD:", cmdstring);
		if(cmddata.length<=0)
		{
			return false;
		}
		char cmd = cmddata[0].charAt(0);

		{
			switch(userState)
			{
			case us_idle:
				if(cmd=='c'&&cmddata.length==3)
				{
					SyncMgr syncmgr = SyncMgr.GetInstance();
					//UserRoster friend = syncmgr.GetFriendRoster(jid);
					String localconcode = syncmgr.signedUser.connectCode;
					if(localconcode.equals(cmddata[2]))
					{
						String[] cleanjids = jid.split("/");
	
						setUserState(UserState.us_waiting_local,cleanjids[0]);
						//waitingTask.state = UserState.us_waiting_local;
						/*
						Intent intent2send = new Intent (execContext,InfoDlgActivity.class);	
						intent2send.putExtra(LinkDefines.Data_Nickname, cmddata[1]+"");
						intent2send.putExtra(LinkDefines.Data_Result, "想要与您连通！");
						((Activity) execContext).startActivityForResult(intent2send, LinkDefines.REQ_ShowInfo);
						*/
						BroadcastIntent(LinkCubeIntents.LinkLocalConfirm,cmddata[1]);
					}
					else
					{
						chatServiceCall = LinkcubeApplication.getApp(execContext).chatServiceCall;
						try {
							chatServiceCall.SendMessageToUser(jid, "e:"+"errorcode");
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				break;
			case us_waiting_remote:
			{
				if(cmd=='y'&&cmddata.length==2)
				{
					SyncMgr syncmgr = SyncMgr.GetInstance();
					toyServiceCall = LinkcubeApplication.getApp(execContext).toyServiceCall;
					String[] cleanjids = jid.split("@");
					if(reqJID.equals(cleanjids[0]))
					{
						try {
							syncmgr.EnableRemoted(true, reqJID);
							toyServiceCall.EnableToyRemoteMode(true, reqJID);
							setUserState(UserState.us_broadcasting,null);
							BroadcastIntent(LinkCubeIntents.LinkAccept,null);
							//syncmgr.SendMessageToUser(connectedJID, "y:"+syncmgr.signedUser.UserID);
						} catch (RemoteException e) {
					// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else if(cmd=='o'&&cmddata.length==2)
				{
					
				}
				else if(cmd=='n'&&cmddata.length==2)
				{
					try {
						toyServiceCall = LinkcubeApplication.getApp(execContext).toyServiceCall;
						toyServiceCall.EnableToyRemoteMode(false, connectedJID);
						setUserState(UserState.us_idle,null);
						BroadcastIntent(LinkCubeIntents.LinkRejected,null);
						//syncmgr.SendMessageToUser(connectedJID, "y:"+syncmgr.signedUser.UserID);
					} catch (RemoteException e) {
				// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(cmd=='e'&&cmddata.length==2)
				{
					try {
						toyServiceCall = LinkcubeApplication.getApp(execContext).toyServiceCall;
						toyServiceCall.EnableToyRemoteMode(false, connectedJID);
						setUserState(UserState.us_idle,null);
						BroadcastIntent(LinkCubeIntents.LinkErrorCode,null);
						//syncmgr.SendMessageToUser(connectedJID, "y:"+syncmgr.signedUser.UserID);
					} catch (RemoteException e) {
				// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
				break;
			case us_broadcasting:
				if(cmd=='d'&&cmddata.length==2)
				{
					SyncMgr syncmgr = SyncMgr.GetInstance();
					try {
	
						setUserState(UserState.us_idle,null);
						toyServiceCall = LinkcubeApplication.getApp(execContext).toyServiceCall;
						syncmgr.EnableRemoted(false, "");
						toyServiceCall.EnableToyRemoteMode(false, "");
						BroadcastIntent(LinkCubeIntents.LinkDisconnect,cmddata[1]);
					} catch (RemoteException e) {
					// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(cmd=='c'&&cmddata.length==3)
				{
					
				}
				break;
				default:
					break;
			}
		}
		return false;
	}
	
	private BroadcastReceiver cmdBR=  new BroadcastReceiver()
	{
    	@Override
    	public void onReceive(Context context, Intent intent) {
			Bundle bundle=intent.getExtras();  //data为B中回传的Intent
			String fromjid=bundle.getString(LinkDefines.Data_From);
			String cmdbuf=bundle.getString(LinkDefines.Data_CMD);
			if(fromjid!=null&&cmdbuf!=null)
			{
				String []cmds = ParserUtil.GetCmds(cmdbuf);
				if(cmds.length>0)
				{
					for(int nc=0;nc<cmds.length;nc++)
					{
						processCmd(fromjid, cmds[nc]);
					}
				}
			}
    	}
    };

	private boolean couldStartUICMDListener()
	{
		return true;
	}
	private boolean couldStopUICMDListener()
	{
		return true;
	}
	public boolean StartUICMDListener()
	{
		if(couldStartUICMDListener())
		{
			cmdFilter.addAction(com.oplibs.support.Intents.MessageReceived);
			execContext.registerReceiver(cmdBR,cmdFilter);
			return true;
		}
		return false;
	}
	
	public boolean StopUICMDListener()
	{
		if(couldStopUICMDListener())
		{
			execContext.unregisterReceiver(cmdBR);
			return true;
		}
		return false;
	}


}
