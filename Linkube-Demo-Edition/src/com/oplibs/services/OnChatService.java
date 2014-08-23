package com.oplibs.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/*
 * 
 * 聊天服务
 * 
 */

public class OnChatService extends Service
{
	private IBinder binder;
	
	@Override
	public void onCreate() 
	{		
		binder = new OnChatServiceCallImp(this);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return binder;
	}
	
	@Override
	public void onDestroy() 
	{
		
	}
}
