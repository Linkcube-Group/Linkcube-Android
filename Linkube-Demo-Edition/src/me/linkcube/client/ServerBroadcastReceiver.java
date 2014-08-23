package me.linkcube.client;

import com.oplibs.services.XMPPUtils;
import com.oplibs.support.Intents;
import com.oplibs.syncore.SyncMgr;
import com.oplibs.ui.WaitingHandler;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;


/*
 * 
 * 连接服务器相关
 * 
 * 
 */

public class ServerBroadcastReceiver extends BroadcastReceiver{
	
	public Activity curActivity = null;
	public static WaitingHandler waitingDlg = new WaitingHandler();
	
	public IntentFilter serverFilter = new IntentFilter();
	public void Init(Activity activity)
	{
		serverFilter.addAction(Intents.ConnectFailed);
		serverFilter.addAction(Intents.LoggedIn);
		serverFilter.addAction(Intents.LoggedInFailed);
        curActivity = activity;
	}
	public void EnableListener()
	{
		curActivity.registerReceiver(this, serverFilter);
	}
	
	public void DisableListener()
	{
		curActivity.unregisterReceiver(this);
	}
	
	public static boolean askLoginOrCreate(Activity activity, int reqcode)
	{
		Intent intent = new Intent (activity,LoginOrCreateActivity.class);
		activity.startActivityForResult(intent, reqcode);
		return false;
	}
	public static boolean startCreateAccount(Activity activity, int reqcode)
	{
		Intent intent = new Intent (activity,CreateAccountActivity.class);
		activity.startActivityForResult(intent, reqcode);
		return false;
	}
	public static boolean startLoginAccount(Activity activity, int reqcode)
	{
		Intent intent = new Intent (activity,LoginActivity.class);
		activity.startActivityForResult(intent, reqcode);
		//waitingDlg.StartProgressDialog(activity,"login","login!");
		return false;
	}
	public static int NextUI=-1;
	
	public static void Login2Server(Activity activity,Intent intent)
	{
		SyncMgr syncmgr = SyncMgr.GetInstance();
		if(intent!=null)
		{
			Bundle b=intent.getExtras();  //data为B中回传的Intent

			String jid=b.getString(LinkDefines.Data_JID);
			String password=b.getString(LinkDefines.Data_Password);
			if((jid!=null)&&(password!=null))
			{
				waitingDlg.StartProgressDialog(activity, "请等待", "登录中……");
				try {
					syncmgr.Login(XMPPUtils.GetJID(jid), password);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				
			}
		}
		else
		{
			//Toast.makeText(MainTabActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals(Intents.ConnectFailed))
        {
			waitingDlg.StopProgressDialog();
			//User user = User.GetUser();
			//user.SendMessageToUser("test2", "msg");
			Toast.makeText(curActivity, "连接服务器失败！", Toast.LENGTH_SHORT).show();
			NextUI = -1;
			return;
        }
		else if(intent.getAction().equals(Intents.LoggedIn))
        {
			waitingDlg.StopProgressDialog();
			Toast.makeText(curActivity, "登录成功！", Toast.LENGTH_SHORT).show();
			if(NextUI>0)
			{
				if(NextUI==1)
				{
					Intent intent1 = new Intent (curActivity,PersonalActivity.class);			
					curActivity.startActivity(intent1);	
					NextUI = -1;
				}
				else if(NextUI==2)
				{
					Intent intent1 = new Intent (curActivity,FriendsActivity.class);			
					curActivity.startActivity(intent1);	
					NextUI = -1;
				}
			}
			return;
        }
		else if(intent.getAction().equals(Intents.LoggedInFailed))
        {
			waitingDlg.StopProgressDialog();
			Toast.makeText(curActivity, "登录失败！", Toast.LENGTH_SHORT).show();
			if(NextUI>0)
			{
				NextUI = -1;
			}
			SyncMgr syncmgr  = SyncMgr.GetInstance();
			syncmgr.LogoutServer();
			return;
        }
	}

}
