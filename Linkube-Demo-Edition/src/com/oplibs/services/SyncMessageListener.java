package com.oplibs.services;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import com.oplibs.message.MessageManager;
import com.oplibs.message.TextMessage;
import com.oplibs.support.Intents;
import com.oplibs.syncore.SyncMgr;

import android.util.Log;

/*
 * 
 * 同步消息监听
 * 
 */
public class SyncMessageListener implements PacketListener
{	
	@Override
	public void processPacket(Packet packet)
	{
		Message message = (Message)packet;

		TextMessage text = new TextMessage(message);
		SyncMgr syncmgr = SyncMgr.GetInstance();
		
		MessageManager mm = MessageManager.GetInstance();
		mm.AddMessage(text);
		syncmgr.BroadcaseIntent(Intents.MessageReceived);
		
		Log.e("Get Message:",message.getFrom());
	}
}
