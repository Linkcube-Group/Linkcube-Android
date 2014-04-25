package me.linkcube.app.sync.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
/**
 * 好友申请的广播接收
 * @author Rodriguez-xin
 *
 */
public class AddFriendReceiver extends BroadcastReceiver {

	public ASmackRequestCallBack addFriendCallBack;
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle=intent.getExtras();
		String friendName=bundle.getString("friendName");
		Message msg=new Message();
		msg.obj=friendName;
		//handler.sendMessage(msg);
		addFriendCallBack.responseSuccess(msg.obj);
	}
	
	public void setAddFriendCallBack(ASmackRequestCallBack addFriendCallBack) {
		this.addFriendCallBack = addFriendCallBack;
	}

}
