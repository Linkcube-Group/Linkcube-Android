package com.oplibs.services;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import com.oplibs.support.Intents;
import com.oplibs.syncore.SyncMgr;

import android.util.Log;

/*
 * 同步Presence监听
 * 
 * 
 */
public class SyncPresenceListener  implements PacketListener
{

	@Override
	public void processPacket(Packet packet)
	{
		SyncMgr syncmgr = SyncMgr.GetInstance();
		Presence presence = (Presence) packet;
		String from = presence.getFrom();
		if(from==null)
		{
			from = "null";
		}
		String to = presence.getTo();
		if(to==null)
		{
			to = "null";
		}
        //String myRoomJID = room + "/" + nickname;
       // isUserStatusModification = presence.getFrom().equals(myRoomJID);
        if(presence.getType() == Presence.Type.subscribe)
        {
        	Log.e("Get Presence:","From "+from+" to "+presence.getTo()+" subscribe");
        	Presence presencefb = new Presence(Presence.Type.subscribed);//同意是
        	presencefb.setTo(from);//接收方jid
        	presencefb.setFrom(to);//发送方jid
        	//connection.sendPacket(presence);//connection是你自己的XMPPConnection链接
        	//syncmgr.BroadcaseIntent(Intents.PresenceReceived);
        	syncmgr.ProcessSubscribe(1, from, to);
        }
        else if(presence.getType() == Presence.Type.subscribed)
        {
        	Log.e("Get Presence:","From "+from+" to "+presence.getTo()+" subscribed");
        	Presence presencefb = new Presence(Presence.Type.subscribed);//同意是
        	presencefb.setTo(from);//接收方jid
        	presencefb.setFrom(to);//发送方jid
        	//connection.sendPacket(presence);//connection是你自己的XMPPConnection链接
        	syncmgr.BroadcaseIntent(Intents.PresenceReceived);
        }
        else if(presence.getType() == Presence.Type.unsubscribe)
        {
        	Log.e("Get Presence:","From "+from+" to "+presence.getTo()+" unsubscribe");
        	syncmgr.BroadcaseIntent(Intents.PresenceReceived);
        }
		return;
	}

}
