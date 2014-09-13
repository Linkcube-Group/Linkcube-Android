package me.linkcube.client;

import com.oplibs.syncore.SyncMgr;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

/*
 * 
 * 游戏接收
 * 
 */

public class GameBroadcastReceiver extends BroadcastReceiver{
	
	public Activity curActivity = null;
	
	public IntentFilter connectionFilter = new IntentFilter();
	public void Init(Activity activity)
	{
		connectionFilter.addAction(LinkCubeIntents.LinkTimeout);
        connectionFilter.addAction(LinkCubeIntents.LinkAccept);
        connectionFilter.addAction(LinkCubeIntents.LinkRejected);
        connectionFilter.addAction(LinkCubeIntents.LinkErrorCode);
        connectionFilter.addAction(LinkCubeIntents.LinkLocalConfirm);
        connectionFilter.addAction(LinkCubeIntents.LinkDisconnect);
        curActivity = activity;
	}
	public void EnableListener()
	{
		curActivity.registerReceiver(this, connectionFilter);
	}
	
	public void DisableListener()
	{
		curActivity.unregisterReceiver(this);
	}
	
	public void ProcessConfirmation(int resultCode, Intent data)
	{
		switch (resultCode) 
		{ //resultCode为回传的标记，我在B中回传的是RESULT_OK
		case LinkDefines.RES_Yes:
			{
				SyncMgr syncmgr = SyncMgr.GetInstance();
				GameMgr gamemgr=GameMgr.GetInstance(curActivity);
				gamemgr.ProcessUserConfirmation(resultCode);
				syncmgr.AddFriend(GameMgr.connectedJID);
				Toast.makeText(curActivity, "与对方建立连接！", Toast.LENGTH_SHORT).show();
			}
			break;
		case LinkDefines.RES_No:
			{
				GameMgr gamemgr=GameMgr.GetInstance(curActivity);
				gamemgr.ProcessUserConfirmation(resultCode);
				Toast.makeText(curActivity, "已拒绝与对方游戏！", Toast.LENGTH_SHORT).show();
			}
			break;
			default:
				break;
		}
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals(LinkCubeIntents.LinkTimeout))
        {
			//Toast.makeText(FriendInfoActivity.this, "对方无应答！", Toast.LENGTH_SHORT).show();
			ProgressDialog.stopProgressDialog();
			ProgressDialog.startProgressDialog(curActivity,"对方无应答！","断开");
        }
		else if(intent.getAction().equals(LinkCubeIntents.LinkRejected))
		{
			//Toast.makeText(FriendInfoActivity.this, "对方已拒绝！", Toast.LENGTH_SHORT).show();
			ProgressDialog.stopProgressDialog();
			ProgressDialog.startProgressDialog(curActivity,"对方已拒绝！","断开");
		}
		else if(intent.getAction().equals(LinkCubeIntents.LinkAccept))
		{
			ProgressDialog.stopProgressDialog();
			Toast.makeText(curActivity, "对方接受了你的请求，可以开始互动了", Toast.LENGTH_SHORT).show();
		}
		else if(intent.getAction().equals(LinkCubeIntents.LinkErrorCode))
		{
			ProgressDialog.stopProgressDialog();
			Toast.makeText(curActivity, "密码错误！", Toast.LENGTH_SHORT).show();
			ProgressDialog.startProgressDialog(curActivity,"密码错误！","断开");
		}
		else if(intent.getAction().equals(LinkCubeIntents.LinkLocalConfirm))
		{
			Bundle extras = intent.getExtras();
			String nick = extras.getString(LinkDefines.Data_Nickname);
			
			Intent intent2send = new Intent (curActivity,ConfirmationDlgActivity.class);	
			intent2send.putExtra(LinkDefines.Data_Nickname, nick);
			intent2send.putExtra(LinkDefines.Data_Result, "想要与您连通！");
			curActivity.startActivityForResult(intent2send, LinkDefines.REQ_LocalConfirmation);
		}
		else if(intent.getAction().equals(LinkCubeIntents.LinkDisconnect))
		{
			Bundle extras = intent.getExtras();
			String nick = extras.getString(LinkDefines.Data_Nickname);
			
			Intent intent2send = new Intent (curActivity,DisconnectDlgActivity.class);	
			intent2send.putExtra(LinkDefines.Data_Nickname, nick);
			curActivity.startActivityForResult(intent2send, LinkDefines.REQ_ShowInfo);
		}
	}

}
